package com.personal.projectforum.controller;

import com.personal.projectforum.config.TestSecurityConfig;
import com.personal.projectforum.domain.constant.FormStatus;
import com.personal.projectforum.domain.constant.SearchType;
import com.personal.projectforum.dto.PostingDto;
import com.personal.projectforum.dto.PostingWithCommentsDto;
import com.personal.projectforum.dto.UserAccountDto;
import com.personal.projectforum.dto.request.PostingRequest;
import com.personal.projectforum.dto.response.PostingResponse;
import com.personal.projectforum.service.PaginationService;
import com.personal.projectforum.service.PostingService;
import com.personal.projectforum.util.FormDataEncoder;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import({TestSecurityConfig.class, FormDataEncoder.class})
@DisplayName("View Controller - Posting")
@WebMvcTest(PostingController.class)
class PostingControllerTest {

    private final MockMvc mvc;
    private final FormDataEncoder formDataEncoder;

    @MockBean private PostingService postingService;
    @MockBean private PaginationService paginationService;

    PostingControllerTest(
            @Autowired MockMvc mvc,
            @Autowired FormDataEncoder formDataEncoder
    ) {
        this.mvc = mvc;
        this.formDataEncoder = formDataEncoder;
    }

    @DisplayName("[view] [GET] Posting List (Forum) Page - Normal Retrieval Case")
    @Test
    void givenNothing_whenRequestingPostingsView_thenReturnsPostingsView() throws Exception {
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

    @DisplayName("[view] [GET] Posting List (Forum) Page - Retrieval with search keyword")
    @Test
    void givenSearchKeyword_whenSearchingPostingsView_thenReturnsPostingsView() throws Exception {
        // Given
        SearchType searchType = SearchType.TITLE;
        String searchValue = "title";

        given(postingService.searchPostings(eq(searchType), eq(searchValue), any(Pageable.class))).willReturn(Page.empty());
        given(paginationService.getPaginationBarNumbers(anyInt(), anyInt())).willReturn(List.of(0, 1, 2, 3, 4));

        // When & Then
        mvc.perform(get("/postings")
                        .queryParam("searchType", searchType.name())
                        .queryParam("searchValue", searchValue)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("postings/index"))
                .andExpect(model().attributeExists("postings"))
                .andExpect(model().attributeExists("searchTypes"));
        then(postingService).should().searchPostings(eq(searchType), eq(searchValue), any(Pageable.class));
        then(paginationService).should().getPaginationBarNumbers(anyInt(), anyInt());
    }

    @DisplayName("[view][GET] posting list (forum) page - paging, sorting function")
    @Test
    void givenPagingAndSortingParams_whenSearchingPostingsPage_thenReturnsPostingsView() throws Exception {
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

    @DisplayName("[view][GET] Posting Detail Page - No authentication, redirect to login page")
    @Test
    void givenNothing_whenRequestingPostingPage_thenRedirectsToLoginPage() throws Exception {
        // Given
        long postingId = 1L;

        // When & Then
        mvc.perform(get("/postings/" + postingId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
        then(postingService).shouldHaveNoInteractions();
        then(postingService).shouldHaveNoInteractions();
    }

    @WithMockUser
    @DisplayName("[view] [GET] Posting Detail Page - Normal Retrieval Case, Authenticated user")
    @Test
    void givenNothing_whenRequestingPostingView_thenReturnsPostingView() throws Exception {
        // Given
        Long postingId = 1L;
        long totalCount = 1L;
        given(postingService.getPostingWithComments(postingId)).willReturn(createPostingWithCommentsDto());
        given(postingService.getPostingCount()).willReturn(totalCount);

        // When & Then
        mvc.perform(get("/postings/" + postingId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("postings/detail"))
                .andExpect(model().attributeExists("posting"))
                .andExpect(model().attributeExists("postingComments"))
                .andExpect(model().attribute("totalCount", totalCount));
        then(postingService).should().getPostingWithComments(postingId);
        then(postingService).should().getPostingCount();
    }

    @Disabled("Development in progress")
    @DisplayName("[view] [GET] Posting Search Page - Normal Retrieval Case")
    @Test
    void givenNothing_whenRequestingPostingSearchView_thenReturnsPostingSearchView() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/postings/search"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("postings/search"));
    }

    @DisplayName("[view] [GET] Posting Search Hashtag Page - Normal Retrieval Case")
    @Test
    void givenNothing_whenRequestingPostingSearchHashtagView_thenReturnsPostingSearchHashtagView() throws Exception {
        // Given
        List<String> hashtags = List.of("#java", "#spring", "#boot");
        given(postingService.searchPostingsViaHashtag(eq(null), any(Pageable.class))).willReturn(Page.empty());
        given(paginationService.getPaginationBarNumbers(anyInt(), anyInt())).willReturn(List.of(1, 2, 3, 4, 5));
        given(postingService.getHashtags()).willReturn(hashtags);
        // When & Then
        mvc.perform(get("/postings/search-hashtag"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("postings/search-hashtag"))
                .andExpect(model().attribute("postings", Page.empty()))
                .andExpect(model().attribute("hashtags", hashtags))
                .andExpect(model().attributeExists("paginationBarNumbers"))
                .andExpect(model().attribute("searchType", SearchType.HASHTAG));
        then(postingService).should().searchPostingsViaHashtag(eq(null), any(Pageable.class));
        then(postingService).should().getHashtags();
        then(paginationService).should().getPaginationBarNumbers(anyInt(), anyInt());
    }

    @DisplayName("[view] [GET] Posting Search Hashtag Page - Normal Retrieval Case, Input hashtag")
    @Test
    void givenHashtag_whenRequestingPostingSearchHashtagView_thenReturnsPostingSearchHashtagView() throws Exception {
        // Given
        String hashtag = "#java";
        List<String> hashtags = List.of("#java", "#spring", "#boot");
        given(postingService.searchPostingsViaHashtag(eq(hashtag), any(Pageable.class))).willReturn(Page.empty());
        given(paginationService.getPaginationBarNumbers(anyInt(), anyInt())).willReturn(List.of(1, 2, 3, 4, 5));
        given(postingService.getHashtags()).willReturn(hashtags);
        // When & Then
        mvc.perform(get("/postings/search-hashtag")
                        .queryParam("searchValue", hashtag)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("postings/search-hashtag"))
                .andExpect(model().attribute("postings", Page.empty()))
                .andExpect(model().attributeExists("hashtags"))
                .andExpect(model().attributeExists("paginationBarNumbers"))
                .andExpect(model().attribute("searchType", SearchType.HASHTAG));
        then(postingService).should().searchPostingsViaHashtag(eq(hashtag), any(Pageable.class));
        then(postingService).should().getHashtags();
        then(paginationService).should().getPaginationBarNumbers(anyInt(), anyInt());
    }


    @WithMockUser
    @DisplayName("[view][GET] Writing new posting page")
    @Test
    void givenNothing_whenRequesting_thenReturnsNewPostingPage() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/postings/form"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("postings/form"))
                .andExpect(model().attribute("formStatus", FormStatus.CREATE));
    }

    @WithUserDetails(value = "eunahTest",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[view][POST] Saving new posting - Normal Retrieval")
    @Test
    void givenNewPostingInfo_whenRequesting_thenSavesNewPosting() throws Exception {
        // Given
        PostingRequest postingRequest = PostingRequest.of("new title", "new content", "#new");
        willDoNothing().given(postingService).savePosting(any(PostingDto.class));

        // When & Then
        mvc.perform(
                        post("/postings/form")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(formDataEncoder.encode(postingRequest))
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/postings"))
                .andExpect(redirectedUrl("/postings"));
        then(postingService).should().savePosting(any(PostingDto.class));
    }

    @DisplayName("[view][GET] Update posting page - No authentication redirect to login page")
    @Test
    void givenNothing_whenRequesting_thenRedirectsToLoginPage() throws Exception {
        // Given
        long postingId = 1L;

        // When & Then
        mvc.perform(get("/postings/" + postingId + "/form"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
        then(postingService).shouldHaveNoInteractions();
    }

    @WithMockUser
    @DisplayName("[view][GET] Update posting page")
    @Test
    void givenNothing_whenRequesting_thenReturnsUpdatedPostingPage() throws Exception {
        // Given
        long postingId = 1L;
        PostingDto dto = createPostingDto();
        given(postingService.getPosting(postingId)).willReturn(dto);

        // When & Then
        mvc.perform(get("/postings/" + postingId + "/form"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("postings/form"))
                .andExpect(model().attribute("posting", PostingResponse.from(dto)))
                .andExpect(model().attribute("formStatus", FormStatus.UPDATE));
        then(postingService).should().getPosting(postingId);
    }

    @WithUserDetails(value = "eunahTest",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[view][POST] Update posting - Normal Retrieval")
    @Test
    void givenUpdatedPostingInfo_whenRequesting_thenUpdatesNewPosting() throws Exception {
        // Given
        long postingId = 1L;
        PostingRequest postingRequest = PostingRequest.of("new title", "new content", "#new");
        willDoNothing().given(postingService).updatePosting(eq(postingId), any(PostingDto.class));

        // When & Then
        mvc.perform(
                        post("/postings/" + postingId + "/form")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(formDataEncoder.encode(postingRequest))
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/postings/" + postingId))
                .andExpect(redirectedUrl("/postings/" + postingId));
        then(postingService).should().updatePosting(eq(postingId), any(PostingDto.class));
    }

    @WithUserDetails(value = "eunahTest",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[view][POST] Delete posting - Normal Retrieval")
    @Test
    void givenPostingIdToDelete_whenRequesting_thenDeletesPosting() throws Exception {
        // Given
        long postingId = 1L;
        String userid = "eunahTest";
        willDoNothing().given(postingService).deletePosting(postingId, userid);

        // When & Then
        mvc.perform(
                        post("/postings/" + postingId + "/delete")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/postings"))
                .andExpect(redirectedUrl("/postings"));
        then(postingService).should().deletePosting(postingId, userid);
    }


    private PostingDto createPostingDto() {
        return PostingDto.of(
                createUserAccountDto(),
                "title",
                "content",
                "#java"
        );
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
        return UserAccountDto.of(
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
