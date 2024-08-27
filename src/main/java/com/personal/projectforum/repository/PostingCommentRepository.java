package com.personal.projectforum.repository;

import com.personal.projectforum.domain.PostingComment;
import com.personal.projectforum.domain.QPostingComment;
import com.personal.projectforum.domain.projection.PostingCommentProjection;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(excerptProjection = PostingCommentProjection.class)
public interface PostingCommentRepository extends
        JpaRepository<PostingComment, Long>,
        QuerydslPredicateExecutor<PostingComment>,
        QuerydslBinderCustomizer<QPostingComment> {

    List<PostingComment> findByPosting_Id(Long postingId);
    void deleteByIdAndUserAccount_UserId(Long postingCommentId, String userId);

    @Override
    default void customize(QuerydslBindings bindings, QPostingComment root) {
        bindings.excludeUnlistedProperties(true);
        bindings.including(root.content, root.createdAt, root.createdBy);
        bindings.bind(root.content).first(StringExpression::containsIgnoreCase); // query like '%${v}%
        bindings.bind(root.createdAt).first(DateTimeExpression::eq);
        bindings.bind(root.createdBy).first(StringExpression::containsIgnoreCase); // query like '%${v}%
    }
}


