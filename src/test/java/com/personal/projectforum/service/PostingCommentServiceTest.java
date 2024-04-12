package com.personal.projectforum.service;

import com.personal.projectforum.domain.Posting;
import com.personal.projectforum.domain.PostingComment;
import com.personal.projectforum.dto.PostingCommentDto;
import com.personal.projectforum.repository.PostingCommentRepository;
import com.personal.projectforum.repository.PostingRepository;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("Business Logic - Comment")
@ExtendWith(MockitoExtension.class)
class PostingCommentServiceTest {

    @InjectMocks private  PostingCommentService sut;

    @Mock private PostingRepository postingRepository;
    @Mock private PostingCommentRepository postingCommentRepository;

    @DisplayName("Search with posting Id, return comment list")
    @Test
    void givenPostingId_whenSearchingPostingComments_thenReturnsPostingComments() {
        // Given
        Long postingId = 1L;
        given(postingRepository.findById(postingId)).willReturn(Optional.of(Posting.of("title", "content", "#java")));

        // When
        List<PostingCommentDto> postingComments = sut.searchPostingComment(postingId);

        // Then
        assertThat(postingComments).isNotNull();
        then(postingRepository).should().findById(postingId);
    }

    @DisplayName("Input comment info, saves comment")
    @Test
    void givenPostingCommentInfo_whenSavingPostingComments_thenSavesPostingComments() {
        // Given
        given(postingCommentRepository.save(any(PostingComment.class))).willReturn(null);

        // When
        sut.savePostingComment(PostingCommentDto.of(LocalDateTime.now(), "Eunah", LocalDateTime.now(), "Eunah", "comment"));

        // Then
        then(postingCommentRepository).should().save(any(PostingComment.class));
    }
}