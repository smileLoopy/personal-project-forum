package com.personal.projectforum.repository.querydsl;

import com.personal.projectforum.domain.Posting;
import com.personal.projectforum.domain.QHashtag;
import com.personal.projectforum.domain.QPosting;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.Collection;
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
                .select(posting.hashtags.any().hashtagName)
                .fetch();
    }

    @Override
    public Page<Posting> findByHashtagNames(Collection<String> hashtagNames, Pageable pageable) {
        QHashtag hashtag = QHashtag.hashtag;
        QPosting posting = QPosting.posting;

        JPQLQuery<Posting> query = from(posting)
                .innerJoin(posting.hashtags, hashtag)
                .where(hashtag.hashtagName.in(hashtagNames));
        List<Posting> postings = getQuerydsl().applyPagination(pageable, query).fetch();

        return new PageImpl<>(postings, pageable, query.fetchCount());
    }
}
