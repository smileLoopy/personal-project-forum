package com.personal.projectforum.service;

import com.personal.projectforum.domain.Hashtag;
import com.personal.projectforum.repository.HashtagRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Transactional
@RequiredArgsConstructor
@Service
public class HashtagService {

    private final HashtagRepository hashtagRepository;

    public Set<String> parseHashtagNames(String content) {
        if(content == null) {
            return Set.of();
        }
        Pattern pattern = Pattern.compile("#[\\w가-힣]+");
        Matcher matcher = pattern.matcher(content.strip());
        Set<String> result = new HashSet<>();

        while(matcher.find()) {
            result.add(matcher.group().replace("#", ""));
        }

        return Set.copyOf(result);
    }

    public Set<Hashtag> findHashtagsByNames(Set<String> hashtagNames) {
        return new HashSet<>(hashtagRepository.findByHashtagNameIn(hashtagNames));
    }

    public void deleteHashtagWithoutPostings(Long hashtagId) {
        Hashtag hashtag = hashtagRepository.getReferenceById(hashtagId);
        if(hashtag.getPostings().isEmpty()) {
            hashtagRepository.delete(hashtag);
        }
    }
}
