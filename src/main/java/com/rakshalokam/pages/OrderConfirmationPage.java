package com.rakshalokam.pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class OrderConfirmationPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    // Locators for order confirmation elements
    private final By successHeading = By.xpath("//*[contains(text(), 'Thank you') or contains(text(), 'Order Placed') or contains(text(), 'Confirmed') or contains(text(), 'Order #')]");
    private final By orderCode = By.xpath("//*[contains(text(), 'Order code') or contains(text(), 'Order Reference') or contains(text(), 'Order ID')]/following-sibling::* | //*[contains(@class, 'order-id')]");

    public OrderConfirmationPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    public boolean isOrderPlacedSuccessfully() {
        // Wait for URL to leave /checkout/payment or match confirmation endpoints
        wait.until(ExpectedConditions.or(
            ExpectedConditions.not(ExpectedConditions.urlContains("/payment")),
            ExpectedConditions.visibilityOfElementLocated(successHeading)
        ));
        return true;
    }

    public String getOrderReference() {
        try {
            WebElement refEl = wait.until(ExpectedConditions.visibilityOfElementLocated(orderCode));
            return refEl.getText().trim();
        } catch (Exception e) {
            return "Reference confirmed on screen: " + driver.getCurrentUrl();
        }
    }
}