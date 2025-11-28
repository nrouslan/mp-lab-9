package com.example.mp_lab_9.util;

import com.example.mp_lab_9.data.model.Product;
import com.example.mp_lab_9.data.model.ShoppingList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class JsonParser {

    public static ShoppingList parseShoppingList(JSONObject json) throws JSONException {
        return new ShoppingList(
                json.getInt("id"),
                json.optInt("user_id", 0),
                json.getString("name"),
                json.getString("created_at"),
                json.optBoolean("is_completed", false)
        );
    }

    public static List<ShoppingList> parseShoppingLists(JSONArray jsonArray) throws JSONException {
        List<ShoppingList> lists = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            lists.add(parseShoppingList(jsonArray.getJSONObject(i)));
        }
        return lists;
    }

    public static Product parseProduct(JSONObject json) throws JSONException {
        return new Product(
                json.getInt("id"),
                json.optInt("list_id", 0),
                json.getString("name"),
                json.optInt("quantity", 1),
                json.optBoolean("is_purchased", false),
                json.optString("created_at", "")
        );
    }

    public static List<Product> parseProducts(JSONArray jsonArray) throws JSONException {
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            products.add(parseProduct(jsonArray.getJSONObject(i)));
        }
        return products;
    }

    public static String getErrorMessage(JSONObject response) {
        return response.optString("message", "Unknown error occurred");
    }
}