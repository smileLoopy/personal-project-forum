package com.personal.projectforum.dto.request;

import com.personal.projectforum.dto.PostingCommentDto;
import com.personal.projectforum.dto.UserAccountDto;

/**
 * DTO for {@link com.personal.projectforum.domain.PostingComment}
 */
public record PostingCommentRequest(
        Long postingId,
        Long parentCommentId,
        String content) {

    public static PostingCommentRequest of(Long articleId, String content) {
        return PostingCommentRequest.of(articleId, null, content);
    }

    public static PostingCommentRequest of(Long articleId, Long parentCommentId, String content) {
        return new PostingCommentRequest(articleId, parentCommentId, content);
    }

    public PostingCommentDto toDto(UserAccountDto userAccountDto) {
        return PostingCommentDto.of(
                postingId,
                userAccountDto,
                parentCommentId,
                content
        );
    }
}