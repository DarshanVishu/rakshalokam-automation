package com.rakshalokam.pages;

import java.time.Duration;
import java.util.List;
import javax.swing.JOptionPane;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SignInPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    // Mobile input field and Send OTP button
    private final By phoneInput = By.cssSelector("input[type='text'], input[type='tel']");
    private final By sendOtpBtn = By.xpath("//button[contains(., 'Send OTP')]");
    
    // Resend countdown or Verify button indicator confirming OTP form has appeared
    private final By otpScreenIndicator = By.xpath("//button[contains(., 'Verify')] | //*[contains(text(), 'Resend in')]");

    public SignInPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(25));
    }

    public void loginWithMobileOtp(String mobileNumber) {
        // 1. Enter mobile number
        WebElement userEl = wait.until(ExpectedConditions.visibilityOfElementLocated(phoneInput));
        userEl.clear();
        userEl.sendKeys(mobileNumber);

        // 2. Click Send OTP button
        WebElement sendBtn = wait.until(ExpectedConditions.elementToBeClickable(sendOtpBtn));
        try {
            sendBtn.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", sendBtn);
        }

        // 3. Ensure the UI has transitioned to the OTP step
        wait.until(ExpectedConditions.presenceOfElementLocated(otpScreenIndicator));

        // 4. Prompt GUI Dialog Box for manual OTP entry
        String enteredOtp = JOptionPane.showInputDialog(
            null,
            "Enter OTP sent to " + mobileNumber + ":",
            "Rakshalokam OTP Verification",
            JOptionPane.QUESTION_MESSAGE
        );

        if (enteredOtp == null || enteredOtp.trim().isEmpty()) {
            throw new RuntimeException("OTP input was cancelled or empty.");
        }

        System.out.println(">>> Submitting OTP: [" + enteredOtp.trim() + "] <<<");

        // 5. Select the visible input field (the last input on the form)
        List<WebElement> inputs = driver.findElements(By.tagName("input"));
        WebElement targetOtpField = null;
        for (int i = inputs.size() - 1; i >= 0; i--) {
            if (inputs.get(i).isDisplayed()) {
                targetOtpField = inputs.get(i);
                break;
            }
        }

        if (targetOtpField == null) {
            targetOtpField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(),'Enter OTP')]/following::input[1]")
            ));
        }

        ((JavascriptExecutor) driver).executeScript("arguments[0].focus();", targetOtpField);
        targetOtpField.clear();
        targetOtpField.sendKeys(enteredOtp.trim());

        // 6. Click 'Verify & Log In' button
        WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[contains(., 'Verify & Log In') or contains(., 'Verify')]")
        ));
        try {
            submitBtn.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submitBtn);
        }
    }
}