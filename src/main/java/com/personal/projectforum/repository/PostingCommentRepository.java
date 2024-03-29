package com.personal.projectforum.repository;

import com.personal.projectforum.domain.PostingComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostingCommentRepository extends JpaRepository<PostingComment, Long> {
}