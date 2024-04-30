package com.personal.projectforum.dto.request;

import com.personal.projectforum.dto.HashtagDto;
import com.personal.projectforum.dto.PostingDto;
import com.personal.projectforum.dto.UserAccountDto;

import java.util.Set;


public record PostingRequest(
        String title,
        String content
) {
    public static PostingRequest of(String title, String content) {
        return new PostingRequest(title, content);
    }

    public PostingDto toDto(UserAccountDto userAccountDto) {
        return toDto(userAccountDto, null);
    }

    public PostingDto toDto(UserAccountDto userAccountDto, Set<HashtagDto> hashtagDtos) {
        return PostingDto.of(
                userAccountDto,
                title,
                content,
                hashtagDtos
        );
    }
}