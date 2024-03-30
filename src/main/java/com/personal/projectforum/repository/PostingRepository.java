package com.personal.projectforum.repository;

import com.personal.projectforum.domain.Posting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface PostingRepository extends JpaRepository<Posting, Long> {
}

