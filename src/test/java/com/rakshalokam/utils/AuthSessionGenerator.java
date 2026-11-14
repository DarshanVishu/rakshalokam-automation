package com.rakshalokam.utils;

import java.nio.file.Paths;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

public class AuthSessionGenerator {

    public static void main(String[] args) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                    .setHeadless(false)); // Headed so you can enter the phone number and OTP

            BrowserContext context = browser.newContext();
            Page page = context.newPage();

            System.out.println("Navigating to Sign-In page...");
            page.navigate("https://rakshalokam.in/sign-in");

            System.out.println(">>> Please enter your mobile number and OTP directly in the open browser window. <<<");

            // Wait until login completes and the URL moves away from /sign-in
            page.waitForURL(url -> !url.contains("/sign-in"), new Page.WaitForURLOptions().setTimeout(120000));

            System.out.println("Login detected! Saving authenticated session state to storageState.json...");
            context.storageState(new BrowserContext.StorageStateOptions().setPath(Paths.get("storageState.json")));

            System.out.println("Session saved successfully as storageState.json. You can close the browser.");
            browser.close();
        }
    }
}