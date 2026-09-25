package com.rakshalokam.pages;

import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SearchPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    // Matches any link on the page that could be an item card
    private final By allAnchorTags = By.tagName("a");

    public SearchPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    public void searchForProduct(String keyword) {
        // Direct route to search guarantees the catalog loads reliably
        driver.get("https://rakshalokam.in/search?q=" + keyword);

        // Wait for page hydration & readyState
        wait.until(d -> ((JavascriptExecutor) d).executeScript("return document.readyState").equals("complete"));

        // Wait until anchor links populate on the page
        wait.until(ExpectedConditions.presenceOfElementLocated(allAnchorTags));
    }

    public boolean hasSearchResults() {
        try {
            List<WebElement> anchors = driver.findElements(allAnchorTags);
            for (WebElement a : anchors) {
                String href = a.getAttribute("href");
                if (isValidProductOrCategoryHref(href)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public String selectFirstProduct() {
        // Locate the first genuine product/collection anchor that is visible
        WebElement targetCard = wait.until(d -> {
            List<WebElement> anchors = d.findElements(allAnchorTags);
            for (WebElement a : anchors) {
                try {
                    String href = a.getAttribute("href");
                    if (isValidProductOrCategoryHref(href) && a.isDisplayed()) {
                        return a;
                    }
                } catch (Exception ignored) {
                }
            }
            return null;
        });

        String targetUrl = targetCard.getAttribute("href");
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", targetCard);

        if (targetUrl != null && !targetUrl.isEmpty()) {
            driver.navigate().to(targetUrl);
            return targetUrl;
        } else {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", targetCard);
            return driver.getCurrentUrl();
        }
    }

    private boolean isValidProductOrCategoryHref(String href) {
        if (href == null || href.isEmpty()) {
            return false;
        }
        // Exclude 3rd-party integrations, static navigation, and utility routes
        if (href.contains("wa.me") || href.contains("whatsapp") || href.contains("facebook") || href.contains("instagram")) {
            return false;
        }
        if (href.endsWith("/search") || href.contains("/search?") || href.endsWith("/cart") || href.endsWith("/sign-in") || href.endsWith("/account")) {
            return false;
        }
        if (href.equals("https://rakshalokam.in") || href.equals("https://rakshalokam.in/")) {
            return false;
        }

        // Target catalog product or collection paths
        return href.contains("/products/") || href.contains("/collections/") || href.contains("/product/") || href.startsWith("https://rakshalokam.in/");
    }
}