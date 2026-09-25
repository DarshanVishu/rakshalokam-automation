package com.rakshalokam.tests;

import java.time.Duration;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import com.rakshalokam.base.BaseTest;
import com.rakshalokam.pages.SearchPage;
import com.rakshalokam.utils.TestListener;

@Listeners(TestListener.class)
public class SearchAndFilterTest extends BaseTest {

    @Test(description = "Verify product search returns relevant items and allows navigation to PDP")
    public void testSearchAndSelectProduct() {
        SearchPage searchPage = new SearchPage(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // 1. Open home page
        driver.get("https://rakshalokam.in");

        // 2. Search for keyword
        searchPage.searchForProduct("rudraksha");

        // 3. Verify search results display items
        Assert.assertTrue(searchPage.hasSearchResults(), "Search should display product cards for 'rudraksha'.");
        System.out.println("Search results loaded successfully.");

        // 4. Select first product and log target URL
        String targetUrl = searchPage.selectFirstProduct();
        System.out.println("Target Product URL: " + targetUrl);

        // 5. Confirm navigation away from the search page
        wait.until(d -> !d.getCurrentUrl().contains("/search?"));
        String currentUrl = driver.getCurrentUrl();

        Assert.assertFalse(currentUrl.contains("/search?"), "Should navigate away from search page to product detail page.");
        System.out.println("Landed on Product Page: " + currentUrl);
    }
}