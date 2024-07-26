package com.personal.projectforum.controller;

import com.personal.projectforum.config.TestSecurityConfig;
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
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import({TestSecurityConfig.class, FormDataEncoder.class})
@DisplayName("View Controller - Comment")
@WebMvcTest(PostingCommentController.class)
class PostingCommentControllerTest {

    private final MockMvc mvc;
    private final FormDataEncoder formDataEncoder;

    @MockBean private PostingCommentService postingCommentService;

    PostingCommentControllerTest(
            @Autowired MockMvc mvc,
            @Autowired FormDataEncoder formDataEncoder
    ) {
        this.mvc = mvc;
        this.formDataEncoder = formDataEncoder;
    }

    @WithUserDetails(value = "eunahTest",setupBefore = TestExecutionEvent.TEST_EXECUTION)
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

    @WithUserDetails(value = "eunahTest",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[view][GET] Delete comment - Normal Retrieval")
    @Test
    void givenPostingCommentIdToDelete_whenRequesting_thenDeletesPostingComment() throws Exception {
        // Given
        long postingId = 1L;
        long postingCommentId = 1L;
        String userId = "eunahTest";
        willDoNothing().given(postingCommentService).deletePostingComment(postingCommentId, userId);

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
        then(postingCommentService).should().deletePostingComment(postingCommentId, userId);
    }

    @WithUserDetails(value = "eunahTest", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[view][POST] Save child comment - Normal Retrieval")
    @Test
    void givenPostingCommentInfoWithParentCommentId_whenRequesting_thenSavesNewChildComment() throws Exception {
        // Given
        long postingId = 1L;
        PostingCommentRequest request = PostingCommentRequest.of(postingId, 1L, "test comment");
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


}