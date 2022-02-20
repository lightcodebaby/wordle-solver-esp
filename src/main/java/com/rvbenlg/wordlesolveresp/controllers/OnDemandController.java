package com.rvbenlg.wordlesolveresp.controllers;

import com.rvbenlg.wordlesolveresp.services.SeleniumService;
import com.rvbenlg.wordlesolveresp.services.TwitterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;

@RestController
@RequestMapping("/on-demand")
public class OnDemandController {

    private final SeleniumService seleniumService;
    private final TwitterService twitterService;

    public OnDemandController(SeleniumService seleniumService, TwitterService twitterService) {
        this.seleniumService = seleniumService;
        this.twitterService = twitterService;
    }

    @GetMapping("/selenium")
    public ResponseEntity<String> onDemandSelenium() throws MalformedURLException {
        return ResponseEntity.ok(seleniumService.autoSolveWordle());
    }

    @PostMapping("/twitter")
    public void onDemandTwitter() {
        twitterService.tweet("Hola mundo");
    }

}
