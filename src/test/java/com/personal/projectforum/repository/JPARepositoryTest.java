package com.personal.projectforum.repository;

import com.personal.projectforum.domain.Hashtag;
import com.personal.projectforum.domain.Posting;
import com.personal.projectforum.domain.PostingComment;
import com.personal.projectforum.domain.UserAccount;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JPA Connection TEST")
@Import(JPARepositoryTest.TestJpaConfig.class)
@DataJpaTest
class JPARepositoryTest {

    private final PostingRepository postingRepository;
    private final PostingCommentRepository postingCommentRepository;
    private final UserAccountRepository userAccountRepository;
    private final HashtagRepository hashtagRepository;

    JPARepositoryTest(
            @Autowired PostingRepository postingRepository,
            @Autowired PostingCommentRepository postingCommentRepository,
            @Autowired UserAccountRepository userAccountRepository,
            @Autowired HashtagRepository hashtagRepository
    ) {
        this.postingRepository = postingRepository;
        this.postingCommentRepository = postingCommentRepository;
        this.userAccountRepository = userAccountRepository;
        this.hashtagRepository = hashtagRepository;
    }

    @DisplayName("select test")
    @Test
    void givenTestData_whenSelecting_thenWorksFine() {

        // Given

        // When
        List<Posting> postings = postingRepository.findAll();

        // Then
        assertThat(postings)
                .isNotNull()
                .hasSize(123); // classpath:resources/data.sql (reference)
    }

    @DisplayName("insert test")
    @Test
    void givenTestData_whenInserting_thenWorksFine() {

        // Given
        long previousCount = postingRepository.count();
        UserAccount userAccount = userAccountRepository.save(UserAccount.of("newEunah", "pw", null, null, null));
        Posting posting = Posting.of(userAccount, "new posting", "new content");
        posting.addHashtags(Set.of(Hashtag.of("spring")));

        // When
        postingRepository.save(posting);

        // Then
        assertThat(postingRepository.count()).isEqualTo(previousCount + 1);

    }

    @DisplayName("update test")
    @Test
    void givenTestData_whenUpdating_thenWorksFine() {

        // Given
        Posting posting = postingRepository.findById(1L).orElseThrow();
        Hashtag updatedHashtag = Hashtag.of("springboot");
        posting.clearHashtags();
        posting.addHashtags(Set.of(updatedHashtag));

        // When
        Posting savedPost = postingRepository.saveAndFlush(posting);

        // Then
        assertThat(savedPost.getHashtags())
                .hasSize(1)
                .extracting("hashtagName", String.class)
                .containsExactly(updatedHashtag.getHashtagName());
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

    @DisplayName("select child comments test")
    @Test
    void givenParentCommentId_whenSelecting_thenReturnsChildComments() {
        // Given

        // When
        Optional<PostingComment> parentComment = postingCommentRepository.findById(1L);

        // Then
        assertThat(parentComment).get()
                .hasFieldOrPropertyWithValue("parentCommentId", null)
                .extracting("childComments", InstanceOfAssertFactories.COLLECTION)
                .hasSize(4);

    }

    @DisplayName("insert comment to the comment test")
    @Test
    void givenParentComment_whenSaving_thenInsertsChildComment() {
        // Given
        PostingComment parentComment = postingCommentRepository.getReferenceById(1L);
        PostingComment childComment = PostingComment.of(
                parentComment.getPosting(),
                parentComment.getUserAccount(),
                "대댓글"
        );

        // When
        parentComment.addChildComment(childComment);
        postingCommentRepository.flush();

        // Then
        assertThat(postingCommentRepository.findById(1L)).get()
                .hasFieldOrPropertyWithValue("parentCommentId", null)
                .extracting("childComments", InstanceOfAssertFactories.COLLECTION)
                .hasSize(5);
    }

    @DisplayName("delete parent comment having child comment test")
    @Test
    void givenPostingCommentHavingChildComments_whenDeletingParentComment_thenDeletesEveryComment() {
        // Given
        PostingComment parentComment = postingCommentRepository.getReferenceById(1L);
        long previousPostingCommentCount = postingCommentRepository.count();

        // When
        postingCommentRepository.delete(parentComment);

        // Then
        assertThat(postingCommentRepository.count()).isEqualTo(previousPostingCommentCount - 5); // 테스트 댓글 + 대댓글 4개
    }

    @DisplayName("delete parent comment having child comment & user Id test")
    @Test
    void givenPostingCommentIdHavingChildCommentsAndUserId_whenDeletingParentComment_thenDeletesEveryComment() {
        // Given
        long previousPostingCommentCount = postingCommentRepository.count();

        // When
        postingCommentRepository.deleteByIdAndUserAccount_UserId(1L, "eunah");

        // Then
        assertThat(postingCommentRepository.count()).isEqualTo(previousPostingCommentCount - 5); // 테스트 댓글 + 대댓글 4개
    }


    @DisplayName("[Querydsl] select name from the whole hashtag list")
    @Test
    void givenNothing_whenQueryingHashtags_thenReturnsHashtagNames() {
        // Given

        // When
        List<String> hashtagNames = hashtagRepository.findAllHashtagNames();

        // Then
        assertThat(hashtagNames).hasSize(19);
    }

    @DisplayName("[Querydsl] search posting which is paged by hashtag")
    @Test
    void givenHashtagNamesAndPageable_whenQueryingPostings_thenReturnsPostingPage() {
        // Given
        List<String> hashtagNames = List.of("blue", "crimson", "fuscia");
        Pageable pageable = PageRequest.of(0, 5, Sort.by(
                Sort.Order.desc("hashtags.hashtagName"),
                Sort.Order.asc("title")
        ));

        // When
        Page<Posting> postingPage = postingRepository.findByHashtagNames(hashtagNames, pageable);

        // Then
        assertThat(postingPage.getContent()).hasSize(pageable.getPageSize());
        assertThat(postingPage.getContent().get(0).getTitle()).isEqualTo("Fusce posuere felis sed lacus.");
        assertThat(postingPage.getContent().get(0).getHashtags())
                .extracting("hashtagName", String.class)
                .containsExactly("fuscia");
        assertThat(postingPage.getTotalElements()).isEqualTo(17);
        assertThat(postingPage.getTotalPages()).isEqualTo(4);
    }

    @EnableJpaAuditing
    @TestConfiguration
    static class TestJpaConfig {
        @Bean
        AuditorAware<String> auditorAware() {
            return  () -> Optional.of("eunah");
        }
    }
}
