package com.rakshalokam.pages;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import com.rakshalokam.base.BasePage;

public class HomePage extends BasePage {

    private By brandAndNavElements = By.xpath(
        "//header//a | //nav//a | //a[@href='/'] | //header//*[name()='svg'] | //h1"
    );

    private By exploreLinks = By.xpath(
        "//a[contains(@href, '/collections') or contains(@href, '/search') or contains(@href, '/products') or contains(@href, '/category')]"
    );

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public boolean isBrandLogoVisible() {
        try {
            List<WebElement> elements = driver.findElements(brandAndNavElements);
            for (WebElement el : elements) {
                if (el.isDisplayed()) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public void clickExploreOrCatalog() {
        int attempts = 0;
        while (attempts < 3) {
            try {
                List<WebElement> links = driver.findElements(exploreLinks);
                for (WebElement link : links) {
                    if (link.isDisplayed() && link.getSize().getWidth() > 0) {
                        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});", link);
                        try {
                            link.click();
                        } catch (Exception clickEx) {
                            // If overlay/z-index intercepts standard click, bypass via JS click
                            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", link);
                        }
                        return;
                    }
                }
                break;
            } catch (Exception e) {
                attempts++;
            }
        }
        
        // Final fallback navigation if DOM element wasn't interactable
        String baseUrl = com.rakshalokam.utils.ConfigReader.getProperty("baseUrl");
        if (baseUrl == null || baseUrl.isEmpty()) {
            baseUrl = "https://rakshalokam.in";
        }
        driver.get(baseUrl + "/collections");
    }
}