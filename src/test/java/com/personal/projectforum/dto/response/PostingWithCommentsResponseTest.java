package com.personal.projectforum.dto.response;

import com.personal.projectforum.dto.HashtagDto;
import com.personal.projectforum.dto.PostingCommentDto;
import com.personal.projectforum.dto.PostingWithCommentsDto;
import com.personal.projectforum.dto.UserAccountDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DTO - Response test of posting with child comment")
class PostingWithCommentsResponseTest {

    @DisplayName("Posting without child comment + When transfer comment dto to api response, order the commnet des for time, and asc for id")
    @Test
    void givenPostingWithCommentsDtoWithoutChildComments_whenMapping_thenOrganizesCommentsWithCertainOrder() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Set<PostingCommentDto> postingCommentDtos = Set.of(
                createPostingCommentDto(1L, null, now),
                createPostingCommentDto(2L, null, now.plusDays(1L)),
                createPostingCommentDto(3L, null, now.plusDays(3L)),
                createPostingCommentDto(4L, null, now),
                createPostingCommentDto(5L, null, now.plusDays(5L)),
                createPostingCommentDto(6L, null, now.plusDays(4L)),
                createPostingCommentDto(7L, null, now.plusDays(2L)),
                createPostingCommentDto(8L, null, now.plusDays(7L))
        );
        PostingWithCommentsDto input = createPostingWithCommentsDto(postingCommentDtos);

        // When
        PostingWithCommentsResponse actual = PostingWithCommentsResponse.from(input);

        // Then
        assertThat(actual.postingCommentsResponse())
                .containsExactly(
                        createPostingCommentResponse(8L, null, now.plusDays(7L)),
                        createPostingCommentResponse(5L, null, now.plusDays(5L)),
                        createPostingCommentResponse(6L, null, now.plusDays(4L)),
                        createPostingCommentResponse(3L, null, now.plusDays(3L)),
                        createPostingCommentResponse(7L, null, now.plusDays(2L)),
                        createPostingCommentResponse(2L, null, now.plusDays(1L)),
                        createPostingCommentResponse(1L, null, now),
                        createPostingCommentResponse(4L, null, now)
                );
    }

    @DisplayName("Posting + when transfer comment dto to api response, sort and organize comment parent-child relationships according to each rule")
    @Test
    void givenPostingWithCommentsDto_whenMapping_thenOrganizesParentAndChildCommentsWithCertainOrders() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Set<PostingCommentDto> postingCommentDtos = Set.of(
                createPostingCommentDto(1L, null, now),
                createPostingCommentDto(2L, 1L, now.plusDays(1L)),
                createPostingCommentDto(3L, 1L, now.plusDays(3L)),
                createPostingCommentDto(4L, 1L, now),
                createPostingCommentDto(5L, null, now.plusDays(5L)),
                createPostingCommentDto(6L, null, now.plusDays(4L)),
                createPostingCommentDto(7L, 6L, now.plusDays(2L)),
                createPostingCommentDto(8L, 6L, now.plusDays(7L))
        );
        PostingWithCommentsDto input = createPostingWithCommentsDto(postingCommentDtos);

        // When
        PostingWithCommentsResponse actual = PostingWithCommentsResponse.from(input);

        // Then
        assertThat(actual.postingCommentsResponse())
                .containsExactly(
                        createPostingCommentResponse(5L, null, now.plusDays(5)),
                        createPostingCommentResponse(6L, null, now.plusDays(4)),
                        createPostingCommentResponse(1L, null, now)
                )
                .flatExtracting(PostingCommentResponse::childComments)
                .containsExactly(
                        createPostingCommentResponse(7L, 6L, now.plusDays(2L)),
                        createPostingCommentResponse(8L, 6L, now.plusDays(7L)),
                        createPostingCommentResponse(4L, 1L, now),
                        createPostingCommentResponse(2L, 1L, now.plusDays(1L)),
                        createPostingCommentResponse(3L, 1L, now.plusDays(3L))
                );
    }

    @DisplayName("Posting + when transfer comment dto to api response, there is no limit to the depth of the parent-child relationship.")
    @Test
    void givenPostingWithCommentsDto_whenMapping_thenOrganizesParentAndChildCommentsWithoutDepthLimit() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Set<PostingCommentDto> postingCommentDtos = Set.of(
                createPostingCommentDto(1L, null, now),
                createPostingCommentDto(2L, 1L, now.plusDays(1L)),
                createPostingCommentDto(3L, 2L, now.plusDays(2L)),
                createPostingCommentDto(4L, 3L, now.plusDays(3L)),
                createPostingCommentDto(5L, 4L, now.plusDays(4L)),
                createPostingCommentDto(6L, 5L, now.plusDays(5L)),
                createPostingCommentDto(7L, 6L, now.plusDays(6L)),
                createPostingCommentDto(8L, 7L, now.plusDays(7L))
        );
        PostingWithCommentsDto input = createPostingWithCommentsDto(postingCommentDtos);

        // When
        PostingWithCommentsResponse actual = PostingWithCommentsResponse.from(input);

        // Then
        Iterator<PostingCommentResponse> iterator = actual.postingCommentsResponse().iterator();
        long i = 1L;
        while (iterator.hasNext()) {
            PostingCommentResponse postingCommentsResponse = iterator.next();
            assertThat(postingCommentsResponse)
                    .hasFieldOrPropertyWithValue("id", i)
                    .hasFieldOrPropertyWithValue("parentCommentId", i == 1L ? null : i - 1L)
                    .hasFieldOrPropertyWithValue("createdAt", now.plusDays(i - 1L));

            iterator = postingCommentsResponse.childComments().iterator();
            i++;
        }
    }


    private PostingWithCommentsDto createPostingWithCommentsDto(Set<PostingCommentDto> postingCommentDtos) {
        return PostingWithCommentsDto.of(
                1L,
                createUserAccountDto(),
                postingCommentDtos,
                "title",
                "content",
                Set.of(HashtagDto.of("java")),
                LocalDateTime.now(),
                "eunah",
                LocalDateTime.now(),
                "eunah"
        );
    }

    private UserAccountDto createUserAccountDto() {
        return UserAccountDto.of(
                "eunah",
                "password",
                "eunah@mail.com",
                "Eunah",
                "This is memo",
                LocalDateTime.now(),
                "eunah",
                LocalDateTime.now(),
                "eunah"
        );
    }

    private PostingCommentDto createPostingCommentDto(Long id, Long parentCommentId, LocalDateTime createdAt) {
        return PostingCommentDto.of(
                id,
                1L,
                createUserAccountDto(),
                parentCommentId,
                "test comment " + id,
                createdAt,
                "eunah",
                createdAt,
                "eunah"
        );
    }

    private PostingCommentResponse createPostingCommentResponse(Long id, Long parentCommentId, LocalDateTime createdAt) {
        return PostingCommentResponse.of(
                id,
                "test comment " + id,
                createdAt,
                "eunah@mail.com",
                "Eunah",
                "eunah",
                parentCommentId
        );
    }
}