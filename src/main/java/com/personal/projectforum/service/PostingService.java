package com.personal.projectforum.service;

import com.personal.projectforum.domain.type.SearchType;
import com.personal.projectforum.dto.PostingDto;
import com.personal.projectforum.dto.PostingWithCommentsDto;
import com.personal.projectforum.repository.PostingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class PostingService {

    private final PostingRepository postingRepository;

    @Transactional(readOnly = true)
    public Page<PostingDto> searchPostings(SearchType searchType, String searchKeyword, Pageable pageable) {
        return Page.empty();
    }

    @Transactional(readOnly = true)
    public PostingWithCommentsDto getPosting(Long postingId) {
        return null;
    }

    public void savePosting(PostingDto dto) {
    }

    public void updatePosting(PostingDto dto) {
    }

    public void deletePosting(long postingId) {
    }
}
