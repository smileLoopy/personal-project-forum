package com.personal.projectforum.service;

import com.personal.projectforum.domain.Hashtag;
import com.personal.projectforum.domain.Posting;
import com.personal.projectforum.domain.UserAccount;
import com.personal.projectforum.domain.constant.SearchType;
import com.personal.projectforum.dto.HashtagDto;
import com.personal.projectforum.dto.PostingDto;
import com.personal.projectforum.dto.PostingWithCommentsDto;
import com.personal.projectforum.dto.UserAccountDto;
import com.personal.projectforum.repository.HashtagRepository;
import org.assertj.core.api.InstanceOfAssertFactories;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@DisplayName("Business Logic - Posting")
@ExtendWith(MockitoExtension.class)
class PostingServiceTest {

    @InjectMocks private PostingService sut;

    @Mock private HashtagService hashtagService;
    @Mock private PostingRepository postingRepository;
    @Mock private UserAccountRepository userAccountRepository;
    @Mock private HashtagRepository hashtagRepository;

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
        then(hashtagRepository).shouldHaveNoInteractions();
        then(postingRepository).shouldHaveNoInteractions();
    }

    @DisplayName("Searching not exist hashtag, return empty page")
    @Test
    void givenNonexistentHashtag_whenSearchingPostingsViaHashtag_thenReturnsEmptyPage() {
        // Given
        String hashtagName = "I'm not exist";
        Pageable pageable = Pageable.ofSize(20);
        given(postingRepository.findByHashtagNames(List.of(hashtagName), pageable)).willReturn(new PageImpl<>(List.of(), pageable, 0));

        // When
        Page<PostingDto> postings = sut.searchPostingsViaHashtag(hashtagName, pageable);

        // Then
        assertThat(postings).isEqualTo(Page.empty(pageable));
        then(postingRepository).should().findByHashtagNames(List.of(hashtagName), pageable);
    }

    @DisplayName("Searching posting via hashtag, return postings page.")
    @Test
    void givenHashtag_whenSearchingPostingsViaHashtag_thenReturnsPostingsPage() {
        // Given
        String hashtagName = "java";
        Pageable pageable = Pageable.ofSize(20);
        Posting expectedPosting = createPosting();
        given(postingRepository.findByHashtagNames(List.of(hashtagName), pageable)).willReturn(new PageImpl<>(List.of(expectedPosting), pageable, 1));

        // When
        Page<PostingDto> postings = sut.searchPostingsViaHashtag(hashtagName, pageable);

        // Then
        assertThat(postings).isEqualTo(new PageImpl<>(List.of(PostingDto.from(expectedPosting)), pageable, 1));
        then(postingRepository).should().findByHashtagNames(List.of(hashtagName), pageable);
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
                .hasFieldOrPropertyWithValue("hashtagDtos", posting.getHashtags().stream()
                        .map(HashtagDto::from)
                        .collect(Collectors.toUnmodifiableSet())
                );
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
                .hasFieldOrPropertyWithValue("hashtagDtos", posting.getHashtags().stream()
                        .map(HashtagDto::from)
                        .collect(Collectors.toUnmodifiableSet())
                );
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

    @DisplayName("Input posting info, create posting which contains hashtag info by extracting hashtag from the content.")
    @Test
    void givenPostingInfo_whenSavingPosting_thenExtractsHashtagsFromContentAndSavesPostingWithExtractedHashtags() {
        // Given
        PostingDto dto = createPostingDto();
        Set<String> expectedHashtagNames = Set.of("java", "spring");
        Set<Hashtag> expectedHashtags = new HashSet<>();
        expectedHashtags.add(createHashtag("java"));

        given(userAccountRepository.getReferenceById(dto.userAccountDto().userId())).willReturn(createUserAccount());
        given(hashtagService.parseHashtagNames(dto.content())).willReturn(expectedHashtagNames);
        given(hashtagService.findHashtagsByNames(expectedHashtagNames)).willReturn(expectedHashtags);
        given(postingRepository.save(any(Posting.class))).willReturn(createPosting());

        // When
        sut.savePosting(dto);

        // Then
        then(userAccountRepository).should().getReferenceById(dto.userAccountDto().userId());
        then(hashtagService).should().parseHashtagNames(dto.content());
        then(hashtagService).should().findHashtagsByNames(expectedHashtagNames);
        then(postingRepository).should().save(any(Posting.class));
    }

    @DisplayName("Input posting ID and modify info of posting, update posting")
    @Test
    void givenModifiedPostingInfo_whenUpdatingPosting_thenUpdatesPosting() {
        // Given
        Posting posting = createPosting();
        PostingDto dto = createPostingDto("new title", "new content #springboot");
        Set<String> expectedHashtagNames = Set.of("springboot");
        Set<Hashtag> expectedHashtags = new HashSet<>();

        given(postingRepository.getReferenceById(dto.id())).willReturn(posting);
        given(userAccountRepository.getReferenceById(dto.userAccountDto().userId())).willReturn(dto.userAccountDto().toEntity());
        willDoNothing().given(postingRepository).flush();
        willDoNothing().given(hashtagService).deleteHashtagWithoutPostings(any());
        given(hashtagService.parseHashtagNames(dto.content())).willReturn(expectedHashtagNames);
        given(hashtagService.findHashtagsByNames(expectedHashtagNames)).willReturn(expectedHashtags);

        // When
        sut.updatePosting(dto.id(), dto);

        // Then
        assertThat(posting)
                .hasFieldOrPropertyWithValue("title", dto.title())
                .hasFieldOrPropertyWithValue("content", dto.content())
                .extracting("hashtags", as(InstanceOfAssertFactories.COLLECTION))
                .hasSize(1)
                .extracting("hashtagName")
                .containsExactly("springboot");
        then(postingRepository).should().getReferenceById(dto.id());
        then(userAccountRepository).should().getReferenceById(dto.userAccountDto().userId());
        then(postingRepository).should().flush();
        then(hashtagService).should(times(2)).deleteHashtagWithoutPostings(any());
        then(hashtagService).should().parseHashtagNames(dto.content());
        then(hashtagService).should().findHashtagsByNames(expectedHashtagNames);
    }

    @DisplayName("Input nonexistent modify info of posting, print warning log and do nothing.")
    @Test
    void givenNonexistentPostingInfo_whenUpdatingPosting_thenLogsWarningAndDoesNothing() {
        // Given
        PostingDto dto = createPostingDto("new title", "new content");
        given(postingRepository.getReferenceById(dto.id())).willThrow(EntityNotFoundException.class);

        // When
        sut.updatePosting(dto.id(), dto);

        // Then
        then(postingRepository).should().getReferenceById(dto.id());
        then(userAccountRepository).shouldHaveNoInteractions();
        then(hashtagService).shouldHaveNoInteractions();
    }

    @DisplayName("If other user try to update the posting, do nothing")
    @Test
    void givenModifiedPostingInfoWithDifferentUser_whenUpdatingPosting_thenDoesNothing() {
        // Given
        Long differentPostingId = 22L;
        Posting differentPosting = createPosting(differentPostingId);
        differentPosting.setUserAccount(createUserAccount("John"));
        PostingDto dto = createPostingDto("새 타이틀", "새 내용");
        given(postingRepository.getReferenceById(differentPostingId)).willReturn(differentPosting);
        given(userAccountRepository.getReferenceById(dto.userAccountDto().userId())).willReturn(dto.userAccountDto().toEntity());

        // When
        sut.updatePosting(differentPostingId, dto);

        // Then
        then(postingRepository).should().getReferenceById(differentPostingId);
        then(userAccountRepository).should().getReferenceById(dto.userAccountDto().userId());
        then(hashtagService).shouldHaveNoInteractions();
    }

    @DisplayName("Input posting ID, delete posting")
    @Test
    void givenPostingId_whenDeletingPosting_thenDeletesPosting() {
        // Given
        Long postingId = 1L;
        String userId = "eunah";
        given(postingRepository.getReferenceById(postingId)).willReturn(createPosting());
        willDoNothing().given(postingRepository).deleteByIdAndUserAccount_UserId(postingId, userId);
        willDoNothing().given(postingRepository).flush();
        willDoNothing().given(hashtagService).deleteHashtagWithoutPostings(any());
        // When
        sut.deletePosting(1L, userId);

        // Then
        then(postingRepository).should().getReferenceById(postingId);
        then(postingRepository).should().deleteByIdAndUserAccount_UserId(postingId, userId);
        then(postingRepository).should().flush();
        then(hashtagService).should(times(2)).deleteHashtagWithoutPostings(any());
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
        Posting posting = createPosting();
        List<String> expectedHashtags = List.of("java", "spring", "boot");
        given(hashtagRepository.findAllHashtagNames()).willReturn(expectedHashtags);

        // When
        List<String> actualHashtags = sut.getHashtags();

        // Then
        assertThat(actualHashtags).isEqualTo(expectedHashtags);
        then(hashtagRepository).should().findAllHashtagNames();
    }

    private UserAccount createUserAccount() {
        return createUserAccount("eunah");
    }

    private UserAccount createUserAccount(String userId) {
        return UserAccount.of(
                userId,
                "password",
                "eunah@email.com",
                "Eunah",
                null
        );
    }

    private Posting createPosting() {
        return createPosting(1L);
    }

    private Posting createPosting(Long id) {
        Posting posting = Posting.of(
                createUserAccount(),
                "title",
                "content"
        );
        posting.addHashtags(Set.of(
                createHashtag(1L, "java"),
                createHashtag(2L, "spring")
        ));
        ReflectionTestUtils.setField(posting, "id", id);

        return posting;
    }

    private Hashtag createHashtag(String hashtagName) {
        return createHashtag(1L, hashtagName);
    }

    private Hashtag createHashtag(Long id, String hashtagName) {
        Hashtag hashtag = Hashtag.of(hashtagName);
        ReflectionTestUtils.setField(hashtag, "id", id);

        return hashtag;
    }

    private HashtagDto createHashtagDto() {
        return HashtagDto.of("java");
    }

    private PostingDto createPostingDto() {
        return createPostingDto("title", "content");
    }

    private PostingDto createPostingDto(String title, String content) {
        return PostingDto.of(
                1L,
                createUserAccountDto(),
                title,
                content,
                null,
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