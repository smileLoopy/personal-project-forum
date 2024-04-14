package com.personal.projectforum.controller;

import com.personal.projectforum.domain.type.SearchType;
import com.personal.projectforum.response.PostingResponse;
import com.personal.projectforum.response.PostingWithCommentsResponse;
import com.personal.projectforum.service.PaginationService;
import com.personal.projectforum.service.PostingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/*
* /postings
* /postings/{posting-id}
* /postings/search
* /postings/search-hashtag
* */
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
        return "postings/index";
    }

    @GetMapping("/{postingId}")
    public String posting(@PathVariable(name="postingId") long postingId, ModelMap map) {
        PostingWithCommentsResponse posting = PostingWithCommentsResponse.from(postingService.getPosting(postingId));
        map.addAttribute("posting",posting);
        map.addAttribute("postingComments", posting.postingCommentsResponse());
        map.addAttribute("totalCount", postingService.getPostingCount());
        return "postings/detail";
    }
}
