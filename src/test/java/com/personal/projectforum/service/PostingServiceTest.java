package com.personal.projectforum.service;

import com.personal.projectforum.domain.Posting;
import com.personal.projectforum.domain.type.SearchType;
import com.personal.projectforum.dto.PostingDto;
import com.personal.projectforum.dto.PostingUpdateDto;
import com.personal.projectforum.repository.PostingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@DisplayName("Business Logic - Posting")
@ExtendWith(MockitoExtension.class)
class PostingServiceTest {

    @InjectMocks private PostingService sut;

    @Mock private PostingRepository postingRepository;

    @DisplayName("Searching posting, return posting list")
    @Test
    void givenSearchParameters_whenSearchingPostings_thenReturnsPostingList() {
        // Given

        // When
        Page<PostingDto> postings = sut.searchPostings(SearchType.TITLE, "search keyword"); // title, content, id, nickname, hashtag

        // Then
        assertThat(postings).isNotNull();

    }

    @DisplayName("Searching posting, return posting")
    @Test
    void givenPostingId_whenSearchingPosting_thenReturnsPosting() {
        // Given

        // When
        PostingDto postings = sut.searchPosting(1L); // title, content, id, nickname, hashtag

        // Then
        assertThat(postings).isNotNull();

    }

    @DisplayName("Input posting info, create posting")
    @Test
    void givenPostingInfo_whenSavingPosting_thenSavesPosting() {
        // Given
        given(postingRepository.save(any(Posting.class))).willReturn(null);

        // When
        sut.savePosting(PostingDto.of(LocalDateTime.now(), "eunah", "title", "content", "#java"));

        // Then
        then(postingRepository).should().save(any(Posting.class));
    }

    @DisplayName("Input posting ID and modify info of posting, update posting")
    @Test
    void givenPostingIdAndModifiedInfo_whenUpdatingPosting_thenUpdatesPosting() {
        // Given
        given(postingRepository.save(any(Posting.class))).willReturn(null);

        // When
        sut.updatePosting(1L, PostingUpdateDto.of("title", "content", "#java"));

        // Then
        then(postingRepository).should().save(any(Posting.class));
    }

    @DisplayName("Input posting ID, delete posting")
    @Test
    void givenPostingId_whenDeletingPosting_thenDeletesPosting() {
        // Given
        willDoNothing().given(postingRepository).delete(any(Posting.class));
        // When
        sut.deletePosting(1L);

        // Then
        then(postingRepository).should().delete(any(Posting.class));
    }
}