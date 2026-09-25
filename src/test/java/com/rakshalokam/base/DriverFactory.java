package com.rakshalokam.base;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import com.rakshalokam.utils.ConfigReader;

import io.github.bonigarcia.wdm.WebDriverManager;

public class DriverFactory {
    private static final ThreadLocal<WebDriver> tlDriver = new ThreadLocal<>();

    public static synchronized void initDriver() {
        String cliHeadless = System.getProperty("headless");
        String configHeadless = ConfigReader.getProperty("headless");
        boolean isHeadless = (cliHeadless != null)
                ? Boolean.parseBoolean(cliHeadless.trim())
                : (configHeadless != null && Boolean.parseBoolean(configHeadless.trim()));

        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        if (isHeadless) {
            options.addArguments("--headless=new");
        }
        // Essential flags for Linux / Docker / CI runners
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-notifications");
        options.addArguments("--remote-allow-origins=*");

        WebDriver driver = new ChromeDriver(options);
        driver.manage().window().maximize();

        String timeoutStr = ConfigReader.getProperty("explicitTimeout");
        long timeout = (timeoutStr != null) ? Long.parseLong(timeoutStr.trim()) : 20;
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(timeout));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));

        tlDriver.set(driver);
    }

    public static synchronized WebDriver getDriver() {
        return tlDriver.get();
    }

    public static synchronized void quitDriver() {
        if (tlDriver.get() != null) {
            tlDriver.get().quit();
            tlDriver.remove();
        }
    }
}