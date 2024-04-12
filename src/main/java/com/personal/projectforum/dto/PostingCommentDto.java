package com.personal.projectforum.dto;

import java.time.LocalDateTime;

/**
 * DTO for {@link com.personal.projectforum.domain.PostingComment}
 */
public record PostingCommentDto(
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy,
        String content
) {

  public static PostingCommentDto of(LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy, String content) {
    return new PostingCommentDto(createdAt, createdBy, modifiedAt, modifiedBy, content);
  }
}