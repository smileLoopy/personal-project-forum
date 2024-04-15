package com.personal.projectforum.dto.request;

import com.personal.projectforum.dto.PostingDto;
import com.personal.projectforum.dto.UserAccountDto;

public record PostingRequest(
        String title,
        String content,
        String hashtag
) {
    public static PostingRequest of(String title, String content, String hashtag) {
        return new PostingRequest(title, content, hashtag);
    }

    public PostingDto toDto(UserAccountDto userAccountDto) {
        return PostingDto.of(
                userAccountDto,
                title,
                content,
                hashtag
        );
    }
}