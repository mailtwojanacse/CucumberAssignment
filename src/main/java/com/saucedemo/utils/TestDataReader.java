package com.saucedemo.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import java.util.Properties;

public class TestDataReader {

    private static final String CONFIG_PATH = "src/test/resources/config/config.properties";
    private static final String USERS_JSON_PATH = "src/test/resources/testdata/users.json";
    private static final String PRODUCTS_JSON_PATH = "src/test/resources/testdata/products.json";
    private static final String CHECKOUT_JSON_PATH = "src/test/resources/testdata/checkout.json";

    private final Properties properties = new Properties();

    public TestDataReader() {
        loadConfig();
    }

    /** Loads config.properties */
    private void loadConfig() {
        try (FileInputStream input = new FileInputStream(CONFIG_PATH)) {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties: " + e.getMessage());
        }
    }

    // ------------------------- CONFIG METHODS ---------------------------- //

    public String getBaseUrl() {
        return properties.getProperty("base.url");
    }

    public String getBrowser() {
        return properties.getProperty("browser", "chrome");
    }

    public boolean isHeadless() {
        return Boolean.parseBoolean(properties.getProperty("headless", "false"));
    }

    public int getTimeout() {
        return Integer.parseInt(properties.getProperty("timeout", "10"));
    }

    // ------------------------- JSON READER UTILITY ---------------------------- //

    private JSONObject readJsonFile(String path) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(path)));
            return new JSONObject(content);
        } catch (IOException e) {
            throw new RuntimeException("Error reading JSON file: " + path + " - " + e.getMessage());
        }
    }

    // ------------------------- USERS DATA ---------------------------- //

    public Map<String, String> getValidUser() {
        JSONObject root = readJsonFile(USERS_JSON_PATH);
        JSONArray validUsers = root.getJSONArray("validUsers");
        JSONObject user = validUsers.getJSONObject(0);

        Map<String, String> userMap = new HashMap<>();
        userMap.put("username", user.getString("username"));
        userMap.put("password", user.getString("password"));
        return userMap;
    }

    public List<Map<String, String>> getInvalidUsers() {
        JSONObject root = readJsonFile(USERS_JSON_PATH);
        JSONArray invalidUsers = root.getJSONArray("invalidUsers");

        List<Map<String, String>> usersList = new ArrayList<>();
        for (int i = 0; i < invalidUsers.length(); i++) {
            JSONObject user = invalidUsers.getJSONObject(i);
            Map<String, String> map = new HashMap<>();
            for (String key : user.keySet()) {
                map.put(key, user.get(key).toString());
            }
            usersList.add(map);
        }
        return usersList;
    }

    // ------------------------- PRODUCTS DATA ---------------------------- //

    public List<Map<String, Object>> getProducts() {
        JSONObject root = readJsonFile(PRODUCTS_JSON_PATH);
        JSONArray products = root.getJSONArray("products");

        List<Map<String, Object>> productList = new ArrayList<>();
        for (int i = 0; i < products.length(); i++) {
            JSONObject item = products.getJSONObject(i);
            Map<String, Object> map = new HashMap<>();
            for (String key : item.keySet()) {
                map.put(key, item.get(key));
            }
            productList.add(map);
        }
        return productList;
    }

    public List<String> getProductNames() {
        List<Map<String, Object>> products = getProducts();
        List<String> names = new ArrayList<>();
        for (Map<String, Object> p : products) {
            names.add(p.get("name").toString());
        }
        return names;
    }

    public List<Double> getProductPrices() {
        List<Map<String, Object>> products = getProducts();
        List<Double> prices = new ArrayList<>();
        for (Map<String, Object> p : products) {
            prices.add(Double.parseDouble(p.get("price").toString()));
        }
        return prices;
    }

    // ------------------------- CHECKOUT DATA ---------------------------- //

    public Map<String, String> getValidCheckout() {
        JSONObject root = readJsonFile(CHECKOUT_JSON_PATH);
        JSONArray validArray = root.getJSONArray("validCheckout");
        JSONObject checkout = validArray.getJSONObject(0);

        Map<String, String> checkoutMap = new HashMap<>();
        for (String key : checkout.keySet()) {
            checkoutMap.put(key, checkout.get(key).toString());
        }
        return checkoutMap;
    }

    public Map<String, String> getInvalidCheckout() {
        JSONObject root = readJsonFile(CHECKOUT_JSON_PATH);
        JSONArray invalidArray = root.getJSONArray("invalidCheckout");
        JSONObject checkout = invalidArray.getJSONObject(0);

        Map<String, String> checkoutMap = new HashMap<>();
        for (String key : checkout.keySet()) {
            checkoutMap.put(key, checkout.get(key).toString());
        }
        return checkoutMap;
    }
}
