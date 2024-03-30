package com.personal.projectforum.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("DATA REST - API TEST")
@Transactional
@AutoConfigureMockMvc
@SpringBootTest
public class DataRestTest {

    private final MockMvc mvc;

    public DataRestTest(@Autowired MockMvc mvc) {
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
}
