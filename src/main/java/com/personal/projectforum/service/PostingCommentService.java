package com.personal.projectforum.service;

import com.personal.projectforum.dto.PostingCommentDto;
import com.personal.projectforum.repository.PostingCommentRepository;
import com.personal.projectforum.repository.PostingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class PostingCommentService {

    private final PostingRepository postingRepository;
    private  final PostingCommentRepository postingCommentRepository;

    @Transactional(readOnly = true)
    public List<PostingCommentDto> searchPostingComment(Long postingId) {
        return List.of();
    }

    public void savePostingComment(PostingCommentDto dto) {
    }
}
