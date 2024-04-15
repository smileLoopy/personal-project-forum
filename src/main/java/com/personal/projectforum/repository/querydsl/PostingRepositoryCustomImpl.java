package com.personal.projectforum.repository.querydsl;

import com.personal.projectforum.domain.Posting;
import com.personal.projectforum.domain.QPosting;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class PostingRepositoryCustomImpl extends QuerydslRepositorySupport implements PostingRepositoryCustom {

    public PostingRepositoryCustomImpl() {
        super(Posting.class);
    }

    @Override
    public List<String> findAllDistinctHashtags() {

        QPosting posting = QPosting.posting;

        return from(posting)
                .distinct()
                .select(posting.hashtag)
                .where(posting.hashtag.isNotNull())
                .fetch();
    }
}
