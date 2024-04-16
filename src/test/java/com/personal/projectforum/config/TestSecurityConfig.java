package com.personal.projectforum.config;

import com.personal.projectforum.domain.UserAccount;
import com.personal.projectforum.repository.UserAccountRepository;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.event.annotation.BeforeTestMethod;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.anyString;

@Import(SecurityConfig.class)
public class TestSecurityConfig {

    @MockBean private UserAccountRepository  userAccountRepository;

    @BeforeTestMethod
    public  void securitySetUp() {
        given(userAccountRepository.findById(anyString())).willReturn(Optional.of(UserAccount.of(
                "eunahTest",
                "pw",
                "eunah-test@email.com",
                "eunah-test",
                "test memo"
                )));
    }
}
