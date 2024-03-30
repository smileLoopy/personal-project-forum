package com.personal.projectforum.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@DisplayName("View Controller - Posting")
@WebMvcTest(PostingController.class)
class PostingControllerTest {

    private final MockMvc mvc;

    public PostingControllerTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

    @DisplayName("[view] [GET] Posting List (Forum) Page - Normal Retrieval Case")
    @Test
    public void givenNothing_whenRequestingPostingsView_thenReturnsPostingsView() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/postings"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_HTML))
                .andExpect(model().attributeExists("postings"));
    }

    @DisplayName("[view] [GET] Posting Detail Page - Normal Retrieval Case")
    @Test
    public void givenNothing_whenRequestingPostingView_thenReturnsPostingView() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/postings/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_HTML))
                .andExpect(model().attributeExists("posting"));
    }

    @DisplayName("[view] [GET] Posting Search Page - Normal Retrieval Case")
    @Test
    public void givenNothing_whenRequestingPostingSearchView_thenReturnsPostingSearchView() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/postings/search"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_HTML));
    }

    @DisplayName("[view] [GET] Posting Search Hashtag Page - Normal Retrieval Case")
    @Test
    public void givenNothing_whenRequestingPostingHashtagSearchView_thenReturnsPostingHashtagSearchView() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/postings/search-hashtag"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_HTML));
    }
}
