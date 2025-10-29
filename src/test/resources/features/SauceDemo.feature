@Smoke
Feature: End-to-End User Journey on SauceDemo

  Scenario: Verify successful login with valid credentials
    Given the user is on the login page
    When the user logs in with valid credentials
    Then the user should be redirected to the products page
@Regression
  Scenario: Validate error messages for invalid credentials and locked-out user
    Given the user is on the login page
    When the user logs in with invalid credentials
    Then the user should see an invalid credentials error message
    When the user logs in with a locked-out account
    Then the user should see a locked-out error message
  @Regression
  Scenario: Add and remove multiple products from cart
    Given the user is logged in
    When the user adds multiple products to the cart
    And the user removes some products from the cart
    Then the cart should display the correct product count and total amount

  Scenario: Verify product sorting functionality
    Given the user is on the products page
    When the user sorts products by name A to Z
    Then the products should be displayed in alphabetical order
    When the user sorts products by price low to high
    Then the products should be displayed in ascending price order
  @Regression
  Scenario: Complete checkout flow successfully
    Given the user has products in the cart
    When the user proceeds to checkout
    And fills all required details
    And completes the order
    Then the user should see a successful order confirmation message

  Scenario: Validate negative and edge cases in checkout
    Given the user has products in the cart
    When the user proceeds to checkout without filling required fields
    Then an error message should be displayed
    When the user accesses the cart without logging in
    Then the user should be redirected to the login page

  Scenario: Verify logout functionality
    Given the user is logged in
    When the user logs out
    Then the user should be redirected to the login page
