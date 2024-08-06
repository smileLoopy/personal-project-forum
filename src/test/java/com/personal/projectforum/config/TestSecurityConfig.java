package com.personal.projectforum.config;

import com.personal.projectforum.domain.UserAccount;
import com.personal.projectforum.dto.UserAccountDto;
import com.personal.projectforum.repository.UserAccountRepository;
import com.personal.projectforum.service.UserAccountService;
import org.apache.catalina.User;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.event.annotation.BeforeTestMethod;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.anyString;

@Import(SecurityConfig.class)
public class TestSecurityConfig {

    @MockBean private UserAccountRepository  userAccountRepository;
    @MockBean private UserAccountService userAccountService;

    @BeforeTestMethod
    public  void securitySetUp() {
        given(userAccountService.searchUser(anyString()))
                .willReturn(Optional.of(createUserAccountDto()));
        given(userAccountService.saveUser(anyString(), anyString(), anyString(), anyString(), anyString()))
                .willReturn(createUserAccountDto());
    }

    private UserAccountDto createUserAccountDto() {
        return UserAccountDto.of(
                "eunahTest",
                "pw",
                "eunah-test@email.com",
                "eunah-test",
                "test memo"
        );
    }
}
