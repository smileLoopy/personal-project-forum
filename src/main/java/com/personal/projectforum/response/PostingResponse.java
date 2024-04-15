package com.personal.projectforum.response;

import com.personal.projectforum.domain.PostingComment;
import com.personal.projectforum.dto.PostingCommentDto;
import com.personal.projectforum.dto.PostingDto;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link PostingComment}
 */
public record PostingResponse(
        Long id,
        String title,
        String content,
        String hashtag,
        LocalDateTime createdAt,
        String email,
        String nickname
) {

  public static PostingResponse of(Long id, String title, String content, String hashtag, LocalDateTime createdAt, String email, String nickname) {
    return new PostingResponse(id, title, content, hashtag, createdAt, email, nickname);
  }

  public static PostingResponse from(PostingDto dto) {
    String nickname = dto.userAccountDto().nickname();
    if (nickname == null || nickname.isBlank()) {
      nickname = dto.userAccountDto().userId();
    }

    return new PostingResponse(
            dto.id(),
            dto.title(),
            dto.content(),
            dto.hashtag(),
            dto.createdAt(),
            dto.userAccountDto().email(),
            nickname
    );
  }
}