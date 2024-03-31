package com.personal.projectforum.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/*
* /postings
* /postings/{posting-id}
* /postings/search
* /postings/search-hashtag
* */
@RequestMapping("/postings")
@Controller
public class PostingController {

    @GetMapping
    public String postings(ModelMap map) {
        map.addAttribute("postings", List.of());
        return "postings/index";
    }
}
