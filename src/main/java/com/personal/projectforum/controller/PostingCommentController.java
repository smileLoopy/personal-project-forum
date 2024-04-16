package com.personal.projectforum.controller;

import com.personal.projectforum.dto.UserAccountDto;
import com.personal.projectforum.dto.request.PostingCommentRequest;
import com.personal.projectforum.service.PostingCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RequestMapping("/comments")
@Controller
public class PostingCommentController {

    private final PostingCommentService postingCommentService;

    @PostMapping("/new")
    public String postNewPostingComment(PostingCommentRequest postingCommentRequest) {
        //TODO: Need to insert authentication info
        postingCommentService.savePostingComment(postingCommentRequest.toDto(UserAccountDto.of(
                "eunah", "pw", "eunah@mail.com", null, null
        )));

        return "redirect:/postings/" + postingCommentRequest.postingId();
    }

    @PostMapping ("/{commentId}/delete")
    public String deletePostingComment(@PathVariable Long commentId, Long postingId) {
        postingCommentService.deletePostingComment(commentId);

        return "redirect:/postings/" + postingId;
    }
}
