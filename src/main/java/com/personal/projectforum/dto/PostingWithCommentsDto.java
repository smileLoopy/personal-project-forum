package com.personal.projectforum.dto;

import com.personal.projectforum.domain.Posting;
import com.personal.projectforum.domain.PostingComment;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.LinkedHashSet;

/**
 * DTO for {@link PostingComment}
 */
public record PostingWithCommentsDto(
        Long id,
        UserAccountDto userAccountDto,
        Set<PostingCommentDto> postingCommentDtos,
        String title,
        String content,
        Set<HashtagDto> hashtagDtos,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {

  public static PostingWithCommentsDto of(Long id, UserAccountDto userAccountDto, Set<PostingCommentDto> postingCommentDtos, String title, String content, Set<HashtagDto> hashtagDtos, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
    return new PostingWithCommentsDto(id, userAccountDto, postingCommentDtos, title, content, hashtagDtos, createdAt, createdBy, modifiedAt, modifiedBy);
  }

  public static PostingWithCommentsDto from(Posting entity) {
    return new PostingWithCommentsDto(
            entity.getId(),
            UserAccountDto.from(entity.getUserAccount()),
            entity.getPostingComments().stream()
                    .map(PostingCommentDto::from)
                    .collect(Collectors.toCollection(LinkedHashSet::new)),
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
}