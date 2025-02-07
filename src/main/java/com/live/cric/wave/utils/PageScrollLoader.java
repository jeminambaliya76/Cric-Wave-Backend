package com.live.cric.wave.utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PageScrollLoader {
    private static final Logger logger = LoggerFactory.getLogger(PageScrollLoader.class);

    public static String loadPageSource( String url, int maxScrolls, int waitTimeMillis) throws InterruptedException {
        WebDriver driver = null;

            // Set up WebDriver
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless");
            options.addArguments("--disable-gpu", "--window-size=1920,1200", "--ignore-certificate-errors");
            driver = new ChromeDriver(options);

        driver.get(url);
        logger.info("Navigated to URL: {}", url);

        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        int scrollCount = 0;

        while (scrollCount < maxScrolls) {
            long initialHeight = (Long) jsExecutor.executeScript("return document.body.scrollHeight");
            jsExecutor.executeScript("window.scrollBy(0, document.body.scrollHeight)");
            Thread.sleep(waitTimeMillis);
            long newHeight = (Long) jsExecutor.executeScript("return document.body.scrollHeight");

            scrollCount++;
            if (initialHeight == newHeight) {
                logger.info("No new content loaded after {} scrolls.", scrollCount);
                break;
            }
        }

        logger.info("Completed scrolling after {} scrolls.", scrollCount);
        return driver.getPageSource();
    }
}
