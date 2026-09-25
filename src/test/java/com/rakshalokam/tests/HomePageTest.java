package com.rakshalokam.tests;

import java.time.Duration;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.rakshalokam.base.BaseTest;
import com.rakshalokam.pages.HomePage;

public class HomePageTest extends BaseTest {

    @Test(priority = 1, description = "Verify that storefront mounts and title matches")
    public void testStorefrontBrandingAndMount() {
        HomePage homePage = new HomePage(driver);

        // 1. Verify page title
        String pageTitle = driver.getTitle();
        System.out.println("Page title: " + pageTitle);
        Assert.assertTrue(
            pageTitle.toLowerCase().contains("rakshalokam"),
            "Expected page title to contain 'Rakshalokam', found: " + pageTitle
        );

        // 2. Verify logo / nav presence
        Assert.assertTrue(
            homePage.isBrandLogoVisible(),
            "Storefront brand anchor or top navigation must be visible."
        );
    }

    @Test(priority = 2, description = "Verify catalog or collection navigation")
    public void testNavigateToCatalogOrProduct() {
        HomePage homePage = new HomePage(driver);

        homePage.clickExploreOrCatalog();

        // Wait up to 10 seconds for Remix client-side router transition
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        boolean urlChanged = wait.until(d -> !d.getCurrentUrl().equals("http://localhost:8002/"));

        System.out.println("URL after transition: " + driver.getCurrentUrl());
        Assert.assertTrue(urlChanged, "Browser URL must change after clicking catalog/explore link.");
    }
}