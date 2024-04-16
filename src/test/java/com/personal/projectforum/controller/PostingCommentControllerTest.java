package com.personal.projectforum.controller;

import com.personal.projectforum.config.SecurityConfig;
import com.personal.projectforum.dto.PostingCommentDto;
import com.personal.projectforum.dto.request.PostingCommentRequest;
import com.personal.projectforum.service.PostingCommentService;
import com.personal.projectforum.util.FormDataEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import({SecurityConfig.class, FormDataEncoder.class})
@DisplayName("View Controller - Comment")
@WebMvcTest(PostingCommentController.class)
class PostingCommentControllerTest {

    private final MockMvc mvc;
    private final FormDataEncoder formDataEncoder;

    @MockBean private PostingCommentService postingCommentService;

    public PostingCommentControllerTest(
            @Autowired MockMvc mvc,
            @Autowired FormDataEncoder formDataEncoder
    ) {
        this.mvc = mvc;
        this.formDataEncoder = formDataEncoder;
    }

    @DisplayName("[view][POST] Create Comment - Normal Retrieval")
    @Test
    void givenPostingCommentInfo_whenRequesting_thenSavesNewPostingComment() throws Exception {
        // Given
        long postingId = 1L;
        PostingCommentRequest request = PostingCommentRequest.of(postingId, "test comment");
        willDoNothing().given(postingCommentService).savePostingComment(any(PostingCommentDto.class));

        // When & Then
        mvc.perform(
                        post("/comments/new")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(formDataEncoder.encode(request))
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/postings/" + postingId))
                .andExpect(redirectedUrl("/postings/" + postingId));
        then(postingCommentService).should().savePostingComment(any(PostingCommentDto.class));
    }

    @DisplayName("[view][GET] Delete comment - Normal Retrieval")
    @Test
    void givenPostingCommentIdToDelete_whenRequesting_thenDeletesPostingComment() throws Exception {
        // Given
        long postingId = 1L;
        long postingCommentId = 1L;
        willDoNothing().given(postingCommentService).deletePostingComment(postingCommentId);

        // When & Then
        mvc.perform(
                        post("/comments/" + postingCommentId + "/delete")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(formDataEncoder.encode(Map.of("postingId", postingId)))
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/postings/" + postingId))
                .andExpect(redirectedUrl("/postings/" + postingId));
        then(postingCommentService).should().deletePostingComment(postingCommentId);
    }


}