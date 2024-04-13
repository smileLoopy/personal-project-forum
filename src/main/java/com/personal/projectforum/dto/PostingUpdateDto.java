package com.personal.projectforum.dto;

import java.io.Serializable;

/**
 * DTO for {@link com.personal.projectforum.domain.Posting}
 */
public record PostingUpdateDto(
        String title,
        String content,
        String hashtag
) {

  public static PostingUpdateDto of(String title, String content, String hashtag) {
    return new PostingUpdateDto(title, content, hashtag);
  }
}