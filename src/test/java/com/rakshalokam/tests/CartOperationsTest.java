package com.rakshalokam.tests;

import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import com.rakshalokam.base.BaseTest;
import com.rakshalokam.pages.CartTrayPage;
import com.rakshalokam.pages.ProductDetailPage;
import com.rakshalokam.utils.TestListener;

@Listeners(TestListener.class)
public class CartOperationsTest extends BaseTest {

    @Test(description = "Verify adding product to cart, opening the drawer, and removing it")
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
}