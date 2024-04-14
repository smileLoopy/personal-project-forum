package com.personal.projectforum.controller;

import com.personal.projectforum.config.SecurityConfig;
import com.personal.projectforum.dto.PostingWithCommentsDto;
import com.personal.projectforum.dto.UserAccountDto;
import com.personal.projectforum.service.PaginationService;
import com.personal.projectforum.service.PostingService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(SecurityConfig.class)
@DisplayName("View Controller - Posting")
@WebMvcTest(PostingController.class)
class PostingControllerTest {

    private final MockMvc mvc;

    @MockBean private PostingService postingService;
    @MockBean private PaginationService paginationService;

    public PostingControllerTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

    @DisplayName("[view] [GET] Posting List (Forum) Page - Normal Retrieval Case")
    @Test
    public void givenNothing_whenRequestingPostingsView_thenReturnsPostingsView() throws Exception {
        // Given
        given(postingService.searchPostings(eq(null), eq(null), any(Pageable.class))).willReturn(Page.empty());
        given(paginationService.getPaginationBarNumbers(anyInt(), anyInt())).willReturn(List.of(0, 1, 2, 3, 4));

        // When & Then
        mvc.perform(get("/postings"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("postings/index"))
                .andExpect(model().attributeExists("postings"))
                .andExpect(model().attributeExists("paginationBarNumbers"));
        then(postingService).should().searchPostings(eq(null), eq(null), any(Pageable.class));
        then(paginationService).should().getPaginationBarNumbers(anyInt(), anyInt());
    }

    @DisplayName("[view][GET] posting list (forum) page - paging, sorting function")
    @Test
    void givenPagingAndSortingParams_whenSearchingPostingsPage_thenReturnsPostingsPage() throws Exception {
        // Given
        String sortName = "title";
        String direction = "desc";
        int pageNumber = 0;
        int pageSize = 5;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.desc(sortName)));
        List<Integer> barNumbers = List.of(1, 2, 3, 4, 5);
        given(postingService.searchPostings(null, null, pageable)).willReturn(Page.empty());
        given(paginationService.getPaginationBarNumbers(pageable.getPageNumber(), Page.empty().getTotalPages())).willReturn(barNumbers);

        // When & Then
        mvc.perform(
                        get("/postings")
                                .queryParam("page", String.valueOf(pageNumber))
                                .queryParam("size", String.valueOf(pageSize))
                                .queryParam("sort", sortName + "," + direction)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("postings/index"))
                .andExpect(model().attributeExists("postings"))
                .andExpect(model().attribute("paginationBarNumbers", barNumbers));
        then(postingService).should().searchPostings(null, null, pageable);
        then(paginationService).should().getPaginationBarNumbers(pageable.getPageNumber(), Page.empty().getTotalPages());
    }

    @DisplayName("[view] [GET] Posting Detail Page - Normal Retrieval Case")
    @Test
    public void givenNothing_whenRequestingPostingView_thenReturnsPostingView() throws Exception {
        // Given
        Long postingId = 1L;
        long totalCount = 1L;
        given(postingService.getPosting(postingId)).willReturn(createPostingWithCommentsDto());
        given(postingService.getPostingCount()).willReturn(totalCount);

        // When & Then
        mvc.perform(get("/postings/" + postingId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("postings/detail"))
                .andExpect(model().attributeExists("posting"))
                .andExpect(model().attributeExists("postingComments"))
                .andExpect(model().attributeExists("postingComments"))
                .andExpect(model().attribute("totalCount", totalCount));
        then(postingService).should().getPosting(postingId);
        then(postingService).should().getPostingCount();
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

    private PostingWithCommentsDto createPostingWithCommentsDto() {
        return PostingWithCommentsDto.of(
                1L,
                createUserAccountDto(),
                Set.of(),
                "title",
                "content",
                "#java",
                LocalDateTime.now(),
                "eunah",
                LocalDateTime.now(),
                "Eunah"
        );
    }

    private UserAccountDto createUserAccountDto() {
        return UserAccountDto.of(1L,
                "eunah",
                "pw",
                "eunah@mail.com",
                "Eunah",
                "memo",
                LocalDateTime.now(),
                "eunah",
                LocalDateTime.now(),
                "eunah"
                );
    }
}
