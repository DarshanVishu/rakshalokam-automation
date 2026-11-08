package com.rakshalokam.pages;

import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ProductDetailPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    // Resilient locator covering standard buttons, data-testid, and text
    private final By addToCartButton = By.xpath(
        "//button[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'add to cart')] | " +
        "//button[@type='submit' and contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'cart')] | " +
        "//button[contains(@class, 'add-to-cart') or contains(@id, 'add-to-cart') or @data-testid='add-to-cart-button'] | " +
        "//button[translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='add']"
    );

    // Common close/backdrop buttons for the slide-out cart drawer
    private final By drawerCloseButtons = By.xpath(
        "//button[contains(@aria-label, 'Close') or contains(@aria-label, 'close')] | " +
        "//aside//button[contains(@class, 'close')] | " +
        "//*[contains(@class, 'drawer')]//button[1] | " +
        "//button[contains(., '✕') or contains(., '×')]"
    );

    private final By productTitle = By.xpath("//h1");

    public ProductDetailPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public boolean isAddToCartPresent() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(addToCartButton)).isDisplayed();
    }

    public String getProductTitle() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(productTitle)).getText();
    }

    /**
     * If the cart tray/drawer auto-opened after a previous click, dismiss it
     * so that the main product page and the Add to Cart button remain interactable.
     */
    public void closeDrawerIfBlocking() {
        try {
            List<WebElement> closeBtns = driver.findElements(drawerCloseButtons);
            for (WebElement close : closeBtns) {
                if (close.isDisplayed()) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", close);
                    Thread.sleep(600);
                    return;
                }
            }
            // Fallback: send ESC key to dismiss any open modal/drawer backdrop
            driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
            Thread.sleep(400);
        } catch (Exception ignored) {
        }
    }

    public void addToCart() {
        // 1. Clear any active drawer overlay that may have popped open
        closeDrawerIfBlocking();

        // 2. Wait for the button presence
        WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(addToCartButton));
        
        // 3. Scroll directly into the viewport center to avoid sticky header/footer obstructions
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});", btn);
        try {
            Thread.sleep(400);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }

        // 4. Click using standard click or fallback JS click
        try {
            wait.until(ExpectedConditions.elementToBeClickable(btn)).click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
    }
}