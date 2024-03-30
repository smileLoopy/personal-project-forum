package com.personal.projectforum.repository;

import com.personal.projectforum.domain.PostingComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface PostingCommentRepository extends JpaRepository<PostingComment, Long> {
}


