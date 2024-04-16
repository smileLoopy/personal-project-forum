package com.personal.projectforum.repository;

import com.personal.projectforum.domain.Posting;
import com.personal.projectforum.domain.QPosting;
import com.personal.projectforum.repository.querydsl.PostingRepositoryCustom;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface PostingRepository extends
        JpaRepository<Posting, Long>,
        PostingRepositoryCustom,
        QuerydslPredicateExecutor<Posting>,
        QuerydslBinderCustomizer<QPosting> {

    Page<Posting> findByTitleContaining(String title, Pageable pageable);
    Page<Posting> findByContentContaining(String content, Pageable pageable);
    Page<Posting> findByUserAccount_UserIdContaining(String userId, Pageable pageable);
    Page<Posting> findByUserAccount_NicknameContaining(String nickname, Pageable pageable);
    Page<Posting> findByHashtag(String hashtag, Pageable pageable);

    void deleteByIdAndUserAccount_UserId(Long postingId, String userId);

    @Override
    default void customize(QuerydslBindings bindings, QPosting root) {
        bindings.excludeUnlistedProperties(true);
        bindings.including(root.title, root.content, root.hashtag, root.createdAt, root.createdBy);
        bindings.bind(root.title).first(StringExpression::containsIgnoreCase); // query like '%${v}%
        bindings.bind(root.content).first(StringExpression::containsIgnoreCase); // query like '%${v}%
        bindings.bind(root.hashtag).first(StringExpression::containsIgnoreCase); // query like '%${v}%
        bindings.bind(root.createdAt).first(DateTimeExpression::eq);
        bindings.bind(root.createdBy).first(StringExpression::containsIgnoreCase); // query like '%${v}%
    }
}

