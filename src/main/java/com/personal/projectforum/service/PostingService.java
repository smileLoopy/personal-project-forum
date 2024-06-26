package com.personal.projectforum.service;

import com.personal.projectforum.domain.Hashtag;
import com.personal.projectforum.domain.Posting;
import com.personal.projectforum.domain.UserAccount;
import com.personal.projectforum.domain.constant.SearchType;
import com.personal.projectforum.dto.PostingDto;
import com.personal.projectforum.dto.PostingWithCommentsDto;
import com.personal.projectforum.repository.HashtagRepository;
import com.personal.projectforum.repository.PostingRepository;
import com.personal.projectforum.repository.UserAccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.querydsl.core.types.Projections.map;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class PostingService {

    private final HashtagService hashtagService;
    private final PostingRepository postingRepository;
    private final UserAccountRepository userAccountRepository;
    private final HashtagRepository hashtagRepository;

    @Transactional(readOnly = true)
    public Page<PostingDto> searchPostings(SearchType searchType, String searchKeyword, Pageable pageable) {
        if(searchKeyword == null || searchKeyword.isBlank()) {
            return postingRepository.findAll(pageable).map(PostingDto::from);
        }

        return switch (searchType) {
            case TITLE -> postingRepository.findByTitleContaining(searchKeyword, pageable).map(PostingDto::from);
            case CONTENT -> postingRepository.findByContentContaining(searchKeyword, pageable).map(PostingDto::from);
            case ID -> postingRepository.findByUserAccount_UserIdContaining(searchKeyword, pageable).map(PostingDto::from);
            case NICKNAME -> postingRepository.findByUserAccount_NicknameContaining(searchKeyword, pageable).map(PostingDto::from);
            case HASHTAG -> postingRepository.findByHashtagNames(
                            Arrays.stream(searchKeyword.split(" ")).toList(),
                            pageable
                    )
                    .map(PostingDto::from);
        };

    }

    @Transactional(readOnly = true)
    public PostingWithCommentsDto getPostingWithComments(Long postingId) {
        return postingRepository.findById(postingId)
                .map(PostingWithCommentsDto::from)
                .orElseThrow(() -> new EntityNotFoundException("Posting not exist - postingId: " + postingId));
    }

    @Transactional(readOnly = true)
    public PostingDto getPosting(Long postingId) {
        return postingRepository.findById(postingId)
                .map(PostingDto::from)
                .orElseThrow(() -> new EntityNotFoundException("Posting not exist - postingId: " + postingId));
    }

    public void savePosting(PostingDto dto) {
        UserAccount userAccount = userAccountRepository.getReferenceById(dto.userAccountDto().userId());
        Set<Hashtag> hashtags = renewHashtagsFromContent(dto.content());

        Posting posting = dto.toEntity(userAccount);
        posting.addHashtags(hashtags);
        postingRepository.save(posting);
    }

    public void updatePosting(Long postingId, PostingDto dto) {
        try {
            Posting posting = postingRepository.getReferenceById(postingId);
            UserAccount userAccount = userAccountRepository.getReferenceById(dto.userAccountDto().userId());

            if(posting.getUserAccount().getUserId().equals(userAccount.getUserId())) {
                if(dto.title() != null) { posting.setTitle(dto.title()); }
                if(dto.content() != null) { posting.setContent(dto.content()); }

                Set<Long> hashtagIds = posting.getHashtags().stream()
                        .map(Hashtag::getId)
                        .collect(Collectors.toUnmodifiableSet());
                posting.clearHashtags();
                postingRepository.flush();

                hashtagIds.forEach(hashtagService::deleteHashtagWithoutPostings);

                Set<Hashtag> hashtags = renewHashtagsFromContent(dto.content());
                posting.addHashtags(hashtags);
            }
        } catch (EntityNotFoundException e) {
            log.warn("Posting Update Failed. Can not find info for updating posting - {}", e.getLocalizedMessage());
        }
    }

    public void deletePosting(long postingId, String userId) {
        Posting posting = postingRepository.getReferenceById(postingId);

        Set<Long> hashtagIds = posting.getHashtags().stream()
                .map(Hashtag::getId)
                .collect(Collectors.toUnmodifiableSet());
        postingRepository.deleteByIdAndUserAccount_UserId(postingId, userId);
        postingRepository.flush();

        hashtagIds.forEach(hashtagService::deleteHashtagWithoutPostings);
    }

    public long getPostingCount() {
        return postingRepository.count();
    }

    @Transactional(readOnly = true)
    public Page<PostingDto> searchPostingsViaHashtag(String hashtagName, Pageable pageable) {
        if(hashtagName == null || hashtagName.isBlank()) {
            return Page.empty(pageable);
        }

        return postingRepository.findByHashtagNames(List.of(hashtagName), pageable).map(PostingDto::from);
    }

    public List<String> getHashtags() {
        return hashtagRepository.findAllHashtagNames(); // TODO: Let's think of moving it to HashtagService
    }

    private Set<Hashtag> renewHashtagsFromContent(String content) {
        Set<String> hashtagNamesInContent = hashtagService.parseHashtagNames(content);
        Set<Hashtag> hashtags = hashtagService.findHashtagsByNames(hashtagNamesInContent);
        Set<String> existingHashtagNames = hashtags.stream()
                .map(Hashtag::getHashtagName)
                .collect(Collectors.toUnmodifiableSet());

        hashtagNamesInContent.forEach(newHashtagName -> {
            if(!existingHashtagNames.contains(newHashtagName)) {
                hashtags.add(Hashtag.of(newHashtagName));
            }
        });

        return hashtags;
    }
}
