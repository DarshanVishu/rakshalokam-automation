package com.rakshalokam.pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class PaymentPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    // Locators for Razorpay container and payment methods
    private final By razorpayIframe = By.cssSelector("iframe.razorpay-checkout-frame, iframe[name^='rzp']");
    private final By netbankingOption = By.xpath("//*[contains(text(), 'Netbanking') or contains(@data-testid, 'netbanking')]");
    private final By sbiBankOption = By.xpath("//*[contains(text(), 'SBI') or contains(text(), 'State Bank') or contains(@data-testid, 'bank-SBIN')]");
    private final By payNowButton = By.cssSelector("button[id*='submit'], button[type='submit'], button[class*='btn-pay']");
    private final By successButton = By.xpath("//button[contains(., 'Success')]");

    public PaymentPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(25));
    }

    public PaymentPage switchToRazorpayFrame() {
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(razorpayIframe));
        System.out.println("Switched inside Razorpay checkout iframe.");
        return this;
    }

    public PaymentPage payWithTestNetBanking() {
        try {
            WebElement nbOption = wait.until(ExpectedConditions.elementToBeClickable(netbankingOption));
            nbOption.click();
        } catch (Exception e) {
            WebElement nbOption = driver.findElement(netbankingOption);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", nbOption);
        }

        try {
            WebElement bank = wait.until(ExpectedConditions.elementToBeClickable(sbiBankOption));
            bank.click();
        } catch (Exception e) {
            WebElement bank = driver.findElement(sbiBankOption);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", bank);
        }

        try {
            WebElement payBtn = wait.until(ExpectedConditions.elementToBeClickable(payNowButton));
            payBtn.click();
        } catch (Exception ignored) {
        }
        return this;
    }

    public void completeTestPayment() {
        // If Razorpay opens a new window/tab for the dummy bank simulator
        String originalWindow = driver.getWindowHandle();
        
        for (String windowHandle : driver.getWindowHandles()) {
            if (!windowHandle.equals(originalWindow)) {
                driver.switchTo().window(windowHandle);
                break;
            }
        }

        // Click the green "Success" button on the Razorpay test banking page
        try {
            WebElement successBtn = wait.until(ExpectedConditions.elementToBeClickable(successButton));
            successBtn.click();
        } catch (Exception ignored) {
        }

        // Return to main application window
        driver.switchTo().window(originalWindow);
        driver.switchTo().defaultContent();
        System.out.println("Payment simulation completed. Waiting for redirect...");
    }
}