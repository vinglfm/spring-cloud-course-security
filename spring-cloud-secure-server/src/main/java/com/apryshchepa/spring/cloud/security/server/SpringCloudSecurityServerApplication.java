package com.apryshchepa.spring.cloud.security.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@SpringBootApplication
@EnableResourceServer
public class SpringCloudSecurityServerApplication {

    @Bean
    @Autowired
    public CustomUserInfoTokenServices userInfoTokenService(ResourceServerProperties sso) {
        return new CustomUserInfoTokenServices(sso.getUserInfoUri(), sso.getClientId());
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudSecurityServerApplication.class, args);
    }
}
