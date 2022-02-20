package com.rvbenlg.wordlesolveresp.services.impl;

import com.rvbenlg.wordlesolveresp.constants.Values;
import com.rvbenlg.wordlesolveresp.models.AbsentLetter;
import com.rvbenlg.wordlesolveresp.models.PresentLetter;
import com.rvbenlg.wordlesolveresp.services.SeleniumService;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class SeleniumServiceImpl implements SeleniumService {

    private WebDriver webDriver;
    private String solution;
    private int tries;
    private List<Integer> undiscoveredPositions;
    private List<PresentLetter> presentLetters;
    private List<AbsentLetter> absentLetters;
    private List<String> filteredWords;

    private final Logger logger = LoggerFactory.getLogger(SeleniumServiceImpl.class);

    @Override
    public String autoSolveWordle() throws MalformedURLException {
        do {
            resetSolution();
            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.addArguments("--incognito");
            logger.info("Connecting to remote web driver: {}", Values.REMOTE_URL_CHROME);
            webDriver = new RemoteWebDriver(new URL(Values.REMOTE_URL_CHROME), chromeOptions);
            try {
                solveWordle();
            } catch (Exception e) {
                logger.error(Values.ERROR_SOLVING_WORDLE_LOG, e.toString());
            }
            webDriver.quit();
        } while (solution.contains("_"));
        logger.info(Values.TODAYS_WORDLE_SOLUTION_LOG, solution);
        return solution;
    }


    private void solveWordle() {
        logger.info(Values.AUTO_SOLVING_WORDLE_LOG);
        navigateToWordle();
        clickOnPlay();
        play();
    }

    private void navigateToWordle() {
        logger.info(Values.NAVIGATING_TO_LOG, Values.WORDLE_ESP_URL);
        webDriver.navigate().to(Values.WORDLE_ESP_URL);
    }

    private void clickOnPlay() {
        logger.info(Values.CLICKING_ON_PLAY_BUTTON_LOG);
        WebElement webElement = webDriver.findElement(By.xpath(Values.PLAY_BUTTON_XPATH));
        webElement.click();
    }

    private void play() {
        String word = Values.FIRST_WORDS[(int) (Math.random() * Values.FIRST_WORDS.length)];
        while (solution.contains("_") && tries < 6) {
            tryWord(word);
            filterWords();
            presentLetters = new ArrayList<>();
            word = filteredWords.get((int) (Math.random() * filteredWords.size()));
        }
    }

    private void tryWord(String word) {
        logger.info("Trying word: {}", word);
        insertWord(word);
        checkWord();
    }

    private void insertWord(String word) {
        for (char c : word.toCharArray()) {
            logger.info(Values.CLICKING_ON_LETTER_BUTTON_LOG, c);
            WebElement webElement = webDriver.findElement(By.xpath(String.format(Values.LETTER_TO_INTRODUCE_XPATH, c)));
            webElement.click();
        }
        logger.info(Values.CLICKING_ON_PROCESS_WORD_BUTTON_LOG);
        WebElement webElement = webDriver.findElement(By.xpath(Values.PROCESS_WORD_XPATH));
        webElement.click();
    }

    private void checkWord() {
        logger.info(Values.CHECKING_INTRODUCED_WORD_LOG);
        List<WebElement> letters = webDriver.findElements(By.xpath(Values.WORD_TO_CHECK_XPATH)).get(tries).findElements(By.xpath(Values.LETTER_TO_CHECK_XPATH));
        for (int i = 0; i < letters.size(); i++) {
            WebElement letter = letters.get(i);
            if (letter.getAttribute(Values.CLASS_ATTRIBUTE).contains(Values.CORRECT_ATTRIBUTE)) {
                logger.debug(Values.LETTER_CORRECT_LOG, letter.getText());
                updateSolution(i, letter.getText().toLowerCase());
                int position = i;
                undiscoveredPositions.removeIf(integer -> integer.equals(position));
            } else if (letter.getAttribute(Values.CLASS_ATTRIBUTE).contains(Values.PRESENT_ATTRIBUTE)) {
                logger.debug(Values.LETTER_PRESENT_LOG, letter.getText());
                presentLetters.add(new PresentLetter(letter.getText().toLowerCase(), i));
            } else if (letter.getAttribute(Values.CLASS_ATTRIBUTE).contains(Values.ABSENT_ATTRIBUTE)) {
                logger.debug(Values.LETTER_ABSENT_LOG, letter.getText());
                absentLetters.add(new AbsentLetter(letter.getText().toLowerCase()));
            } else {
                deleteWord();
                tries--;
            }
        }
        tries++;
    }

    private void deleteWord() {
        logger.info(Values.DELETING_WORD_LOG);
        WebElement webElement = webDriver.findElement(By.xpath(String.format(Values.LETTER_TO_INTRODUCE_XPATH, Values.DELETE_LABEL)));
        for (int i = 0; i < 5; i++) {
            logger.debug(Values.CLICKING_ON_DELETE_LETTER_BUTTON_LOG);
            webElement.click();
        }
    }

    private void updateSolution(int position, String correct) {
        StringBuilder solutionBuilder = new StringBuilder();
        for (int i = 0; i < solution.length(); i++) {
            if (i != position) {
                solutionBuilder.append(solution.charAt(i));
            } else {
                solutionBuilder.append(correct);
            }
        }
        logger.debug(Values.UPDATING_SOLUTION_LOG, solution, solutionBuilder);
        solution = solutionBuilder.toString();
    }

    private void filterWords() {
        List<String> result = new ArrayList<>(filteredWords);
        for (AbsentLetter absentLetter : absentLetters) {
            result.removeIf(s -> !solution.contains(absentLetter.getLetter()) && s.contains(absentLetter.getLetter()));
        }
        for (PresentLetter presentLetter : presentLetters) {
            result.removeIf(s -> undiscoveredPositions.stream()
                    .filter(position -> position != presentLetter.getPosition())
                    .noneMatch(position -> s.substring(position, position + 1).equalsIgnoreCase(presentLetter.getLetter())));
        }
        for (int i = 0; i < solution.length(); i++) {
            int position = i;
            if (solution.charAt(i) != '_') {
                result.removeIf(s -> s.charAt(position) != solution.charAt(position));
            }
        }
        logger.debug(Values.FILTERING_WORDS_LOG, filteredWords, result);
        filteredWords = result;
    }

    private void resetSolution() {
        logger.debug(Values.INITIALIZING_VARIABLES_LOG);
        tries = 0;
        solution = "_____";
        undiscoveredPositions = new ArrayList<>(Arrays.asList(Values.POSITIONS));
        presentLetters = new ArrayList<>();
        absentLetters = new ArrayList<>();
        filteredWords = new ArrayList<>(Arrays.asList(Values.WORDS));
    }
}
