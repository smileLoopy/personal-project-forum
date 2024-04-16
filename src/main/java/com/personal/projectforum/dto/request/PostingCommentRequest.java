package com.personal.projectforum.dto.request;

import com.personal.projectforum.dto.PostingCommentDto;
import com.personal.projectforum.dto.UserAccountDto;

/**
 * DTO for {@link com.personal.projectforum.domain.PostingComment}
 */
public record PostingCommentRequest(Long postingId, String content) {

    public static PostingCommentRequest of(Long articleId, String content) {
        return new PostingCommentRequest(articleId, content);
    }

    public PostingCommentDto toDto(UserAccountDto userAccountDto) {
        return PostingCommentDto.of(
                postingId,
                userAccountDto,
                content
        );
    }
}