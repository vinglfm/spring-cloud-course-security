package com.apryshchepa.spring.cloud.security.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.security.oauth2.resource.AuthoritiesExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.FixedAuthoritiesExtractor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.BaseOAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

import java.util.*;

public class CustomUserInfoTokenServices implements ResourceServerTokenServices {

    private static final Log LOGGER = LogFactory.getLog(CustomUserInfoTokenServices.class);

    private static final String[] PRINCIPAL_KEYS = new String[]{"user", "username",
            "userid", "user_id", "login", "id", "name"};

    private final String userInfoEndpointUrl;

    private final String clientId;

    private OAuth2RestOperations restTemplate;

    private String tokenType = DefaultOAuth2AccessToken.BEARER_TYPE;

    private AuthoritiesExtractor authoritiesExtractor = new FixedAuthoritiesExtractor();

    public CustomUserInfoTokenServices(String userInfoEndpointUrl, String clientId) {
        this.userInfoEndpointUrl = userInfoEndpointUrl;
        this.clientId = clientId;
        this.restTemplate = getRestTemplate(clientId);
    }

    private OAuth2RestOperations getRestTemplate(String clientId) {
        BaseOAuth2ProtectedResourceDetails resource = new BaseOAuth2ProtectedResourceDetails();
        resource.setClientId(clientId);
        return new OAuth2RestTemplate(resource);
    }

    @Override
    public OAuth2Authentication loadAuthentication(String accessToken)
            throws AuthenticationException, InvalidTokenException {
        Map<String, Object> map = getMap(this.userInfoEndpointUrl, accessToken);
        return extractAuthentication(map);
    }

    @Override
    public OAuth2AccessToken readAccessToken(String accessToken) {
        throw new UnsupportedOperationException("Not supported: read access token");
    }

    private OAuth2Authentication extractAuthentication(Map<String, Object> map) {
        Object principal = getPrincipal(map);
        OAuth2Request request = getRequest(map);
        List<GrantedAuthority> authorities = this.authoritiesExtractor.extractAuthorities(map);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                principal, "N/A", authorities);
        token.setDetails(map);
        return new OAuth2Authentication(request, token);
    }

    private Object getPrincipal(Map<String, Object> map) {
        for (String key : PRINCIPAL_KEYS) {
            if (map.containsKey(key)) {
                return map.get(key);
            }
        }
        return "unknown";
    }

    @SuppressWarnings({"unchecked"})
    private OAuth2Request getRequest(Map<String, Object> map) {
        Map<String, Object> request = (Map<String, Object>) map.get("oauth2Request");

        String clientId = (String) request.get("clientId");
        Set<String> scope = new HashSet<>(request.containsKey("scope") ?
                (Collection<String>) request.get("scope") : Collections.emptySet());

        return new OAuth2Request(null, clientId, null, true, scope,
                null, null, null, null);
    }

    @SuppressWarnings({"unchecked"})
    private Map<String, Object> getMap(String path, String accessToken) {
        LOGGER.info("Getting user info from: " + path);
        try {
            OAuth2AccessToken existingToken = this.restTemplate.getOAuth2ClientContext()
                    .getAccessToken();
            if (existingToken == null || !accessToken.equals(existingToken.getValue())) {
                DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken(accessToken);
                token.setTokenType(this.tokenType);
                this.restTemplate.getOAuth2ClientContext().setAccessToken(token);
            }
            return this.restTemplate.getForEntity(path, Map.class).getBody();
        } catch (Exception exp) {
            LOGGER.error("Could not fetch user details: " + exp.getClass() + ", "
                    + exp.getMessage());
            throw new InvalidTokenException(accessToken);
        }
    }
}