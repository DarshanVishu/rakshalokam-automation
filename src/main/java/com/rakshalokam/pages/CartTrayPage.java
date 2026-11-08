package com.rakshalokam.pages;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CartTrayPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Resilient standard W3C locators
    private final By cartToggleTrigger = By.xpath(
        "//header//button[contains(@aria-label, 'cart') or contains(@aria-label, 'Cart') or contains(@aria-label, 'bag') or contains(@aria-label, 'Bag')] | " +
        "//header//a[contains(@href, 'cart')] | " +
        "//header//*[local-name()='svg']/ancestor::button[1] | " +
        "//*[@data-testid='cart-icon' or @data-testid='cart-drawer-trigger']"
    );

    private final By checkoutButton = By.xpath(
        "//button[contains(translate(., 'checkout', 'CHECKOUT'), 'CHECKOUT')] | " +
        "//a[contains(@href, 'checkout') or contains(translate(., 'checkout', 'CHECKOUT'), 'CHECKOUT')] | " +
        "//button[contains(., 'Proceed') or contains(., 'BUY')]"
    );

    private final By removeButton = By.xpath(
        "//button[contains(@aria-label, 'Remove') or contains(., 'Remove') or contains(., 'Delete')] | " +
        "//button[contains(@class, 'remove') or contains(@class, 'delete')]"
    );

    private final By emptyCartMessage = By.xpath(
        "//*[contains(translate(., 'EMPTY', 'empty'), 'your cart is empty') or contains(translate(., 'EMPTY', 'empty'), 'cart is empty')]"
    );

    private final By cartItemsList = By.xpath(
        "//button[contains(@aria-label, 'Remove') or contains(., 'Remove')] | " +
        "//div[contains(@class, 'cart')]//li | " +
        "//aside//ul/li | " +
        "//div[contains(@class, 'cart-item') or @data-testid='cart-item']"
    );

    private final By itemQuantityDisplay = By.xpath(
        "//input[@type='number' or contains(@class, 'qty') or contains(@class, 'quantity')] | " +
        "//span[contains(@class, 'qty') or contains(@class, 'quantity')] | " +
        "//*[@data-testid='item-quantity' or @data-testid='cart-item-qty']"
    );

    private final By plusButton = By.xpath(
        "//button[contains(@aria-label, 'Increase') or contains(@aria-label, 'increase') or contains(@class, 'plus') or text()='+'] | " +
        "//*[@data-testid='increase-quantity']"
    );

    private final By minusButton = By.xpath(
        "//button[contains(@aria-label, 'Decrease') or contains(@aria-label, 'decrease') or contains(@class, 'minus') or text()='-'] | " +
        "//*[@data-testid='decrease-quantity']"
    );

    private final By subtotalDisplay = By.xpath(
        "//*[contains(@class, 'subtotal') or contains(@class, 'total')]//span[contains(text(), '₹')] | " +
        "//*[@data-testid='cart-subtotal'] | " +
        "//*[contains(text(), 'Subtotal')]/following-sibling::*"
    );

    public CartTrayPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    /**
     * Ensures the cart drawer is open.
     */
    public void openDrawer() {
        if (isCartDrawerOpen()) {
            return;
        }

        try {
            WebElement trigger = wait.until(ExpectedConditions.presenceOfElementLocated(cartToggleTrigger));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true); arguments[0].click();", trigger);
            Thread.sleep(800);
        } catch (Exception e) {
            try {
                WebElement fallbackBtn = driver.findElement(By.xpath("//header//button[last()]"));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", fallbackBtn);
                Thread.sleep(800);
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * Checks if the cart drawer or its interactive elements are currently visible.
     */
    public boolean isCartDrawerOpen() {
        try {
            List<WebElement> checkouts = driver.findElements(checkoutButton);
            if (!checkouts.isEmpty() && checkouts.get(0).isDisplayed()) {
                return true;
            }
            List<WebElement> removes = driver.findElements(removeButton);
            if (!removes.isEmpty() && removes.get(0).isDisplayed()) {
                return true;
            }
            List<WebElement> empties = driver.findElements(emptyCartMessage);
            return !empties.isEmpty() && empties.get(0).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Reads the current quantity of the first item line in the cart tray.
     */
    public int getItemQuantity() {
        try {
            WebElement qtyElem = wait.until(ExpectedConditions.presenceOfElementLocated(itemQuantityDisplay));
            String value = qtyElem.getAttribute("value");
            if (value == null || value.trim().isEmpty()) {
                value = qtyElem.getText().trim();
            }
            value = value.replaceAll("[^0-9]", "");
            return Integer.parseInt(value);
        } catch (Exception e) {
            System.err.println("Could not parse quantity from cart drawer: " + e.getMessage());
            return 1;
        }
    }

    /**
     * Returns total distinct item count in the active drawer.
     */
    public int getCartItemCount() {
        try {
            List<WebElement> items = driver.findElements(cartItemsList);
            return items.size();
        } catch (Exception ignored) {
            return 0;
        }
    }

    /**
     * Clicks the '+' stepper inside the cart drawer to increase item count.
     */
    public void incrementItemQuantity() {
        WebElement plus = wait.until(ExpectedConditions.elementToBeClickable(plusButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", plus);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Clicks the '-' stepper inside the cart drawer to decrease item count.
     */
    public void decrementItemQuantity() {
        WebElement minus = wait.until(ExpectedConditions.elementToBeClickable(minusButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", minus);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Reads current subtotal string from cart drawer.
     */
    public String getSubtotalText() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(subtotalDisplay)).getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Removes the first active item line in the cart tray.
     */
    public void removeItemFromCart() {
        WebElement removeBtn = wait.until(ExpectedConditions.presenceOfElementLocated(removeButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true); arguments[0].click();", removeBtn);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Verifies if the cart drawer reflects an empty state.
     */
    public boolean isCartEmpty() {
        try {
            List<WebElement> empties = driver.findElements(emptyCartMessage);
            if (!empties.isEmpty() && empties.get(0).isDisplayed()) {
                return true;
            }
            List<WebElement> remainingItems = driver.findElements(removeButton);
            return remainingItems.isEmpty();
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Triggers the checkout progression button directly inside the cart drawer.
     */
    public void proceedToCheckout() {
        WebElement checkoutBtn = wait.until(ExpectedConditions.presenceOfElementLocated(checkoutButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true); arguments[0].click();", checkoutBtn);
    }
}