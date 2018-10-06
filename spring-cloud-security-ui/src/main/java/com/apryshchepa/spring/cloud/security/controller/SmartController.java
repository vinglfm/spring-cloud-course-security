package com.apryshchepa.spring.cloud.security.controller;

import com.apryshchepa.spring.cloud.security.TollUsage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@EnableOAuth2Sso
public class SmartController extends WebSecurityConfigurerAdapter {

    @Autowired
    private OAuth2ClientContext clientContext;

    @Autowired
    private OAuth2RestTemplate restTemplate;

    @RequestMapping("/")
    public String home() {
        return "home";
    }

    @RequestMapping("/tolls")
    public String tolls(Model model) {
        OAuth2AccessToken token = clientContext.getAccessToken();
        System.out.println("Token: " + token);

        ResponseEntity<List<TollUsage>> tolls = restTemplate.exchange("http://localhost:9001/services/tolldata",
                HttpMethod.GET, null, new ParameterizedTypeReference<List<TollUsage>>() {
                });
        model.addAttribute("tolls", tolls.getBody());

        return "tolls";
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/", "/login**")
                .permitAll()
                .anyRequest()
                .authenticated();
    }
}
