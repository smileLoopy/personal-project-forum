package com.personal.projectforum.dto.response;

import com.personal.projectforum.domain.Posting;
import com.personal.projectforum.domain.PostingComment;
import com.personal.projectforum.dto.PostingCommentDto;
import com.personal.projectforum.dto.PostingWithCommentsDto;
import com.personal.projectforum.dto.HashtagDto;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * DTO for {@link PostingComment}
 */
public record PostingWithCommentsResponse(
        Long id,
        String title,
        String content,
        Set<String> hashtags,
        LocalDateTime createdAt,
        String email,
        String nickname,
        String userId,
        Set<PostingCommentResponse> postingCommentsResponse
) {

  public static PostingWithCommentsResponse of(Long id, String title, String content, Set<String> hashtags, LocalDateTime createdAt, String email, String nickname, String userId, Set<PostingCommentResponse> postingCommentResponses) {
    return new PostingWithCommentsResponse(id, title, content, hashtags, createdAt, email, nickname, userId, postingCommentResponses);
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
            dto.hashtagDtos().stream()
                    .map(HashtagDto::hashtagName)
                    .collect(Collectors.toUnmodifiableSet())
            ,
            dto.createdAt(),
            dto.userAccountDto().email(),
            nickname,
            dto.userAccountDto().userId(),
            organizeChildComments(dto.postingCommentDtos())
    );
  }

  private static Set<PostingCommentResponse> organizeChildComments (Set<PostingCommentDto> dtos) {
    Map<Long, PostingCommentResponse> map = dtos.stream()
            .map(PostingCommentResponse::from)
            .collect(Collectors.toMap(PostingCommentResponse::id, Function.identity()));

    map.values().stream()
            .filter(PostingCommentResponse::hasParentComment)
            .forEach(comment -> {
              PostingCommentResponse parentComment = map.get(comment.parentCommentId());
              parentComment.childComments().add(comment);
            });

    return map.values().stream()
            .filter(comment -> !comment.hasParentComment())
            .collect(Collectors.toCollection(() ->
                    new TreeSet<>(Comparator
                            .comparing(PostingCommentResponse::createdAt)
                            .reversed()
                            .thenComparingLong(PostingCommentResponse::id)
                    )
            ));
  }
}