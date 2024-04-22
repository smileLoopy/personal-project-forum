package com.personal.projectforum.controller;

import com.personal.projectforum.domain.constant.FormStatus;
import com.personal.projectforum.domain.constant.SearchType;
import com.personal.projectforum.dto.request.PostingRequest;
import com.personal.projectforum.dto.security.ForumPrincipal;
import com.personal.projectforum.response.PostingResponse;
import com.personal.projectforum.response.PostingWithCommentsResponse;
import com.personal.projectforum.service.PaginationService;
import com.personal.projectforum.service.PostingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/postings")
@Controller
public class PostingController {

    private final PostingService postingService;
    private final PaginationService paginationService;

    @GetMapping
    public String postings(
            @RequestParam(required = false) SearchType searchType,
            @RequestParam(required = false) String searchValue,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            ModelMap map) {
        Page<PostingResponse> postings = postingService.searchPostings(searchType, searchValue, pageable).map(PostingResponse::from);
        List<Integer> barNumbers = paginationService.getPaginationBarNumbers(pageable.getPageNumber(), postings.getTotalPages());
        map.addAttribute("postings", postings);
        map.addAttribute("paginationBarNumbers", barNumbers);
        map.addAttribute("searchTypes", SearchType.values());
        return "postings/index";
    }

    @GetMapping("/{postingId}")
    public String posting(@PathVariable(name="postingId") long postingId, ModelMap map) {
        PostingWithCommentsResponse posting = PostingWithCommentsResponse.from(postingService.getPostingWithComments(postingId));
        map.addAttribute("posting",posting);
        map.addAttribute("postingComments", posting.postingCommentsResponse());
        map.addAttribute("totalCount", postingService.getPostingCount());
        map.addAttribute("searchTypeHashtag", SearchType.HASHTAG);
        return "postings/detail";
    }

    @GetMapping("/search-hashtag")
    public String searchPostingHashtag(
            @RequestParam(required = false) String searchValue,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            ModelMap map
    ) {

        Page<PostingResponse> postings = postingService.searchPostingsViaHashtag(searchValue, pageable).map(PostingResponse::from);
        List<Integer> barNumbers = paginationService.getPaginationBarNumbers(pageable.getPageNumber(), postings.getTotalPages());
        List<String> hashtag = postingService.getHashtags();
        map.addAttribute("postings", postings);
        map.addAttribute("hashtags", hashtag);
        map.addAttribute("paginationBarNumbers", barNumbers);
        map.addAttribute("searchType", SearchType.HASHTAG);
        return "postings/search-hashtag";
    }
    @GetMapping("/form")
    public String postingForm(ModelMap map) {
        map.addAttribute("formStatus", FormStatus.CREATE);

        return "postings/form";
    }

    @PostMapping("/form")
    public String postNewPosting(
            @AuthenticationPrincipal ForumPrincipal forumPrincipal,
            PostingRequest postingRequest
    ) {
        postingService.savePosting(postingRequest.toDto(forumPrincipal.toDto()));

        return "redirect:/postings";
    }

    @GetMapping("/{postingId}/form")
    public String updatePostingForm(@PathVariable Long postingId, ModelMap map) {
        PostingResponse posting = PostingResponse.from(postingService.getPosting(postingId));

        map.addAttribute("posting", posting);
        map.addAttribute("formStatus", FormStatus.UPDATE);

        return "postings/form";
    }

    @PostMapping("/{postingId}/form")
    public String updatePosting(
            @PathVariable Long postingId,
            @AuthenticationPrincipal ForumPrincipal forumPrincipal,
            PostingRequest postingRequest
    ) {
        postingService.updatePosting(postingId, postingRequest.toDto(forumPrincipal.toDto()));

        return "redirect:/postings/" + postingId;
    }

    @PostMapping("/{postingId}/delete")
    public String deletePosting(
            @PathVariable Long postingId,
            @AuthenticationPrincipal ForumPrincipal forumPrincipal
    ) {
        postingService.deletePosting(postingId, forumPrincipal.getUsername());

        return "redirect:/postings";
    }
}
