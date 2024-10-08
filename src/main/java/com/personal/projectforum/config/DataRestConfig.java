package com.personal.projectforum.config;

import com.personal.projectforum.domain.Hashtag;
import com.personal.projectforum.domain.Posting;
import com.personal.projectforum.domain.PostingComment;
import com.personal.projectforum.domain.UserAccount;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;

@Configuration
public class DataRestConfig {

    @Bean
    public RepositoryRestConfigurer repositoryRestConfigurer() {
        return RepositoryRestConfigurer.withConfig((config, cors) ->
                config
                        .exposeIdsFor(UserAccount.class)
                        .exposeIdsFor(Posting.class)
                        .exposeIdsFor(PostingComment.class)
                        .exposeIdsFor(Hashtag.class)
        );
    }

}
