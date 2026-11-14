package com.rakshalokam.tests;

import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.FrameLocator;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.Cookie;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.SameSiteAttribute;
import com.rakshalokam.utils.ConfigReader;

public class RazorpayPaymentPlaywrightTest {

    @Test(priority = 1, description = "Verify checkout flow, complete Razorpay test payment, and settle order")
    public void testCompleteRazorpayCheckoutAndSettleOrder() {
        String baseUrl = getBaseUrl();
        boolean headlessMode = isHeadless();
        double timeoutMs = getTimeoutMs();
        String sessionCookie = getSessionCookie();
        String domain = baseUrl.replaceFirst("^https?://", "").split("/")[0];

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                    .setHeadless(headlessMode)
                    .setSlowMo(headlessMode ? 0 : 400));

            BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                    .setViewportSize(1366, 768));

            context.addCookies(List.of(
                    new Cookie("vendure_remix_session", sessionCookie)
                            .setDomain(domain)
                            .setPath("/")
                            .setHttpOnly(true)
                            .setSameSite(SameSiteAttribute.LAX)
            ));

            Page page = context.newPage();
            page.setDefaultTimeout(timeoutMs);

            // 1. Navigate to product & add to cart
            System.out.println("[STEP 1] Navigating to product page...");
            page.navigate(baseUrl + "/products/one-mukhi-rudrakshi-2");
            page.waitForLoadState(LoadState.DOMCONTENTLOADED);

            System.out.println("[STEP 2] Adding product to cart...");
            Locator addToCartBtn = page.locator("button:has-text('Add to Cart'), button:has-text('ADD TO CART')").first();
            addToCartBtn.waitFor(new Locator.WaitForOptions().setTimeout(timeoutMs));
            addToCartBtn.click();
            page.waitForTimeout(2000);

            // 2. Open checkout & trigger Razorpay modal
            FrameLocator rzpFrame = proceedToRazorpayModal(page, baseUrl, timeoutMs);

            // 3. Select Netbanking -> Test Bank inside Razorpay iframe
            System.out.println("[STEP 6] Selecting Netbanking inside Razorpay modal...");
            Locator netbankingTab = rzpFrame.getByText("Netbanking", new FrameLocator.GetByTextOptions().setExact(true)).first();
            netbankingTab.waitFor(new Locator.WaitForOptions().setTimeout(timeoutMs));
            netbankingTab.click();

            System.out.println("[STEP 6b] Selecting test bank...");
            Locator bankOption = rzpFrame.locator("text=/State Bank of India|SBI|HDFC|ICICI/i").first();
            bankOption.waitFor(new Locator.WaitForOptions().setTimeout(timeoutMs));
            bankOption.click();

            // 4. Poll reliably for Razorpay Bank popup and click Success
            System.out.println("[STEP 7] Locating Razorpay Bank popup and clicking Success...");
            Page bankPage = waitForRazorpayBankPopup(context, page, rzpFrame, timeoutMs);
            Assert.assertNotNull(bankPage, "Razorpay mock bank popup window did not open.");
            bankPage.waitForLoadState(LoadState.DOMCONTENTLOADED);

            Locator successBtn = bankPage.locator("button:has-text('Success'), input[value='Success']").first();
            successBtn.waitFor(new Locator.WaitForOptions().setTimeout(timeoutMs));
            successBtn.click();
            System.out.println("[STEP 7] Clicked 'Success' on Razorpay mock bank page!");

            // 5. Verify order confirmation and settlement
            System.out.println("[STEP 8] Awaiting order settlement and confirmation page...");
            page.waitForURL(url -> url.contains("/confirmation") || url.contains("/success") || url.contains("/orders/") || !url.contains("/checkout/payment"),
                    new Page.WaitForURLOptions().setTimeout(30000));

            String confirmationUrl = page.url();
            System.out.println("[SUCCESS] Order completed successfully! Landed URL: " + confirmationUrl);
            Assert.assertTrue(confirmationUrl.contains("/confirmation"),
                    "Expected confirmation route, but landed on: " + confirmationUrl);

            String[] urlSegments = confirmationUrl.split("/");
            String orderCode = urlSegments[urlSegments.length - 1];
            System.out.println("[ORDER SETTLED] Generated Vendure Order Code: " + orderCode);
            Assert.assertFalse(orderCode.isBlank(), "Order confirmation URL did not contain a valid order code.");

            context.close();
            browser.close();
        }
    }

    @Test(priority = 2, description = "Verify declined Razorpay test payment keeps user on checkout and does not settle order")
    public void testDeclinedRazorpayPaymentDoesNotSettleOrder() {
        String baseUrl = getBaseUrl();
        boolean headlessMode = isHeadless();
        double timeoutMs = getTimeoutMs();
        String sessionCookie = getSessionCookie();
        String domain = baseUrl.replaceFirst("^https?://", "").split("/")[0];

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                    .setHeadless(headlessMode)
                    .setSlowMo(headlessMode ? 0 : 400));

            BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                    .setViewportSize(1366, 768));

            context.addCookies(List.of(
                    new Cookie("vendure_remix_session", sessionCookie)
                            .setDomain(domain)
                            .setPath("/")
                            .setHttpOnly(true)
                            .setSameSite(SameSiteAttribute.LAX)
            ));

            Page page = context.newPage();
            page.setDefaultTimeout(timeoutMs);

            // 1. Add product & navigate to checkout
            page.navigate(baseUrl + "/products/one-mukhi-rudrakshi-2");
            page.waitForLoadState(LoadState.DOMCONTENTLOADED);
            Locator addToCartBtn = page.locator("button:has-text('Add to Cart'), button:has-text('ADD TO CART')").first();
            addToCartBtn.waitFor(new Locator.WaitForOptions().setTimeout(timeoutMs));
            addToCartBtn.click();
            page.waitForTimeout(2000);

            FrameLocator rzpFrame = proceedToRazorpayModal(page, baseUrl, timeoutMs);

            // 2. Open Razorpay modal -> Netbanking -> Test Bank
            rzpFrame.getByText("Netbanking", new FrameLocator.GetByTextOptions().setExact(true)).first().click();
            rzpFrame.locator("text=/State Bank of India|SBI|HDFC|ICICI/i").first().click();

            // 3. Poll reliably for Razorpay Bank popup and click Failure
            Page bankPage = waitForRazorpayBankPopup(context, page, rzpFrame, timeoutMs);
            Assert.assertNotNull(bankPage, "Razorpay mock bank popup window did not open.");
            bankPage.waitForLoadState(LoadState.DOMCONTENTLOADED);

            Locator failureBtn = bankPage.locator("button:has-text('Failure'), input[value='Failure']").first();
            failureBtn.waitFor(new Locator.WaitForOptions().setTimeout(timeoutMs));
            failureBtn.click();
            System.out.println("[NEGATIVE TEST] Clicked 'Failure' on Razorpay mock bank page.");

            page.waitForTimeout(3000);

            // 4. Assert storefront did NOT navigate to /confirmation
            String currentUrl = page.url();
            System.out.println("[NEGATIVE TEST] Post-failure URL: " + currentUrl);
            Assert.assertFalse(currentUrl.contains("/confirmation"),
                    "Declined payment should not redirect to order confirmation page!");

            context.close();
            browser.close();
        }
    }

    private FrameLocator proceedToRazorpayModal(Page page, String baseUrl, double timeoutMs) {
        System.out.println("[STEP 3] Opening checkout route...");
        page.navigate(baseUrl + "/checkout");
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        page.waitForTimeout(2000);

        String activeUrl = page.url();
        System.out.println("[DEBUG] Active URL at checkout: " + activeUrl);
        Assert.assertFalse(activeUrl.contains("/sign-in"),
                "Session cookie expired or rejected; redirected to /sign-in. Update test.session.cookie in config.properties.");

        fillCheckoutAddressIfPresent(page);

        System.out.println("[STEP 4] Clicking Proceed to Payment / Place Order button...");
        Locator proceedBtn = page.locator("button:has-text('Proceed to Payment'), button:has-text('Place Order'), button:has-text('Pay Now'), button:has-text('Continue')").first();
        if (proceedBtn.isVisible()) {
            proceedBtn.click();
            page.waitForTimeout(2000);
        }

        if (page.url().contains("/checkout/payment")) {
            System.out.println("[STEP 4b] Landed on /checkout/payment. Checking for visible payment trigger...");
            page.waitForLoadState(LoadState.DOMCONTENTLOADED);
            page.waitForTimeout(2000);

            Locator finalPayTrigger = page.locator(
                    "main button:visible, button:has-text('Pay'):visible, button:has-text('Razorpay'):visible, button:has-text('Place Order'):visible, button:has-text('Proceed'):visible"
            ).filter(new Locator.FilterOptions().setHasNot(page.locator("[aria-label='Search']"))).first();

            if (finalPayTrigger.isVisible()) {
                System.out.println("[STEP 4b] Clicking visible payment button: " + finalPayTrigger.innerText().trim());
                finalPayTrigger.click();
                page.waitForTimeout(2000);
            }
        }

        System.out.println("[STEP 5] Waiting for Razorpay modal iframe...");
        Locator rzpIframeLocator = page.locator("iframe.razorpay-checkout-frame, iframe[name*='rzp']").first();
        rzpIframeLocator.waitFor(new Locator.WaitForOptions().setTimeout(timeoutMs));
        Assert.assertTrue(rzpIframeLocator.isVisible(), "Razorpay iframe did not appear on checkout.");

        return page.frameLocator("iframe.razorpay-checkout-frame, iframe[name*='rzp']").first();
    }

    private Page waitForRazorpayBankPopup(BrowserContext context, Page mainPage, FrameLocator rzpFrame, double timeoutMs) {
        long deadline = System.currentTimeMillis() + (long) timeoutMs;
        boolean clickedFallbackPay = false;

        while (System.currentTimeMillis() < deadline) {
            for (Page p : context.pages()) {
                if (p != mainPage && p.url().contains("razorpay.com")) {
                    return p;
                }
            }
            if (!clickedFallbackPay) {
                try {
                    Locator modalPayBtn = rzpFrame.locator("button:has-text('Pay Now'), button:has-text('Pay')").first();
                    if (modalPayBtn.isVisible()) {
                        modalPayBtn.click();
                        clickedFallbackPay = true;
                    }
                } catch (Exception ignored) {
                }
            }
            mainPage.waitForTimeout(500);
        }
        return null;
    }

    private void fillCheckoutAddressIfPresent(Page page) {
        try {
            Locator addressInput = page.locator("input[name='streetLine1'], input[name='address1']").first();
            if (addressInput.isVisible()) {
                addressInput.fill("123 MG Road");
                page.locator("input[name='city']").first().fill("Bengaluru");
                page.locator("input[name='postalCode'], input[name='zipCode']").first().fill("560001");
            }

            Locator submitAddressBtn = page.locator("button:has-text('Save'), button:has-text('Continue'), button:has-text('Next')").first();
            if (submitAddressBtn.isVisible()) {
                submitAddressBtn.click();
                page.waitForTimeout(2000);
            }
        } catch (Exception ignored) {
        }
    }

    private String getBaseUrl() {
        String baseUrl = System.getProperty("baseUrl", ConfigReader.getProperty("baseUrl"));
        if (baseUrl == null || baseUrl.isBlank()) {
            baseUrl = "https://rakshalokam.in";
        }
        return baseUrl.replaceAll("/$", "");
    }

    private boolean isHeadless() {
        String cliHeadless = System.getProperty("headless");
        String configHeadless = ConfigReader.getProperty("headless");
        return (cliHeadless != null)
                ? Boolean.parseBoolean(cliHeadless.trim())
                : (configHeadless != null && Boolean.parseBoolean(configHeadless.trim()));
    }

    private double getTimeoutMs() {
        String timeoutStr = ConfigReader.getProperty("explicitTimeout");
        return (timeoutStr != null && !timeoutStr.isBlank())
                ? Double.parseDouble(timeoutStr.trim()) * 1000
                : 20000;
    }

    private String getSessionCookie() {
        String sessionCookie = System.getProperty("test.session.cookie", ConfigReader.getProperty("test.session.cookie"));
        Assert.assertNotNull(sessionCookie, "test.session.cookie is missing in config.properties or system properties.");
        Assert.assertFalse(sessionCookie.isBlank(), "test.session.cookie cannot be blank.");
        return sessionCookie.trim();
    }
}