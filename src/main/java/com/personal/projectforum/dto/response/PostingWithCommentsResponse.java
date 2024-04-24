package com.personal.projectforum.dto.response;

import com.personal.projectforum.domain.PostingComment;
import com.personal.projectforum.dto.PostingWithCommentsDto;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * DTO for {@link PostingComment}
 */
public record PostingWithCommentsResponse(
        Long id,
        String title,
        String content,
        String hashtag,
        LocalDateTime createdAt,
        String email,
        String nickname,
        String userId,
        Set<PostingCommentResponse> postingCommentsResponse
) {

  public static PostingWithCommentsResponse of(Long id, String title, String content, String hashtag, LocalDateTime createdAt, String email, String nickname, String userId, Set<PostingCommentResponse> postingCommentResponses) {
    return new PostingWithCommentsResponse(id, title, content, hashtag, createdAt, email, nickname, userId, postingCommentResponses);
  }

  public static PostingWithCommentsResponse from(PostingWithCommentsDto dto) {
    String nickname = dto.userAccountDto().nickname();
    if (nickname == null || nickname.isBlank()) {
      nickname = dto.userAccountDto().userId();
    }

    return new PostingWithCommentsResponse(
            dto.id(),
            dto.title(),
            dto.content(),
            dto.hashtag(),
            dto.createdAt(),
            dto.userAccountDto().email(),
            nickname,
            dto.userAccountDto().userId(),
            dto.postingCommentDtos().stream()
                    .map(PostingCommentResponse::from)
                    .collect(Collectors.toCollection(LinkedHashSet::new))
    );
  }
}