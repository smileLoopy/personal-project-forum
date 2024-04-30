package com.personal.projectforum.dto;

import com.personal.projectforum.domain.Hashtag;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public record HashtagWithPostingsDto(
        Long id,
        Set<PostingDto> postings,
        String hashtagName,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {

  public static HashtagWithPostingsDto of(Set<PostingDto> postings, String hashtagName) {
    return new HashtagWithPostingsDto(null, postings, hashtagName, null, null, null, null);
  }

  public static HashtagWithPostingsDto of(Long id, Set<PostingDto> postings, String hashtagName, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
    return new HashtagWithPostingsDto(id, postings, hashtagName, createdAt, createdBy, modifiedAt, modifiedBy);
  }

  public static HashtagWithPostingsDto from(Hashtag entity) {
    return new HashtagWithPostingsDto(
            entity.getId(),
            entity.getPostings().stream()
                    .map(PostingDto::from)
                    .collect(Collectors.toUnmodifiableSet())
            ,
            entity.getHashtagName(),
            entity.getCreatedAt(),
            entity.getCreatedBy(),
            entity.getModifiedAt(),
            entity.getModifiedBy()
    );
  }

  public Hashtag toEntity() {
    return Hashtag.of(hashtagName);
  }

}