package com.saucedemo.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;

public class BasePage {

    protected WebDriver driver;
    protected WebDriverWait wait;
    private static final int DEFAULT_TIMEOUT = 10;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
    }

    /** Waits for element to be visible */
    protected WebElement waitForVisibility(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /** Waits for element to be clickable */
    protected WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    /** Clicks on a visible and clickable element */
    public void click(By locator) {
        try {
            waitForClickable(locator).click();
        } catch (StaleElementReferenceException e) {
            // Retry once if stale element
            waitForClickable(locator).click();
        } catch (TimeoutException e) {
            throw new RuntimeException("Element not clickable: " + locator);
        }
    }

    /** Types text into a field (clears before typing) */
    public void type(By locator, String text) {
        WebElement element = waitForVisibility(locator);
        element.clear();
        element.sendKeys(text);
    }

    /** Retrieves text from a visible element */
    public String getText(By locator) {
        return waitForVisibility(locator).getText().trim();
    }

    /** Returns true if element is displayed */
    public boolean isDisplayed(By locator) {
        try {
            return waitForVisibility(locator).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    /** Selects dropdown option by visible text */
    public void selectByVisibleText(By locator, String optionText) {
        WebElement dropdownElement = waitForVisibility(locator);
        Select dropdown = new Select(dropdownElement);
        dropdown.selectByVisibleText(optionText);
    }

    /** Gets all visible elements for a given locator */
    public List<WebElement> getElements(By locator) {
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    /** Scrolls the page to the element using JavaScript */
    public void scrollToElement(By locator) {
        WebElement element = waitForVisibility(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    /** Checks if the element contains specific text */
    public boolean elementContainsText(By locator, String expectedText) {
        String actual = getText(locator);
        return actual.contains(expectedText);
    }

    /** Safe click using JavaScript (backup for stubborn elements) */
    public void jsClick(By locator) {
        WebElement element = waitForVisibility(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    /** Waits until page fully loads */
    public void waitForPageLoad() {
        new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT))
                .until(webDriver -> ((JavascriptExecutor) webDriver)
                        .executeScript("return document.readyState").equals("complete"));
    }
}
