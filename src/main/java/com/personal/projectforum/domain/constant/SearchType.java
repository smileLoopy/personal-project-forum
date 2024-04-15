package com.personal.projectforum.domain.constant;

import lombok.Getter;

public enum SearchType {
        TITLE("Title"),
        CONTENT("Content"),
        ID("User ID"),
        NICKNAME("Nickname"),
        HASHTAG("Hashtag");

        @Getter private final String description;

        SearchType(String description) {
                this.description = description;
        }
}
