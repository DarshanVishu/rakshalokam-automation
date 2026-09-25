package com.rakshalokam.base;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

public class PlaywrightBaseTest {
    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext context;
    protected Page page;

    @BeforeClass
    public void setUpPlaywright() {
        playwright = Playwright.create();
        // Use local installed Google Chrome to avoid downloading from azureedge.net
        browser = playwright.chromium().launch(
            new BrowserType.LaunchOptions()
                .setHeadless(false)
                .setChannel("chrome")
        );
        context = browser.newContext();
        page = context.newPage();
    }

    @AfterClass
    public void tearDownPlaywright() {
        if (context != null) context.close();
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }
}