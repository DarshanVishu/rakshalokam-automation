package com.rakshalokam.tests;

import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.rakshalokam.base.BaseTest;
import com.rakshalokam.pages.CartTrayPage;
import com.rakshalokam.pages.HomePage;
import com.rakshalokam.pages.ProductDetailPage;

public class CartSmokeTest extends BaseTest {

    @Test(priority = 1, description = "Verify that a user can select a product, add it to cart, and verify cart line items")
    public void testAddProductToCart() {
        HomePage homePage = new HomePage(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // 1. Navigate to collections/catalog
        homePage.clickExploreOrCatalog();

        // 2. Locate any clickable collection tile or product link (with StaleElement resilience)
        By anyCatalogCard = By.xpath(
            "//main//a[contains(@href, '/products/') or contains(@href, '/product/') or contains(@href, '/collections/')]"
        );

        wait.ignoring(StaleElementReferenceException.class).until(d -> {
            try {
                List<WebElement> els = d.findElements(anyCatalogCard);
                for (WebElement el : els) {
                    if (el.isDisplayed() && el.getSize().getWidth() > 0) {
                        ((JavascriptExecutor) d).executeScript("arguments[0].scrollIntoView({block: 'center'});", el);
                        ((JavascriptExecutor) d).executeScript("arguments[0].click();", el);
                        return true;
                    }
                }
            } catch (StaleElementReferenceException e) {
                return false; // Retry in next polling cycle when new DOM attaches
            }
            return false;
        });

        // 3. If we landed on a sub-collection (not yet PDP), click the actual product item
        if (!driver.getCurrentUrl().contains("/product")) {
            By productLink = By.xpath("//main//a[contains(@href, '/products/') or contains(@href, '/product/')]");
            
            wait.ignoring(StaleElementReferenceException.class).until(d -> {
                try {
                    WebElement prod = d.findElement(productLink);
                    if (prod.isDisplayed()) {
                        ((JavascriptExecutor) d).executeScript("arguments[0].scrollIntoView({block: 'center'});", prod);
                        ((JavascriptExecutor) d).executeScript("arguments[0].click();", prod);
                        return true;
                    }
                } catch (StaleElementReferenceException e) {
                    return false;
                }
                return false;
            });
        }

        // 4. Assert arrival on Product Detail Page
        wait.until(ExpectedConditions.urlContains("/product"));
        System.out.println("Landed on PDP: " + driver.getCurrentUrl());

        ProductDetailPage pdp = new ProductDetailPage(driver);
        Assert.assertTrue(pdp.isAddToCartPresent(), "Add to Cart CTA must be visible on the product detail page.");

        String title = pdp.getProductTitle();
        System.out.println("Adding product to cart: " + title);

     // 5. Trigger Add to Cart
        pdp.addToCart();

        // 6. Handle Cart Tray Drawer
        CartTrayPage cartTray = new CartTrayPage(driver);

        // If the drawer doesn't auto-open, click the cart icon in the header to open it
        if (!cartTray.isCartDrawerOpen()) {
            By headerCartButton = By.cssSelector("button[aria-label*='Cart'], button[aria-label*='cart'], a[href*='/cart']");
            try {
                WebElement cartIcon = wait.until(ExpectedConditions.elementToBeClickable(headerCartButton));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", cartIcon);
            } catch (Exception e) {
                // If direct click fails, fallback to pressing cart button via JS
                ((JavascriptExecutor) driver).executeScript("document.querySelector(\"button[aria-label*='cart' i], a[href*='cart']\").click();");
            }
        }

        // 7. Assert Cart Drawer is open and populated
        Assert.assertTrue(cartTray.isCartDrawerOpen(), "Cart drawer should be open and visible.");
        Assert.assertTrue(cartTray.getCartItemCount() > 0, "Cart line item count should be greater than zero."); }
}