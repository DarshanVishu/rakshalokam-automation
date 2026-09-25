package com.rakshalokam.base;

import java.time.Duration;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import io.github.bonigarcia.wdm.WebDriverManager;

public class BaseTest {
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    protected WebDriver driver;

    public WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    @BeforeMethod
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--remote-allow-origins=*");

        WebDriver localDriver = new ChromeDriver(options);
        localDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        localDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));

        driverThreadLocal.set(localDriver);
        this.driver = localDriver;
    }

    @AfterMethod
    public void tearDown() {
        WebDriver currentDriver = driverThreadLocal.get();
        if (currentDriver != null) {
            currentDriver.quit();
            driverThreadLocal.remove();
        }
    }
}