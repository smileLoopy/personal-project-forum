package com.personal.projectforum.response;

import com.personal.projectforum.domain.Posting;
import com.personal.projectforum.domain.PostingComment;
import com.personal.projectforum.dto.PostingCommentDto;
import com.personal.projectforum.dto.UserAccountDto;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link PostingComment}
 */
public record PostingCommentResponse(
        Long id,
        String content,
        LocalDateTime createdAt,
        String email,
        String nickname,
        String userId
) {

  public static PostingCommentResponse of(Long id, String content, LocalDateTime createdAt, String email, String nickname, String userId) {
    return new PostingCommentResponse(id, content, createdAt, email, nickname, userId);
  }

  public static PostingCommentResponse from(PostingCommentDto dto) {
    String nickname = dto.userAccountDto().nickname();
    if (nickname == null || nickname.isBlank()) {
      nickname = dto.userAccountDto().userId();
    }

    return new PostingCommentResponse(
            dto.id(),
            dto.content(),
            dto.createdAt(),
            dto.userAccountDto().email(),
            nickname,
            dto.userAccountDto().userId()
    );
  }
}