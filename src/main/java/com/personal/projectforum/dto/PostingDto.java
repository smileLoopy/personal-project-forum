package com.personal.projectforum.dto;

import com.personal.projectforum.domain.Posting;
import com.personal.projectforum.domain.UserAccount;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * DTO for {@link com.personal.projectforum.domain.Posting}
 */
public record PostingDto(
        Long id,
        UserAccountDto userAccountDto,
        String title,
        String content,
        Set<HashtagDto> hashtagDtos,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {

  public static PostingDto of(UserAccountDto userAccountDto, String title, String content,  Set<HashtagDto> hashtagDtos) {
    return new PostingDto(null, userAccountDto, title, content, hashtagDtos, null, null, null, null);
  }

  public static PostingDto of(Long id, UserAccountDto userAccountDto, String title, String content, Set<HashtagDto> hashtagDtos, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
    return new PostingDto(id, userAccountDto, title, content, hashtagDtos, createdAt, createdBy, modifiedAt, modifiedBy);
  }

  public static PostingDto from(Posting entity) {
    return new PostingDto(
            entity.getId(),
            UserAccountDto.from(entity.getUserAccount()),
            entity.getTitle(),
            entity.getContent(),
            entity.getHashtags().stream()
                    .map(HashtagDto::from)
                    .collect(Collectors.toUnmodifiableSet())
            ,
            entity.getCreatedAt(),
            entity.getCreatedBy(),
            entity.getModifiedAt(),
            entity.getModifiedBy()
    );
  }

  public Posting toEntity(UserAccount userAccount) {
    return Posting.of(
            userAccount,
            title,
            content
    );
  }
}