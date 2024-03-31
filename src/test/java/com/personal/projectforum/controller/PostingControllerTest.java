package com.personal.projectforum.controller;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("postings/index"))
                .andExpect(model().attributeExists("postings"));
    }

    @Disabled("Development in progress")
    @DisplayName("[view] [GET] Posting Detail Page - Normal Retrieval Case")
    @Test
    public void givenNothing_whenRequestingPostingView_thenReturnsPostingView() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/postings/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("postings/detail"))
                .andExpect(model().attributeExists("posting"))
                .andExpect(model().attributeExists("postingComments"));
    }

    @Disabled("Development in progress")
    @DisplayName("[view] [GET] Posting Search Page - Normal Retrieval Case")
    @Test
    public void givenNothing_whenRequestingPostingSearchView_thenReturnsPostingSearchView() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/postings/search"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("postings/search"));
    }

    @Disabled("Development in progress")
    @DisplayName("[view] [GET] Posting Search Hashtag Page - Normal Retrieval Case")
    @Test
    public void givenNothing_whenRequestingPostingHashtagSearchView_thenReturnsPostingHashtagSearchView() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/postings/search-hashtag"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("postings/search-hashtag"));
    }
}
