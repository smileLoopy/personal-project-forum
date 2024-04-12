package com.personal.projectforum.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.personal.projectforum.domain.Posting}
 */
public record PostingDto(
        LocalDateTime createdAt,
        String createdBy,
        String title,
        String content,
        String hashtag
) {

  public static PostingDto of(LocalDateTime createdAt, String createdBy, String title, String content, String hashtag) {
    return new PostingDto(createdAt, createdBy, title, content, hashtag);
  }
}