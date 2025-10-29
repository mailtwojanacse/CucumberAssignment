package com.saucedemo.stepdefinitions;

import io.cucumber.java.en.*;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import com.saucedemo.pages.*;
import com.saucedemo.utils.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.*;

import java.util.stream.Collectors;

public class SaceDemoSteps {

    WebDriver driver;
    LoginPage loginPage;
    ProductsPage productsPage;
    CartPage cartPage;
    CheckoutPage checkoutPage;
    OrderConfirmationPage confirmationPage;
    TestDataReader data = new TestDataReader();

    List<String> displayedProductNames;
    List<Double> displayedProductPrices;

    @Given("the user is on the login page")
    public void userOnLoginPage() {
        driver = WebDriverManager.getDriver();
        driver.get(data.getBaseUrl());
        loginPage = new LoginPage(driver);
    }

    @Given("the user is on the products page")
    public void userIsOnProductsPage() {
        userLoggedIn();
    }


    @When("the user logs in with valid credentials")
    public void loginWithValidCredentials() {
        Map<String, String> validUser = data.getValidUser();
        loginPage.login(validUser.get("username"), validUser.get("password"));
        productsPage = new ProductsPage(driver);
    }

    @Then("the user should be redirected to the products page")
    public void verifyProductsPage() {
        Assert.assertTrue("Products page not loaded", driver.getCurrentUrl().contains("inventory"));
    }

    @When("the user logs in with invalid credentials")
    public void loginWithInvalidCredentials() {
        Map<String, String> invalidUser = data.getInvalidUsers().stream()
                .filter(u -> u.get("expectedResult").equals("invalid"))
                .findFirst().orElse(null);
        loginPage.login(invalidUser.get("username"), invalidUser.get("password"));
    }

    @Then("the user should see an invalid credentials error message")
    public void verifyInvalidLoginError() {
        Map<String, String> invalidUser = data.getInvalidUsers().stream()
                .filter(u -> u.get("expectedResult").equals("invalid"))
                .findFirst().orElse(null);
        Assert.assertTrue(loginPage.getErrorMessage().contains(invalidUser.get("expectedMessage")));
    }

    @When("the user logs in with a locked-out account")
    public void loginWithLockedUser() {
        Map<String, String> lockedUser = data.getInvalidUsers().stream()
                .filter(u -> u.get("expectedResult").equals("locked_out"))
                .findFirst().orElse(null);
        loginPage.login(lockedUser.get("username"), lockedUser.get("password"));
    }

    @Then("the user should see a locked-out error message")
    public void verifyLockedOutError() {
        Map<String, String> lockedUser = data.getInvalidUsers().stream()
                .filter(u -> u.get("expectedResult").equals("locked_out"))
                .findFirst().orElse(null);
        Assert.assertTrue(loginPage.getErrorMessage().contains(lockedUser.get("expectedMessage")));
    }

    @Given("the user is logged in")
    public void userLoggedIn() {
        userOnLoginPage();
        loginWithValidCredentials();
    }

    @When("the user adds multiple products to the cart")
    public void addProductsToCart() {
        List<Map<String, Object>> products = data.getProducts();
        for (Map<String, Object> product : products) {
            productsPage.addProduct(product.get("name").toString());
        }
    }

    @When("the user removes some products from the cart")
    public void removeProductsFromCart() {
        productsPage.removeProduct("Sauce Labs Bike Light");
    }

    @Then("the cart should display the correct product count and total amount")
    public void verifyCartCount() {
        Assert.assertEquals("Cart count mismatch", 1, productsPage.getCartCount());
    }

    @When("the user sorts products by name A to Z")
    public void sortAtoZ() {
        productsPage.sortBy("Name (A to Z)");
        displayedProductNames = driver.findElements(By.className("inventory_item_name"))
                .stream().map(WebElement::getText).collect(Collectors.toList());
    }

