package com.rvbenlg.wordlesolveresp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WordleSolverEspApplication {

    public static void main(String[] args) {
        SpringApplication.run(WordleSolverEspApplication.class, args);
    }

}
