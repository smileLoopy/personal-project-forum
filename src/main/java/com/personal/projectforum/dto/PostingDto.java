package com.personal.projectforum.dto;

import com.personal.projectforum.domain.Posting;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.personal.projectforum.domain.Posting}
 */
public record PostingDto(
        Long id,
        UserAccountDto userAccountDto,
        String title,
        String content,
        String hashtag,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {

  public static PostingDto of(Long id, UserAccountDto userAccountDto, String title, String content, String hashtag, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
    return new PostingDto(id, userAccountDto, title, content, hashtag, createdAt, createdBy, modifiedAt, modifiedBy);
  }

  public static PostingDto from(Posting entity) {
    return new PostingDto(
            entity.getId(),
            UserAccountDto.from(entity.getUserAccount()),
            entity.getTitle(),
            entity.getContent(),
            entity.getHashtag(),
            entity.getCreatedAt(),
            entity.getCreatedBy(),
            entity.getModifiedAt(),
            entity.getModifiedBy()
    );
  }

  public Posting toEntity() {
    return Posting.of(
            userAccountDto.toEntity(),
            title,
            content,
            hashtag
    );
  }
}