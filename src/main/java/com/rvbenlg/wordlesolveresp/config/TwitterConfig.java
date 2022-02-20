package com.rvbenlg.wordlesolveresp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:twitter.properties")
public class TwitterConfig {

    @Value("${access.token}")
    private String accessToken;

    @Value("${access.token.secret}")
    private String accessTokenSecret;

    @Value("${api.key}")
    private String apiKey;

    @Value("${api.key.secret}")
    private String apiKeySecret;

    public String getAccessToken() {
        return accessToken;
    }

    public String getAccessTokenSecret() {
        return accessTokenSecret;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getApiKeySecret() {
        return apiKeySecret;
    }
}