    @Then("the products should be displayed in alphabetical order")
    public void verifyAtoZSorting() {
        List<String> sortedNames = new ArrayList<>(displayedProductNames);
        Collections.sort(sortedNames);
        Assert.assertEquals("Products are not sorted alphabetically (A–Z)", sortedNames, displayedProductNames);
    }

    @When("the user sorts products by price low to high")
    public void sortLowToHigh() {
        productsPage.sortBy("Price (low to high)");
        displayedProductPrices = driver.findElements(By.className("inventory_item_price"))
                .stream().map(WebElement::getText)
                .map(p -> Double.parseDouble(p.replace("$", "").trim()))
                .collect(Collectors.toList());
    }

    @Then("the products should be displayed in ascending price order")
    public void verifyPriceSorting() {
        List<Double> sortedPrices = new ArrayList<>(displayedProductPrices);
        Collections.sort(sortedPrices);
        Assert.assertEquals("Products are not sorted by price (Low–High)", sortedPrices, displayedProductPrices);
    }

    @Given("the user has products in the cart")
    public void userHasCartItems() {
        userLoggedIn();
        addProductsToCart();
    }

    @When("the user proceeds to checkout")
    public void proceedToCheckout() {
        cartPage = new CartPage(driver);
        cartPage.clickCheckout();
        checkoutPage = new CheckoutPage(driver);
    }

    @When("fills all required details")
    public void fillCheckoutDetails() {
        Map<String, String> validCheckout = data.getValidCheckout();
        checkoutPage.fillCheckoutInfo(validCheckout.get("firstName"), validCheckout.get("lastName"), validCheckout.get("postalCode"));
        checkoutPage.continueCheckout();
    }

    @When("completes the order")
    public void completeOrder() {
        checkoutPage.finishOrder();
        confirmationPage = new OrderConfirmationPage(driver);
    }

    @Then("the user should see a successful order confirmation message")
    public void verifyOrderSuccess() {
        Assert.assertTrue(confirmationPage.getSuccessMessage().contains("Thank you"));
    }

    @When("the user proceeds to checkout without filling required fields")
    public void incompleteCheckout() {
        cartPage = new CartPage(driver);
        cartPage.clickCheckout();
        checkoutPage = new CheckoutPage(driver);
        checkoutPage.continueCheckout();
    }

    @Then("an error message should be displayed")
    public void verifyErrorOnCheckout() {
        Map<String, String> invalidCheckout = data.getInvalidCheckout();
        Assert.assertTrue(checkoutPage.getErrorMessage().contains(invalidCheckout.get("expectedError")));
    }

    @When("the user accesses the cart without logging in")
    public void accessCartWithoutLogin() {
        driver = WebDriverManager.getDriver();
        driver.get(data.getBaseUrl() + "/cart.html");
    }

    @Then("the user should be redirected to the login page")
    public void redirectedToLogin() {
        Assert.assertTrue(driver.getCurrentUrl().contains("login"));
    }

    @When("the user logs out")
    public void logoutUser() {
        By menuBtn = By.id("react-burger-menu-btn");
        By logoutLink = By.id("logout_sidebar_link");

        // Wait for menu button to be clickable
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.elementToBeClickable(menuBtn));

        // Click on menu to open the sidebar
        driver.findElement(menuBtn).click();

        // Wait for sidebar animation to finish (small pause helps avoid ElementNotInteractableException)
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Wait until logout link is visible and clickable
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.visibilityOfElementLocated(logoutLink));
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.elementToBeClickable(logoutLink));

        // Scroll into view in case sidebar scroll is needed
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", driver.findElement(logoutLink));

        // Click logout link safely using JS to avoid animation issues
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", driver.findElement(logoutLink));
    }



    @Then("the user should be redirected to the login page after logout")
    public void verifyLogout() {
        loginPage = new LoginPage(driver);
        Assert.assertTrue(loginPage.isOnLoginPage());
        driver.quit();
    }
}
