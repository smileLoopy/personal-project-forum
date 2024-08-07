package com.personal.projectforum.controller;

import com.personal.projectforum.config.SecurityConfig;
import com.personal.projectforum.repository.UserAccountRepository;
import com.personal.projectforum.service.UserAccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(SecurityConfig.class)
@WebMvcTest(MainController.class)
class MainControllerTest {

    private final MockMvc mvc;

    MainControllerTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

    @MockBean
    private UserAccountRepository userAccountRepository;

    @MockBean
    private UserAccountService userAccountService;

    @Test
    void givenNothing_whenRequestingRootPage_thenRedirectsToPostingsPage() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("forward:/postings"))
                .andExpect(forwardedUrl("/postings"))
                .andDo(MockMvcResultHandlers.print());
    }

}