package com.rakshalokam.pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ProductDetailPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By addToCartButton = By.xpath(
        "//button[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'add to cart')] | //button[@type='submit' and contains(., 'Cart')]"
    );
    private final By productTitle = By.xpath("//h1");

    public ProductDetailPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public boolean isAddToCartPresent() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(addToCartButton)).isDisplayed();
    }

    public String getProductTitle() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(productTitle)).getText();
    }

    public void addToCart() {
        WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(addToCartButton));
        
        // 1. Scroll directly to center to clear sticky headers/footers
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});", btn);

        // 2. Perform safe click with JavaScript fallback to bypass any overlay intercept
        try {
            wait.until(ExpectedConditions.elementToBeClickable(btn)).click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
    }
}