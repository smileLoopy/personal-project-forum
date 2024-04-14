package com.personal.projectforum.service;

import com.personal.projectforum.domain.Posting;
import com.personal.projectforum.domain.type.SearchType;
import com.personal.projectforum.dto.PostingDto;
import com.personal.projectforum.dto.PostingWithCommentsDto;
import com.personal.projectforum.repository.PostingRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class PostingService {

    private final PostingRepository postingRepository;

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
    public PostingWithCommentsDto getPosting(Long postingId) {
        return postingRepository.findById(postingId)
                .map(PostingWithCommentsDto::from)
                .orElseThrow(() -> new EntityNotFoundException("Posting not exist - postingId: " + postingId));
    }

    public void savePosting(PostingDto dto) {
        postingRepository.save(dto.toEntity());
    }

    public void updatePosting(PostingDto dto) {
        try {
            Posting posting = postingRepository.getReferenceById(dto.id());
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
}
