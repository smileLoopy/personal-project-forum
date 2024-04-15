package com.personal.projectforum.repository.querydsl;

import java.util.List;

public interface PostingRepositoryCustom {

    List<String> findAllDistinctHashtags();
}
