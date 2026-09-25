package com.rakshalokam.pages;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import com.rakshalokam.base.BasePage;

public class FilterComponent extends BasePage {

    // Dynamic locator for facet checkboxes or buttons across desktop and mobile sheets
    private By facetCheckbox(String filterOption) {
        return By.xpath(
            "//label[contains(., '" + filterOption + "')]//input[@type='checkbox'] | " +
            "//input[@type='checkbox' and @value='" + filterOption + "'] | " +
            "//button[contains(., '" + filterOption + "')]"
        );
    }

    // Product cards displayed inside the collection/search grid
    private By productItems = By.xpath(
        "//div[contains(@class, 'grid')]//a[contains(@href, '/products/') or contains(@href, '/product/')] | " +
        "//div[contains(@class, 'product-card')] | " +
        "//main//ul//li"
    );

    // Active filter tags or pills that appear once a filter is selected
    private By activeFilterChips = By.xpath(
        "//span[contains(@class, 'chip') or contains(@class, 'badge') or contains(@class, 'tag')] | " +
        "//button[contains(@aria-label, 'Remove')]"
    );

    public FilterComponent(WebDriver driver) {
        super(driver);
    }

    /**
     * Selects a facet option and scrolls it into view to avoid layout intercepts.
     */
    public void selectFacet(String filterOption) {
        By locator = facetCheckbox(filterOption);
        WebElement checkboxOrButton = wait.until(d -> {
            List<WebElement> els = d.findElements(locator);
            for (WebElement el : els) {
                if (el.isDisplayed() || el.isEnabled()) {
                    return el;
                }
            }
            return null;
        });

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", checkboxOrButton);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", checkboxOrButton);
    }

    /**
     * Returns the number of visible product items currently rendered in the catalog.
     */
    public int getVisibleProductCount() {
        try {
            // Brief stabilization wait for Remix client loader revalidation
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {}

        List<WebElement> items = driver.findElements(productItems);
        return (int) items.stream().filter(WebElement::isDisplayed).count();
    }

    /**
     * Verifies if the active filter badge/chip is visible in the filter tray.
     */
    public boolean hasActiveFilterChip(String filterOption) {
        try {
            List<WebElement> chips = driver.findElements(activeFilterChips);
            boolean matchedInChips = chips.stream()
                .filter(WebElement::isDisplayed)
                .anyMatch(el -> el.getText().toLowerCase().contains(filterOption.toLowerCase()));

            if (matchedInChips) {
                return true;
            }

            // Fallback for custom storefront chip implementations
            return isAnyVisible(By.xpath("//*[contains(text(), '" + filterOption + "')]"));
        } catch (Exception e) {
            return false;
        }
    }
}