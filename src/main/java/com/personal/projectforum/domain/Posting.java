package com.personal.projectforum.domain;

import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

public class Posting {
    private Long id;
    private String title;
    private String content;
    private String hashtag;

    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime modifiedAt;
    private String modifiedBy;
}
