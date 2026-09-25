package com.rakshalokam.pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CheckoutPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    // Contact & Guest Inputs
    private final By emailInput = By.cssSelector("input[name='emailAddress'], input[type='email'], input[name='email']");
    private final By phoneInput = By.cssSelector("input[name='phoneNumber'], input[type='tel'], input[name='phone']");
    private final By firstNameInput = By.cssSelector("input[name='firstName'], input[name='name']");
    private final By lastNameInput = By.cssSelector("input[name='lastName']");

    // Address Inputs
    private final By streetAddressInput = By.cssSelector("input[name='streetLine1'], textarea[name='streetLine1'], input[name='address']");
    private final By cityInput = By.cssSelector("input[name='city']");
    private final By provinceSelect = By.cssSelector("select[name='province'], select[name='state']");
    private final By provinceInput = By.cssSelector("input[name='province'], input[name='state']");
    private final By postalCodeInput = By.cssSelector("input[name='postalCode'], input[name='pincode']");

    // Action Buttons
    private final By continueButton = By.xpath("//button[@type='submit' or contains(., 'Continue') or contains(., 'Save Address') or contains(., 'Next')]");
    private final By shippingRadioBtn = By.cssSelector("input[name='shippingMethodId'], input[type='radio'][name*='shipping']");
    private final By proceedPaymentBtn = By.xpath("//button[contains(., 'Proceed to Payment') or contains(., 'Pay with Razorpay') or contains(., 'Place Order')]");

    // Razorpay Iframe
    private final By razorpayIframe = By.cssSelector("iframe.razorpay-checkout-frame, iframe[src*='razorpay']");

    public CheckoutPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void fillGuestCustomerDetails(String email, String phone, String firstName, String lastName) {
        if (!driver.findElements(emailInput).isEmpty()) {
            WebElement emailEl = wait.until(ExpectedConditions.visibilityOfElementLocated(emailInput));
            emailEl.clear();
            emailEl.sendKeys(email);
        }

        if (!driver.findElements(phoneInput).isEmpty()) {
            WebElement phoneEl = wait.until(ExpectedConditions.visibilityOfElementLocated(phoneInput));
            phoneEl.clear();
            phoneEl.sendKeys(phone);
        }

        if (!driver.findElements(firstNameInput).isEmpty()) {
            WebElement fnEl = wait.until(ExpectedConditions.visibilityOfElementLocated(firstNameInput));
            fnEl.clear();
            fnEl.sendKeys(firstName);
        }

        if (!driver.findElements(lastNameInput).isEmpty()) {
            WebElement lnEl = wait.until(ExpectedConditions.visibilityOfElementLocated(lastNameInput));
            lnEl.clear();
            lnEl.sendKeys(lastName);
        }
    }

    public void fillShippingAddress(String street, String city, String state, String pin) {
        WebElement streetEl = wait.until(ExpectedConditions.visibilityOfElementLocated(streetAddressInput));
        streetEl.clear();
        streetEl.sendKeys(street);

        WebElement cityEl = wait.until(ExpectedConditions.visibilityOfElementLocated(cityInput));
        cityEl.clear();
        cityEl.sendKeys(city);

        // State select or input fallback
        if (!driver.findElements(provinceSelect).isEmpty()) {
            Select dropdown = new Select(driver.findElement(provinceSelect));
            dropdown.selectByVisibleText(state);
        } else if (!driver.findElements(provinceInput).isEmpty()) {
            WebElement provEl = driver.findElement(provinceInput);
            provEl.clear();
            provEl.sendKeys(state);
        }

        WebElement pinEl = wait.until(ExpectedConditions.visibilityOfElementLocated(postalCodeInput));
        pinEl.clear();
        pinEl.sendKeys(pin);
    }

    public void submitAddressAndContinue() {
        WebElement submit = wait.until(ExpectedConditions.elementToBeClickable(continueButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", submit);
        submit.click();
    }

    public void selectFirstAvailableShippingMethod() {
        try {
            WebElement radio = wait.until(ExpectedConditions.elementToBeClickable(shippingRadioBtn));
            if (!radio.isSelected()) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", radio);
            }
        } catch (Exception e) {
            // Some configurations auto-select the default standard delivery method
            System.out.println("[INFO] Default shipping method already selected or unavailable.");
        }
    }

    public void proceedToPayment() {
        try {
            WebElement payBtn = wait.until(ExpectedConditions.elementToBeClickable(proceedPaymentBtn));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", payBtn);
            payBtn.click();
        } catch (Exception e) {
            submitAddressAndContinue();
        }
    }

    public boolean isRazorpayModalMounted() {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(razorpayIframe));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}