package com.saucedemo.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class WebDriverManager {
    private static WebDriver driver;

    public static WebDriver getDriver() {
        if (driver == null) {
            String browser = ConfigReader.getBrowser();
            boolean headless = ConfigReader.isHeadless();

            switch (browser.toLowerCase()) {
                case "chrome":
                    Map<String, Object> prefs = new HashMap<>();
                    prefs.put("credentials_enable_service", false);
                    prefs.put("profile.password_manager_enabled", false);
                    prefs.put("profile.default_content_setting_values.notifications", 2);
                    prefs.put("profile.default_content_setting_values.popups", 0);
                    prefs.put("profile.default_content_setting_values.cookies", 1);
                    prefs.put("safebrowsing.enabled", false);
                    prefs.put("safebrowsing.disable_download_protection", true);
                    ChromeOptions chromeOptions = new ChromeOptions();
                    chromeOptions.setExperimentalOption("prefs", prefs);
                    if (headless) {
                        chromeOptions.addArguments("--headless");

                    }
                    chromeOptions.addArguments("--disable-features=PrivacySandboxSettings3,PrivacySandboxAdsAPIs,PrivacySandboxSettings4");
                    chromeOptions.addArguments("--disable-features=UserDataRetention,InterestCohortAPI,FledgeInterestGroups,TopicsAPI,FirstPartySets");
                    chromeOptions.addArguments("--disable-site-isolation-trials");
                    chromeOptions.addArguments("--disable-client-side-phishing-detection");
                    chromeOptions.addArguments("--disable-notifications");
                    chromeOptions.addArguments("--disable-popup-blocking");
                    chromeOptions.addArguments("--disable-extensions");
                    chromeOptions.addArguments("--no-sandbox");
                    chromeOptions.addArguments("--disable-infobars");
                    chromeOptions.addArguments("--disable-save-password-bubble");
                    chromeOptions.addArguments("--disable-password-generation");
                    chromeOptions.addArguments("--disable-features=PasswordManagerOnboarding,PasswordLeakDetection,AutofillServerCommunication");
                    chromeOptions.addArguments("--start-maximized");

                    chromeOptions.setExperimentalOption("excludeSwitches", Arrays.asList("enable-automation"));
                    driver = new ChromeDriver(chromeOptions);
                    break;
                case "firefox":

                    break;
                default:
                    throw new IllegalArgumentException("Unsupported browser: " + browser);
            }
        }
        return driver;
    }

    public static void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}