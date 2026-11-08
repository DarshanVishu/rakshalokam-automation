package com.rakshalokam.tests;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import com.rakshalokam.base.BaseTest;
import com.rakshalokam.pages.CartTrayPage;
import com.rakshalokam.pages.ProductDetailPage;
import com.rakshalokam.utils.TestListener;

@Listeners(TestListener.class)
public class CartOperationsTest extends BaseTest {

    @Test(priority = 1, description = "Verify adding product to cart, opening the drawer, and removing it")
    public void testAddAndRemoveItemFromCart() {
        ProductDetailPage pdp = new ProductDetailPage(driver);
        CartTrayPage cartTray = new CartTrayPage(driver);

        driver.get("https://rakshalokam.in/products/one-mukhi-rudrakshi-2");
        pdp.addToCart();

        cartTray.openDrawer();
        cartTray.removeItemFromCart();

        Assert.assertTrue(cartTray.isCartEmpty(), "Cart drawer should reflect empty state after removing the item.");
        System.out.println("Cart addition and removal successfully verified.");
    }

    @Test(priority = 2, description = "Verify that clicking Add to Cart 3 times increments item quantity to 3")
    public void testRepeatedAddToCartIncrementsQuantity() {
        ProductDetailPage pdp = new ProductDetailPage(driver);
        CartTrayPage cartTray = new CartTrayPage(driver);

        driver.get("https://rakshalokam.in/products/one-mukhi-rudrakshi-2");

        // Click Add to Cart 3 times
        for (int i = 1; i <= 3; i++) {
            pdp.addToCart();
            try { 
                Thread.sleep(1000); 
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }

        cartTray.openDrawer();
        int currentQuantity = cartTray.getItemQuantity();

        System.out.println("Observed quantity in cart: " + currentQuantity);
        Assert.assertEquals(currentQuantity, 3,
            "Defect Confirmed: Repeatedly adding product failed to increment quantity to 3. Found: " + currentQuantity);
    }

    @Test(priority = 3, description = "Verify cart retains and displays more than 2 distinct items")
    public void testCartHoldsMoreThanTwoDistinctItems() {
        ProductDetailPage pdp = new ProductDetailPage(driver);
        CartTrayPage cartTray = new CartTrayPage(driver);

        // Fetch products dynamically from storefront
        driver.get("https://rakshalokam.in");
        List<WebElement> productLinks = driver.findElements(By.xpath("//a[contains(@href, '/product') or contains(@href, '/item')]"));

        Assert.assertTrue(productLinks.size() >= 3, "Catalog has fewer than 3 products to test.");

        String[] targetUrls = new String[3];
        int count = 0;
        for (WebElement link : productLinks) {
            String href = link.getAttribute("href");
            if (href != null && href.contains("/product") && !containsUrl(targetUrls, href)) {
                targetUrls[count++] = href;
                if (count == 3) break;
            }
        }

        for (String url : targetUrls) {
            if (url != null) {
                driver.get(url);
                pdp.addToCart();
                try { 
                    Thread.sleep(1200); 
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        cartTray.openDrawer();
        int distinctItemCount = cartTray.getCartItemCount();

        System.out.println("Observed distinct items in cart: " + distinctItemCount);
        Assert.assertTrue(distinctItemCount >= 3,
            "Defect Confirmed: Cart failed to hold more than 2 distinct products. Found: " + distinctItemCount);
    }

    @Test(priority = 4, description = "Verify that in-cart '+' stepper increments quantity properly")
    public void testInCartQuantityStepperIncrements() {
        ProductDetailPage pdp = new ProductDetailPage(driver);
        CartTrayPage cartTray = new CartTrayPage(driver);

        driver.get("https://rakshalokam.in/products/one-mukhi-rudrakshi-2");
        pdp.addToCart();

        cartTray.openDrawer();
        int initialQty = cartTray.getItemQuantity();
        Assert.assertEquals(initialQty, 1, "Initial quantity in cart should be 1.");

        // Click '+' stepper inside the cart tray
        cartTray.incrementItemQuantity();

        int updatedQty = cartTray.getItemQuantity();
        System.out.println("Updated in-cart quantity: " + updatedQty);

        Assert.assertEquals(updatedQty, 2, "In-cart stepper failed to increment quantity to 2.");
        System.out.println("In-cart stepper increment successfully verified.");
    }

    private boolean containsUrl(String[] array, String url) {
        for (String item : array) {
            if (url.equals(item)) return true;
        }
        return false;
    }
}