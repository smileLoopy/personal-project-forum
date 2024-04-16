package com.personal.projectforum.service;

import com.personal.projectforum.domain.Posting;
import com.personal.projectforum.domain.UserAccount;
import com.personal.projectforum.domain.constant.SearchType;
import com.personal.projectforum.dto.PostingDto;
import com.personal.projectforum.dto.PostingWithCommentsDto;
import com.personal.projectforum.dto.UserAccountDto;
import com.personal.projectforum.repository.PostingRepository;
import com.personal.projectforum.repository.UserAccountRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
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
    @Mock private UserAccountRepository userAccountRepository;

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
        given(postingRepository.findByTitleContaining(searchKeyword, pageable)).willReturn(Page.empty());

        // When
        Page<PostingDto> postings = sut.searchPostings(searchType, searchKeyword, pageable);

        // Then
        assertThat(postings).isEmpty();
        then(postingRepository).should().findByTitleContaining(searchKeyword, pageable);
    }

    @DisplayName("Searching posting via hashtag without any search keyword, return empty page.")
    @Test
    void givenNoSearchParameters_whenSearchingPostingsViaHashtag_thenReturnsEmptyPage() {
        // Given
        Pageable pageable = Pageable.ofSize(20);

        // When
        Page<PostingDto> postings = sut.searchPostingsViaHashtag(null, pageable);

        // Then
        assertThat(postings).isEqualTo(Page.empty(pageable));
        then(postingRepository).shouldHaveNoInteractions();
    }

    @DisplayName("Searching posting via hashtag, return postings page.")
    @Test
    void givenHashtag_whenSearchingPostingsViaHashtag_thenReturnsPostingsPage() {
        // Given
        String hashtag = "#java";
        Pageable pageable = Pageable.ofSize(20);
        given(postingRepository.findByHashtag(hashtag, pageable)).willReturn(Page.empty(pageable));
        // When
        Page<PostingDto> postings = sut.searchPostingsViaHashtag(hashtag, pageable);

        // Then
        assertThat(postings).isEqualTo(Page.empty(pageable));
        then(postingRepository).should().findByHashtag(hashtag, pageable);
    }


    @DisplayName("Searching with postingId, return comments on the posting.")
    @Test
    void givenPostingId_whenSearchingPostingWithComments_thenReturnsPostingWithComments() {
        // Given
        Long postingId = 1L;
        Posting posting = createPosting();
        given(postingRepository.findById(postingId)).willReturn(Optional.of(posting));

        // When
        PostingWithCommentsDto dto = sut.getPostingWithComments(postingId);

        // Then
        assertThat(dto)
                .hasFieldOrPropertyWithValue("title", posting.getTitle())
                .hasFieldOrPropertyWithValue("content", posting.getContent())
                .hasFieldOrPropertyWithValue("hashtag", posting.getHashtag());
        then(postingRepository).should().findById(postingId);
    }

    @DisplayName("If there is no comment on the posting, return exception.")
    @Test
    void givenNonexistentPostingId_whenSearchingPostingWithComments_thenThrowsException() {
        // Given
        Long postingId = 0L;
        given(postingRepository.findById(postingId)).willReturn(Optional.empty());

        // When
        Throwable t = catchThrowable(() -> sut.getPostingWithComments(postingId));

        // Then
        assertThat(t)
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Posting not exist - postingId: " + postingId);
        then(postingRepository).should().findById(postingId);
    }


    @DisplayName("Searching posting, return posting")
    @Test
    void givenPostingId_whenSearchingPosting_thenReturnsPosting() {
        // Given
        Long postingId = 1L;
        Posting posting = createPosting();
        given(postingRepository.findById(postingId)).willReturn(Optional.of(posting));

        // When
        PostingDto dto = sut.getPosting(postingId);

        // Then
        assertThat(dto)
                .hasFieldOrPropertyWithValue("title", posting.getTitle())
                .hasFieldOrPropertyWithValue("content", posting.getContent())
                .hasFieldOrPropertyWithValue("hashtag", posting.getHashtag());
        then(postingRepository).should().findById(postingId);

    }

    @DisplayName("If the posting is not exist, throw exception.")
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
                .hasMessage("Posting not exist - postingId: " + postingId);
        then(postingRepository).should().findById(postingId);
    }

    @DisplayName("Input posting info, create posting.")
    @Test
    void givenPostingInfo_whenSavingPosting_thenSavesPosting() {
        // Given
        PostingDto dto = createPostingDto();
        given(userAccountRepository.getReferenceById(dto.userAccountDto().userId())).willReturn(createUserAccount());
        given(postingRepository.save(any(Posting.class))).willReturn(createPosting());

        // When
        sut.savePosting(dto);

        // Then
        then(userAccountRepository).should().getReferenceById(dto.userAccountDto().userId());
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
        sut.updatePosting(dto.id(), dto);

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
        sut.updatePosting(dto.id(), dto);

        // Then
        then(postingRepository).should().getReferenceById(dto.id());
    }

    @DisplayName("Input posting ID, delete posting")
    @Test
    void givenPostingId_whenDeletingPosting_thenDeletesPosting() {
        // Given
        Long postingId = 1L;
        //willDoNothing().given(postingRepository).delete(any(Posting.class));
        // When
        sut.deletePosting(1L);

        // Then
        then(postingRepository).should().deleteById(postingId);
    }

    @DisplayName("Search posting count, return posting count")
    @Test
    void givenNothing_whenCountingPostings_thenReturnsPostingCount() {
        // Given
        long expected = 0L;
        given(postingRepository.count()).willReturn(expected);

        // When
        long actual = sut.getPostingCount();

        // Then
        assertThat(actual).isEqualTo(expected);
        then(postingRepository).should().count();
    }

    @DisplayName("Search hashtag, return unique list of hashtags")
    @Test
    void givenNothing_whenCalling_thenReturnsHashtags() {
        // Given
        List<String> expectedHashtags = List.of("#java", "#spring", "#boot");
        given(postingRepository.findAllDistinctHashtags()).willReturn(expectedHashtags);

        // When
        List<String> actualHashtags = sut.getHashtags();

        // Then
        assertThat(actualHashtags).isEqualTo(expectedHashtags);
        then(postingRepository).should().findAllDistinctHashtags();
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
        Posting posting = Posting.of(
                createUserAccount(),
                "title",
                "content",
                "#java"
        );
        ReflectionTestUtils.setField(posting, "id", 1L);

        return posting;
    }

    private PostingDto createPostingDto() {
        return createPostingDto("title", "content", "#java");
    }

    private PostingDto createPostingDto(String title, String content, String hashtag) {
        return PostingDto.of(
                1L,
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