package com.personal.projectforum.domain;

import java.time.LocalDateTime;

public class PostingComment {
    private Long id;
    private Posting posting;
    private String content;

    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime modifiedAt;
    private String modifiedBy;
}
