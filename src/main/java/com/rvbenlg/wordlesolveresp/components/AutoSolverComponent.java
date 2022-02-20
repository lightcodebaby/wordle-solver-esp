package com.rvbenlg.wordlesolveresp.components;

import com.rvbenlg.wordlesolveresp.constants.Values;
import com.rvbenlg.wordlesolveresp.services.SeleniumService;
import com.rvbenlg.wordlesolveresp.services.TwitterService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class AutoSolverComponent {

    private final SeleniumService seleniumService;
    private final TwitterService twitterService;

    public AutoSolverComponent(SeleniumService seleniumService, TwitterService twitterService) {
        this.seleniumService = seleniumService;
        this.twitterService = twitterService;
    }

    @Scheduled(cron = "0 17 18 * * *", zone = "Europe/Madrid")
    public void solveWordle() throws MalformedURLException {
        String solution = seleniumService.autoSolveWordle();
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern(Values.DATE_FORMATTER_PATTERN));
        String tweet = String.format(Values.SOLUTION_TWEET, date, solution.toUpperCase(), Values.WORDLE_ESP_URL);
        twitterService.tweet(tweet);
    }
}
