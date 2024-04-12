package com.personal.projectforum.dto;

import com.personal.projectforum.domain.Posting;
import com.personal.projectforum.domain.PostingComment;

import java.time.LocalDateTime;

/**
 * DTO for {@link com.personal.projectforum.domain.PostingComment}
 */
public record PostingCommentDto(
        Long id,
        Long postingId,
        UserAccountDto userAccountDto,
        String content,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {

  public static PostingCommentDto of(Long id, Long articleId, UserAccountDto userAccountDto, String content, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
    return new PostingCommentDto(id, articleId, userAccountDto, content, createdAt, createdBy, modifiedAt, modifiedBy);
  }

  public static PostingCommentDto from(PostingComment entity) {
    return new PostingCommentDto(
            entity.getId(),
            entity.getPosting().getId(),
            UserAccountDto.from(entity.getUserAccount()),
            entity.getContent(),
            entity.getCreatedAt(),
            entity.getCreatedBy(),
            entity.getModifiedAt(),
            entity.getModifiedBy()
    );
  }

  public PostingComment toEntity(Posting entity) {
    return PostingComment.of(
            entity,
            userAccountDto.toEntity(),
            content
    );
  }
}