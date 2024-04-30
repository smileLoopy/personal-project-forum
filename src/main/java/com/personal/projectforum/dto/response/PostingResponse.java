package com.personal.projectforum.dto.response;

import com.personal.projectforum.domain.PostingComment;
import com.personal.projectforum.dto.PostingDto;
import com.personal.projectforum.dto.HashtagDto;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * DTO for {@link PostingComment}
 */
public record PostingResponse(
        Long id,
        String title,
        String content,
        Set<String>hashtags,
        LocalDateTime createdAt,
        String email,
        String nickname
) {

  public static PostingResponse of(Long id, String title, String content, Set<String> hashtags, LocalDateTime createdAt, String email, String nickname) {
    return new PostingResponse(id, title, content, hashtags, createdAt, email, nickname);
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
            dto.hashtagDtos().stream()
                    .map(HashtagDto::hashtagName)
                    .collect(Collectors.toUnmodifiableSet())
            ,
            dto.createdAt(),
            dto.userAccountDto().email(),
            nickname
    );
  }
}