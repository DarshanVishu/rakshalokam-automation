package com.rakshalokam.tests;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import com.rakshalokam.base.BaseTest;
import com.rakshalokam.pages.SignInPage;
import com.rakshalokam.utils.TestListener;

@Listeners(TestListener.class)
public class SignInTest extends BaseTest {

    @Test(description = "Verify user OTP sign-in flow and dashboard landing")
    public void testUserOtpLogin() {
        SignInPage signInPage = new SignInPage(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(25));

        // 1. Open sign-in page
        driver.get("https://rakshalokam.in/sign-in");

        // 2. Perform interactive console OTP login
        signInPage.loginWithMobileOtp("7204911922");

        // 3. Wait for redirect to account page
        wait.until(ExpectedConditions.urlContains("/account"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/account"), "User should be redirected to the /account dashboard upon OTP verification.");

        // 4. Assert dashboard elements are visible
        WebElement signOutBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//button[contains(., 'Sign out')] | //a[contains(., 'Sign out')]")
        ));
        Assert.assertTrue(signOutBtn.isDisplayed(), "'Sign out' button should be visible on devotee account dashboard.");

        System.out.println("Sign-In verification PASSED! Landed on: " + driver.getCurrentUrl());
    }
}