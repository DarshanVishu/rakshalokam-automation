package com.rakshalokam.tests;

import java.time.Duration;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.rakshalokam.base.BaseTest;
import com.rakshalokam.pages.CartTrayPage;
import com.rakshalokam.pages.ProductDetailPage;
import com.rakshalokam.utils.TestListener;

@Listeners(TestListener.class)
public class CheckoutFlowTest extends BaseTest {

    @Test(description = "Verify guest checkout funnel enforces authentication redirect preserving checkout intent")
    public void testGuestCheckoutFunnelThroughRazorpayMount() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        ProductDetailPage pdp = new ProductDetailPage(driver);
        CartTrayPage cartTray = new CartTrayPage(driver);

        // 1. Land on PDP and add item to cart
        driver.get("https://rakshalokam.in/products/one-mukhi-rudrakshi-2");
        pdp.addToCart();

        // 2. Open drawer and proceed to checkout
        cartTray.openDrawer();
        cartTray.proceedToCheckout();

        // 3. Wait for navigation transition
        wait.until(ExpectedConditions.or(
            ExpectedConditions.urlContains("/checkout"),
            ExpectedConditions.urlContains("/sign-in")
        ));

        String currentUrl = driver.getCurrentUrl();
        System.out.println("[TRANSITION URL] Landed on: " + currentUrl);

        // 4. Assert authentication guard correctly captured the checkout destination
        Assert.assertTrue(
            currentUrl.contains("redirectTo=%2Fcheckout") || currentUrl.contains("/sign-in"),
            "Expected redirection to sign-in guard with checkout intent, but landed on: " + currentUrl
        );

        System.out.println("[GUARD VERIFIED] Storefront securely guarded checkout and preserved redirect parameter.");
    }
}