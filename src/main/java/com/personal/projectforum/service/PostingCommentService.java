package com.personal.projectforum.service;

import com.personal.projectforum.domain.Posting;
import com.personal.projectforum.domain.PostingComment;
import com.personal.projectforum.domain.UserAccount;
import com.personal.projectforum.dto.PostingCommentDto;
import com.personal.projectforum.repository.PostingCommentRepository;
import com.personal.projectforum.repository.PostingRepository;
import com.personal.projectforum.repository.UserAccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class PostingCommentService {

    private final PostingRepository postingRepository;
    private  final PostingCommentRepository postingCommentRepository;
    private final UserAccountRepository userAccountRepository;

    @Transactional(readOnly = true)
    public List<PostingCommentDto> searchPostingComments(Long postingId) {

        return postingCommentRepository.findByPosting_Id(postingId)
                .stream()
                .map(PostingCommentDto::from)
                .toList();
    }

    public void savePostingComment(PostingCommentDto dto) {
        try {
            Posting posting = postingRepository.getReferenceById(dto.postingId());
            UserAccount userAccount = userAccountRepository.getReferenceById(dto.userAccountDto().userId());
            postingCommentRepository.save(dto.toEntity(posting, userAccount));
        } catch (EntityNotFoundException e) {
            log.warn("Failed to save comment. Can not info for creating comment - : {}", e.getLocalizedMessage());
        }
    }

    public void updatePostingComment(PostingCommentDto dto) {
        try {
            PostingComment postingComment = postingCommentRepository.getReferenceById(dto.id());
            if (dto.content() != null) { postingComment.setContent(dto.content()); }
        } catch (EntityNotFoundException e) {
            log.warn("Failed to update comment. Can not find the comment - dto: {}", dto);
        }
    }

    public void deletePostingComment(Long postingCommentId) {
        postingCommentRepository.deleteById(postingCommentId);
    }
}
