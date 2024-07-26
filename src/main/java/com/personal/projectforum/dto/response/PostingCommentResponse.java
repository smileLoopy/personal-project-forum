package com.personal.projectforum.dto.response;

import com.personal.projectforum.domain.PostingComment;
import com.personal.projectforum.dto.PostingCommentDto;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

/**
 * DTO for {@link PostingComment}
 */
public record PostingCommentResponse(
        Long id,
        String content,
        LocalDateTime createdAt,
        String email,
        String nickname,
        String userId,
        Long parentCommentId,
        Set<PostingCommentResponse> childComments
) {

  public static PostingCommentResponse of(Long id, String content, LocalDateTime createdAt, String email, String nickname, String userId) {
    return PostingCommentResponse.of(id, content, createdAt, email, nickname, userId, null);
  }

  public static PostingCommentResponse of(Long id, String content, LocalDateTime createdAt, String email, String nickname, String userId, Long parentCommentId) {
    Comparator<PostingCommentResponse> childCommentComparator = Comparator
            .comparing(PostingCommentResponse::createdAt)
            .thenComparingLong(PostingCommentResponse::id);
    return new PostingCommentResponse(id, content, createdAt, email, nickname, userId, parentCommentId, new TreeSet<>(childCommentComparator));
  }

  public static PostingCommentResponse from(PostingCommentDto dto) {
    String nickname = dto.userAccountDto().nickname();
    if (nickname == null || nickname.isBlank()) {
      nickname = dto.userAccountDto().userId();
    }

    return PostingCommentResponse.of(
            dto.id(),
            dto.content(),
            dto.createdAt(),
            dto.userAccountDto().email(),
            nickname,
            dto.userAccountDto().userId(),
            dto.parentCommentId()
    );
  }

  public boolean hasParentComment() {
    return parentCommentId != null;
  }
}