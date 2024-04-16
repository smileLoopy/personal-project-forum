package com.personal.projectforum.service;

import com.personal.projectforum.domain.Posting;
import com.personal.projectforum.domain.PostingComment;
import com.personal.projectforum.domain.UserAccount;
import com.personal.projectforum.dto.PostingCommentDto;
import com.personal.projectforum.dto.UserAccountDto;
import com.personal.projectforum.repository.PostingCommentRepository;
import com.personal.projectforum.repository.PostingRepository;
import com.personal.projectforum.repository.UserAccountRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@DisplayName("Business Logic - Comment")
@ExtendWith(MockitoExtension.class)
class PostingCommentServiceTest {

    @InjectMocks private  PostingCommentService sut;

    @Mock private PostingRepository postingRepository;
    @Mock private PostingCommentRepository postingCommentRepository;
    @Mock private UserAccountRepository userAccountRepository;

    @DisplayName("Search with posting Id, return comment list")
    @Test
    void givenPostingId_whenSearchingPostingComments_thenReturnsPostingComments() {
        // Given
        Long postingId = 1L;
        PostingComment expected = createPostingComment("content");
        given(postingCommentRepository.findByPosting_Id(postingId)).willReturn(List.of(expected));

        // When
        List<PostingCommentDto> actual = sut.searchPostingComments(postingId);

        // Then
        assertThat(actual)
                .hasSize(1)
                .first().hasFieldOrPropertyWithValue("content", expected.getContent());
        then(postingCommentRepository).should().findByPosting_Id(postingId);
    }

    @DisplayName("Input comment info, saves comment")
    @Test
    void givenPostingCommentInfo_whenSavingPostingComments_thenSavesPostingComments() {
        // Given
        PostingCommentDto dto = createPostingCommentDto("comment");
        given(postingRepository.getReferenceById(dto.postingId())).willReturn(createPosting());
        given(userAccountRepository.getReferenceById(dto.userAccountDto().userId())).willReturn(createUserAccount());
        given(postingCommentRepository.save(any(PostingComment.class))).willReturn(null);

        // When
        sut.savePostingComment(dto);

        // Then
        then(postingRepository).should().getReferenceById(dto.postingId());
        then(userAccountRepository).should().getReferenceById(dto.userAccountDto().userId());
        then(postingCommentRepository).should().save(any(PostingComment.class));
    }

    @DisplayName("Try to save comment and posting is not exist, print warning log and do nothing.")
    @Test
    void givenNonexistentPosting_whenSavingPostingComment_thenLogsSituationAndDoesNothing() {
        // Given
        PostingCommentDto dto = createPostingCommentDto("댓글");
        given(postingRepository.getReferenceById(dto.postingId())).willThrow(EntityNotFoundException.class);

        // When
        sut.savePostingComment(dto);

        // Then
        then(postingRepository).should().getReferenceById(dto.postingId());
        then(userAccountRepository).shouldHaveNoInteractions();
        then(postingCommentRepository).shouldHaveNoInteractions();
    }

    @DisplayName("Input comment info, update comment.")
    @Test
    void givenPostingCommentInfo_whenUpdatingPostingComment_thenUpdatesPostingComment() {
        // Given
        String oldContent = "content";
        String updatedContent = "content2";
        PostingComment postingComment = createPostingComment(oldContent);
        PostingCommentDto dto = createPostingCommentDto(updatedContent);
        given(postingCommentRepository.getReferenceById(dto.id())).willReturn(postingComment);

        // When
        sut.updatePostingComment(dto);

        // Then
        assertThat(postingComment.getContent())
                .isNotEqualTo(oldContent)
                .isEqualTo(updatedContent);
        then(postingCommentRepository).should().getReferenceById(dto.id());
    }

    @DisplayName("Try to update no existing comment, print log and do nothing.")
    @Test
    void givenNonexistentPostingComment_whenUpdatingPostingComment_thenLogsWarningAndDoesNothing() {
        // Given
        PostingCommentDto dto = createPostingCommentDto("comment");
        given(postingCommentRepository.getReferenceById(dto.id())).willThrow(EntityNotFoundException.class);

        // When
        sut.updatePostingComment(dto);

        // Then
        then(postingCommentRepository).should().getReferenceById(dto.id());
    }

    @DisplayName("Input comment id, delete comment.")
    @Test
    void givenPostingCommentId_whenDeletingPostingComment_thenDeletesPostingComment() {
        // Given
        Long postingCommentId = 1L;
        String userId = "eunah";
        willDoNothing().given(postingCommentRepository).deleteByIdAndUserAccount_UserId(postingCommentId, userId);

        // When
        sut.deletePostingComment(postingCommentId, userId);

        // Then
        then(postingCommentRepository).should().deleteByIdAndUserAccount_UserId(postingCommentId, userId);
    }


    private PostingCommentDto createPostingCommentDto(String content) {
        return PostingCommentDto.of(
                1L,
                1L,
                createUserAccountDto(),
                content,
                LocalDateTime.now(),
                "uno",
                LocalDateTime.now(),
                "uno"
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

    private PostingComment createPostingComment(String content) {
        return PostingComment.of(
                Posting.of(createUserAccount(), "title", "content", "hashtag"),
                createUserAccount(),
                content
        );
    }

    private UserAccount createUserAccount() {
        return UserAccount.of(
                "eunah",
                "password",
                "eunah@email.com",
                "Eunah",
                null
        );
    }

    private Posting createPosting() {
        return Posting.of(
                createUserAccount(),
                "title",
                "content",
                "#java"
        );
    }
}