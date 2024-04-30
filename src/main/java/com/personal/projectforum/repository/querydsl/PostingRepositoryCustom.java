package com.personal.projectforum.repository.querydsl;

import com.personal.projectforum.domain.Posting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;

public interface PostingRepositoryCustom {

    /*
    * @Deprecated Since I made the new hashtag domain, this code is no longer nedded
    * @see HashtagRepositoryCustom#findAllHashtagNames()
    * */
    @Deprecated
    List<String> findAllDistinctHashtags();
    Page<Posting> findByHashtagNames(Collection<String> hashtagNames, Pageable pageable);
}
