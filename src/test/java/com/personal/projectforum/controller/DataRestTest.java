package com.personal.projectforum.controller;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Disabled("Spring Data REST Integration test is not needed anymore")
@DisplayName("DATA REST - API TEST")
@Transactional
@AutoConfigureMockMvc
@SpringBootTest
class DataRestTest {

    private final MockMvc mvc;

    DataRestTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

    @DisplayName("[api] retrieving posting list")
    @Test
    void givenNothing_whenRequestingPostings_thenReturnsPostings() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/api/postings"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json")))
                .andDo(print());
    }

    @DisplayName("[api] retrieving single posting")
    @Test
    void givenNothing_whenRequestingPosting_thenReturnJsonPostings() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/api/postings/1"))
                .andExpect((status().isOk()))
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json")));
    }

    @DisplayName("[api] retrieving posting -> comment list")
    @Test
    void givenNothing_whenRequestingPostingCommentsFromPosting_thenReturnPostingCommentsJsonPostings() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/api/postings/1/postingComments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json")));
    }

    @DisplayName("[api] retrieving comment list")
    @Test
    void givenNothing_whenRequestingPostingComments_thenReturnPostingCommentsJsonPostings() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/api/postingComments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json")));
    }

    @DisplayName("[api] retrieving single comment")
    @Test
    void givenNothing_whenRequestingPostingComments_thenReturnPostingCommentJsonPostings() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/api/postingComments/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json")));
    }


    @DisplayName("[api] Not providing any API related to member.")
    @Test
    void givenNothing_whenRequestingUserAccounts_thenThrowsException() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/api/userAccounts")).andExpect(status().isNotFound());
        mvc.perform(post("/api/userAccounts")).andExpect(status().isNotFound());
        mvc.perform(put("/api/userAccounts")).andExpect(status().isNotFound());
        mvc.perform(patch("/api/userAccounts")).andExpect(status().isNotFound());
        mvc.perform(delete("/api/userAccounts")).andExpect(status().isNotFound());
        mvc.perform(head("/api/userAccounts")).andExpect(status().isNotFound());
    }
}
