package com.personal.projectforum.repository;

import com.personal.projectforum.config.JpaConfig;
import com.personal.projectforum.domain.Posting;
import com.personal.projectforum.domain.UserAccount;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JPA Connection TEST")
@Import(JpaConfig.class)
@DataJpaTest
class JPARepositoryTest {

    private final PostingRepository postingRepository;
    private final PostingCommentRepository postingCommentRepository;
    private final UserAccountRepository userAccountRepository;

    public JPARepositoryTest(
            @Autowired PostingRepository postingRepository,
            @Autowired PostingCommentRepository postingCommentRepository,
            @Autowired UserAccountRepository userAccountRepository
    ) {
        this.postingRepository = postingRepository;
        this.postingCommentRepository = postingCommentRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @DisplayName("select test")
    @Test
    void givenTestData_whenSelecting_thenWorksFine() {

        // Given
        long previousCount = postingRepository.count();
        UserAccount userAccount = userAccountRepository.save(UserAccount.of("newEunah", "pw", null, null, null));
        Posting posting = Posting.of(userAccount, "new article", "new content", "#spring");

        // When
        postingRepository.save(posting);

        // Then
        assertThat(postingRepository.count()).isEqualTo(previousCount + 1);
    }

    @DisplayName("insert test")
    @Test
    void givenTestData_whenInserting_thenWorksFine() {

        // Given
        long previousCount = postingRepository.count();
        UserAccount userAccount = userAccountRepository.save(UserAccount.of("newEunah", "pw", null, null, null));
        Posting posting = Posting.of(userAccount, "new article", "new content", "#spring");

        // When
        Posting savedPost = postingRepository.save(posting);

        // Then
        assertThat(postingRepository.count()).isEqualTo(previousCount + 1);

    }

    @DisplayName("update test")
    @Test
    void givenTestData_whenUpdating_thenWorksFine() {

        // Given
        Posting posting = postingRepository.findById(1L).orElseThrow();
        String updatedHashtag = "#springboot";
        posting.setHashtag(updatedHashtag);

        // When
        Posting savedPost = postingRepository.saveAndFlush(posting);

        // Then
        assertThat(savedPost).hasFieldOrPropertyWithValue("hashtag", updatedHashtag);

    }

    @DisplayName("delete test")
    @Test
    void givenTestData_whenDeleting_thenWorksFine() {

        // Given
        Posting posting = postingRepository.findById(1L).orElseThrow();
        long previousPostingCount = postingRepository.count();
        long previousPostingCommentCont = postingCommentRepository.count(); // We set castcating, delete posting should delete comments too
        int deletedCommentsSize = posting.getPostingComments().size();

        // When
        postingRepository.delete(posting);

        // Then
        assertThat(postingRepository.count()).isEqualTo(previousPostingCount - 1);
        assertThat(postingCommentRepository.count()).isEqualTo(previousPostingCommentCont - deletedCommentsSize);

    }
}
