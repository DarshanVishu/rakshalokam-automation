package com.rakshalokam.base;

import java.time.Duration;
import java.util.List;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.rakshalokam.utils.ConfigReader;

public abstract class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        int timeout = ConfigReader.getInt("explicitTimeout", 15);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
    }

    protected WebElement waitForVisibility(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected void click(By locator) {
        try {
            waitForClickable(locator).click();
        } catch (ElementClickInterceptedException | StaleElementReferenceException e) {
            // Self-healing scroll fallback
            WebElement el = driver.findElement(locator);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", el);
            el.click();
        }
    }

    protected void type(By locator, String text) {
        WebElement element = waitForVisibility(locator);
        element.clear();
        element.sendKeys(text);
    }

    protected boolean isAnyVisible(By locator) {
        try {
            List<WebElement> elements = driver.findElements(locator);
            return elements.stream().anyMatch(WebElement::isDisplayed);
        } catch (Exception e) {
            return false;
        }
    }

    public String getCurrentPageUrl() {
        return driver.getCurrentUrl();
    }
}