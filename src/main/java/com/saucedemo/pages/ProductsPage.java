package com.saucedemo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ProductsPage extends BasePage {
    private By inventoryItems = By.className("inventory_item");
    private By sortDropdown = By.className("product_sort_container");
    private By cartBadge = By.className("shopping_cart_badge");

    public ProductsPage(WebDriver driver) {
        super(driver);
    }

    public void addProduct(String productName) {
        click(By.xpath("//div[text()='" + productName + "']/ancestor::div[@class='inventory_item']//button"));
    }

    public void removeProduct(String productName) {
        click(By.xpath("//div[text()='" + productName + "']/ancestor::div[@class='inventory_item']//button"));
    }

    public void sortBy(String optionText) {
        selectByVisibleText(sortDropdown, optionText);
    }

    public int getCartCount() {
        String count = getText(cartBadge);
        return count.isEmpty() ? 0 : Integer.parseInt(count);
    }
}
