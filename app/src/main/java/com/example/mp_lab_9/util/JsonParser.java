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
                json.getString("name"),
                json.getString("created_at"),
                json.getInt("total_products"),
                json.getInt("purchased_products"),
                json.getBoolean("is_completed")
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
        Product product = new Product(
                json.getInt("id"),
                json.getInt("list_id"),
                json.getString("name"),
                json.getInt("quantity"),
                json.getBoolean("is_purchased"),
                json.getString("created_at")
        );

        if (json.has("category") && !json.isNull("category")) {
            product.setCategory(json.getString("category"));
        }
        if (json.has("price") && !json.isNull("price")) {
            product.setPrice(json.getDouble("price"));
        }
        if (json.has("notes") && !json.isNull("notes")) {
            product.setNotes(json.getString("notes"));
        }

        return product;
    }

    public static List<Product> parseProducts(JSONArray jsonArray) throws JSONException {
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            products.add(parseProduct(jsonArray.getJSONObject(i)));
        }
        return products;
    }
}