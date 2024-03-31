package com.personal.projectforum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class PersonalProjectForumApplication {

    public static void main(String[] args) {
        SpringApplication.run(PersonalProjectForumApplication.class, args);
    }

}
