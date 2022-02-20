package com.rvbenlg.wordlesolveresp.services.impl;

import com.rvbenlg.wordlesolveresp.config.TwitterConfig;
import com.rvbenlg.wordlesolveresp.constants.Values;
import com.rvbenlg.wordlesolveresp.services.TwitterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

@Service
public class TwitterServiceImpl implements TwitterService {

    private final TwitterConfig twitterConfig;

    private final Logger logger = LoggerFactory.getLogger(TwitterServiceImpl.class);

    public TwitterServiceImpl(TwitterConfig twitterConfig) {
        this.twitterConfig = twitterConfig;
    }

    @Override
    public void tweet(String text) {
        logger.info(Values.POSTING_TWEET_LOG, text);
        boolean done = false;
        int tries = 0;
        do {
            tries++;
            try {
                ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
                configurationBuilder.setDebugEnabled(true)
                        .setOAuthAccessToken(twitterConfig.getAccessToken())
                        .setOAuthAccessTokenSecret(twitterConfig.getAccessTokenSecret())
                        .setOAuthConsumerKey(twitterConfig.getApiKey())
                        .setOAuthConsumerSecret(twitterConfig.getApiKeySecret());
                TwitterFactory twitterFactory = new TwitterFactory(configurationBuilder.build());
                Twitter twitter = twitterFactory.getInstance();
                twitter.updateStatus(text);
                done = true;
            } catch (Exception e) {
                logger.error(Values.ERROR_POSTING_TWEET_LOG, e.toString());
            }
        } while (!done && tries < 5);


    }

}
