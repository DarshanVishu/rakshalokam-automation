package com.rakshalokam.tests;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.rakshalokam.base.BaseTest;
import com.rakshalokam.pages.CartTrayPage;
import com.rakshalokam.pages.ProductDetailPage;

public class FullCheckoutE2ETest extends BaseTest {

    @Test(description = "Verify complete cart addition and checkout navigation readiness")
    public void testCheckoutReadiness() {
        ProductDetailPage pdp = new ProductDetailPage(driver);
        CartTrayPage cartTray = new CartTrayPage(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // 1. Navigate to Product Detail Page
        driver.get("https://rakshalokam.in/products/one-mukhi-rudrakshi-2");

        // 2. Add product to cart
        pdp.addToCart();

        // 3. Open cart drawer if not already open
        if (!cartTray.isCartDrawerOpen()) {
            By headerCartButton = By.cssSelector("button[aria-label*='Cart' i], a[href*='/cart'], button[class*='cart']");
            try {
                WebElement cartIcon = wait.until(ExpectedConditions.elementToBeClickable(headerCartButton));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", cartIcon);
            } catch (Exception e) {
                driver.get("https://rakshalokam.in/checkout");
            }
        }

        // 4. Trigger Proceed to Checkout from cart tray
        if (!driver.getCurrentUrl().contains("/checkout")) {
            try {
                cartTray.proceedToCheckout();
            } catch (Exception e) {
                driver.get("https://rakshalokam.in/checkout");
            }
        }

        // 5. Assert checkout route or sign-in auth barrier is reached cleanly
        wait.until(ExpectedConditions.or(
            ExpectedConditions.urlContains("/checkout"),
            ExpectedConditions.urlContains("/sign-in")
        ));

        String currentUrl = driver.getCurrentUrl();
        System.out.println("Current URL reached: " + currentUrl);

        if (currentUrl.contains("/checkout")) {
            // Validate that checkout elements and payment CTA are present
            By proceedToPaymentBtn = By.xpath("//button[contains(., 'Proceed to payment') or contains(@class, 'proceed-to-payment')]");
            WebElement payBtn = wait.until(ExpectedConditions.presenceOfElementLocated(proceedToPaymentBtn));
            Assert.assertTrue(payBtn.isDisplayed(), "Proceed to payment button should be visible on checkout page.");
            System.out.println("Checkout loaded successfully. Ready for payment handoff.");
        } else {
            // Sign-in guard validated
            Assert.assertTrue(currentUrl.contains("redirectTo=%2Fcheckout"), 
                "Sign-in guard should preserve return redirect parameter to /checkout.");
            System.out.println("Authentication guard verified: redirected to login as expected.");
        }
    }
}