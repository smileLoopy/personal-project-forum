package com.personal.projectforum.service;

import com.personal.projectforum.domain.type.SearchType;
import com.personal.projectforum.dto.PostingDto;
import com.personal.projectforum.dto.PostingUpdateDto;
import com.personal.projectforum.repository.PostingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class PostingService {

    private final PostingRepository postingRepository;

    @Transactional(readOnly = true)
    public Page<PostingDto> searchPostings(SearchType searchType, String searchKeyword) {
        return Page.empty();
    }

    @Transactional(readOnly = true)
    public PostingDto searchPosting(long l) {
        return  null;
    }

    public void savePosting(PostingDto dto) {
    }

    public void updatePosting(long postingId, PostingUpdateDto dto) {
    }

    public void deletePosting(long postingId) {
    }
}
