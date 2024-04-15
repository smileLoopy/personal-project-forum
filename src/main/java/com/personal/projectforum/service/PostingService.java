package com.personal.projectforum.service;

import com.personal.projectforum.domain.Posting;
import com.personal.projectforum.domain.UserAccount;
import com.personal.projectforum.domain.constant.SearchType;
import com.personal.projectforum.dto.PostingDto;
import com.personal.projectforum.dto.PostingWithCommentsDto;
import com.personal.projectforum.repository.PostingRepository;
import com.personal.projectforum.repository.UserAccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class PostingService {

    private final PostingRepository postingRepository;
    private final UserAccountRepository userAccountRepository;

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
            case HASHTAG -> postingRepository.findByHashtag("#" + searchKeyword, pageable).map(PostingDto::from);
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
                .orElseThrow(() -> new EntityNotFoundException("No posting - postingId: " + postingId));
    }

    public void savePosting(PostingDto dto) {
        UserAccount userAccount = userAccountRepository.getReferenceById(dto.userAccountDto().userId());
        postingRepository.save(dto.toEntity(userAccount));
    }

    public void updatePosting(Long postingId, PostingDto dto) {
        try {
            Posting posting = postingRepository.getReferenceById(postingId);
            if(dto.title() != null) { posting.setTitle(dto.title()); }
            if(dto.content() != null) { posting.setContent(dto.content()); }
            posting.setHashtag(dto.hashtag());
        } catch (EntityNotFoundException e) {
            log.warn("Posting Update Failed. Not possible to find the posting - dto: {}", dto);
        }
    }

    public void deletePosting(long postingId) {
        postingRepository.deleteById(postingId);
    }

    public long getPostingCount() {
        return postingRepository.count();
    }

    @Transactional(readOnly = true)
    public Page<PostingDto> searchPostingsViaHashtag(String hashtag, Pageable pageable) {
        if(hashtag == null || hashtag.isBlank()) {
            return Page.empty(pageable);
        }

        return postingRepository.findByHashtag(hashtag, pageable).map(PostingDto::from);
    }

    public List<String> getHashtags() {
        return postingRepository.findAllDistinctHashtags();
    }
}
