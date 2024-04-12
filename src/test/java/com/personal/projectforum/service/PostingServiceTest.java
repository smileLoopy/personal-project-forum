package com.personal.projectforum.service;

import com.personal.projectforum.domain.Posting;
import com.personal.projectforum.domain.UserAccount;
import com.personal.projectforum.domain.type.SearchType;
import com.personal.projectforum.dto.PostingDto;
import com.personal.projectforum.dto.PostingWithCommentsDto;
import com.personal.projectforum.dto.UserAccountDto;
import com.personal.projectforum.repository.PostingRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@DisplayName("Business Logic - Posting")
@ExtendWith(MockitoExtension.class)
class PostingServiceTest {

    @InjectMocks private PostingService sut;

    @Mock private PostingRepository postingRepository;

    @DisplayName("Searching posting without search keyword, return posting page")
    @Test
    void givenNoSearchParameters_whenSearchingPostings_thenReturnsPostingPage() {
        // Given
        Pageable pageable = Pageable.ofSize(20);
        given(postingRepository.findAll(pageable)).willReturn(Page.empty());

        // When
        Page<PostingDto> postings = sut.searchPostings(null, null, pageable);
        // Then
        assertThat(postings).isEmpty();
        then(postingRepository).should().findAll(pageable);

    }

    @DisplayName("Searching posting with search keyword, return posting page.")
    @Test
    void givenSearchParameters_whenSearchingPostings_thenReturnsPostingPage() {
        // Given
        SearchType searchType = SearchType.TITLE;
        String searchKeyword = "title";
        Pageable pageable = Pageable.ofSize(20);
        given(postingRepository.findByTitle(searchKeyword, pageable)).willReturn(Page.empty());

        // When
        Page<PostingDto> postings = sut.searchPostings(searchType, searchKeyword, pageable);

        // Then
        assertThat(postings).isEmpty();
        then(postingRepository).should().findByTitle(searchKeyword, pageable);
    }


    @DisplayName("Searching posting, return posting")
    @Test
    void givenPostingId_whenSearchingPosting_thenReturnsPosting() {
        // Given
        Long postingId = 1L;
        Posting posting = createPosting();
        given(postingRepository.findById(postingId)).willReturn(Optional.of(posting));

        // When
        PostingWithCommentsDto dto = sut.getPosting(postingId);

        // Then
        assertThat(dto)
                .hasFieldOrPropertyWithValue("title", posting.getTitle())
                .hasFieldOrPropertyWithValue("content", posting.getContent())
                .hasFieldOrPropertyWithValue("hashtag", posting.getHashtag());
        then(postingRepository).should().findById(postingId);

    }

    @DisplayName("Search nonexistent Posting, return exception.")
    @Test
    void givenNonexistentPostingId_whenSearchingPosting_thenThrowsException() {
        // Given
        Long postingId = 0L;
        given(postingRepository.findById(postingId)).willReturn(Optional.empty());

        // When
        Throwable t = catchThrowable(() -> sut.getPosting(postingId));

        // Then
        assertThat(t)
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("NO POSTING - postingId: " + postingId);
        then(postingRepository).should().findById(postingId);
    }

    @DisplayName("Input posting info, create posting.")
    @Test
    void givenPostingInfo_whenSavingPosting_thenSavesPosting() {
        // Given
        PostingDto dto = createPostingDto();
        given(postingRepository.save(any(Posting.class))).willReturn(createPosting());

        // When
        sut.savePosting(dto);

        // Then
        then(postingRepository).should().save(any(Posting.class));
    }

    @DisplayName("Input posting ID and modify info of posting, update posting")
    @Test
    void givenModifiedPostingInfo_whenUpdatingPosting_thenUpdatesPosting() {
        // Given
        Posting posting = createPosting();
        PostingDto dto = createPostingDto("new title", "new content", "#springboot");
        given(postingRepository.getReferenceById(dto.id())).willReturn(posting);

        // When
        sut.updatePosting(dto);

        // Then
        assertThat(posting)
                .hasFieldOrPropertyWithValue("title", dto.title())
                .hasFieldOrPropertyWithValue("content", dto.content())
                .hasFieldOrPropertyWithValue("hashtag", dto.hashtag());
        then(postingRepository).should().getReferenceById(dto.id());
    }

    @DisplayName("Input nonexistent modify info of posting, print warning log and do nothing.")
    @Test
    void givenNonexistentPostingInfo_whenUpdatingPosting_thenLogsWarningAndDoesNothing() {
        // Given
        PostingDto dto = createPostingDto("new title", "new content", "#springboot");
        given(postingRepository.getReferenceById(dto.id())).willThrow(EntityNotFoundException.class);

        // When
        sut.updatePosting(dto);

        // Then
        then(postingRepository).should().getReferenceById(dto.id());
    }

    @DisplayName("Input posting ID, delete posting")
    @Test
    void givenPostingId_whenDeletingPosting_thenDeletesPosting() {
        // Given
        Long postingId = 1L;
        willDoNothing().given(postingRepository).delete(any(Posting.class));
        // When
        sut.deletePosting(1L);

        // Then
        then(postingRepository).should().deleteById(postingId);
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

    private PostingDto createPostingDto() {
        return createPostingDto("title", "content", "#java");
    }

    private PostingDto createPostingDto(String title, String content, String hashtag) {
        return PostingDto.of(1L,
                createUserAccountDto(),
                title,
                content,
                hashtag,
                LocalDateTime.now(),
                "Eunah",
                LocalDateTime.now(),
                "Eunah");
    }

    private UserAccountDto createUserAccountDto() {
        return UserAccountDto.of(
                1L,
                "eunah",
                "password",
                "eunah@mail.com",
                "eunah",
                "This is memo",
                LocalDateTime.now(),
                "eunah",
                LocalDateTime.now(),
                "eunah"
        );
    }
}