package com.personal.projectforum.dto;

import com.personal.projectforum.domain.Posting;
import com.personal.projectforum.domain.PostingComment;
import com.personal.projectforum.domain.UserAccount;

import java.time.LocalDateTime;

/**
 * DTO for {@link com.personal.projectforum.domain.PostingComment}
 */
public record PostingCommentDto(
        Long id,
        Long postingId,
        UserAccountDto userAccountDto,
        Long parentCommentId,
        String content,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {

  public static PostingCommentDto of(Long postingId, UserAccountDto userAccountDto, String content) {
    return PostingCommentDto.of(postingId, userAccountDto, null, content);
  }

  public static PostingCommentDto of(Long postingId, UserAccountDto userAccountDto, Long parentCommentId, String content) {
    return PostingCommentDto.of(null, postingId, userAccountDto, parentCommentId, content, null, null, null, null);
  }

  public static PostingCommentDto of(Long id, Long postingId, UserAccountDto userAccountDto, Long parentCommentId, String content, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
    return new PostingCommentDto(id, postingId, userAccountDto, parentCommentId, content, createdAt, createdBy, modifiedAt, modifiedBy);
  }

  public static PostingCommentDto from(PostingComment entity) {
    return new PostingCommentDto(
            entity.getId(),
            entity.getPosting().getId(),
            UserAccountDto.from(entity.getUserAccount()),
            entity.getParentCommentId(),
            entity.getContent(),
            entity.getCreatedAt(),
            entity.getCreatedBy(),
            entity.getModifiedAt(),
            entity.getModifiedBy()
    );
  }

  public PostingComment toEntity(Posting posting, UserAccount userAccount) {
    return PostingComment.of(
            posting,
            userAccount,
            content
    );
  }
}