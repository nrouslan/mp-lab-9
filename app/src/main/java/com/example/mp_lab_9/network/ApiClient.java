package com.example.mp_lab_9.network;

import android.content.Context;
import com.example.mp_lab_9.util.SharedPrefManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ApiClient {
    private static final String BASE_URL = "http://your-server.com/api/";
    private Context context;
    private SharedPrefManager sharedPrefManager;

    public ApiClient(Context context) {
        this.context = context;
        this.sharedPrefManager = SharedPrefManager.getInstance(context);
    }

    public interface ApiCallback {
        void onSuccess(JSONObject response);
        void onError(String error);
    }

    // === АУТЕНТИФИКАЦИЯ ===

    public void login(String email, String password, ApiCallback callback) {
        executeRequest("login", "POST",
                new String[]{"email", "password"},
                new String[]{email, password},
                null, callback);
    }

    public void register(String name, String email, String password, ApiCallback callback) {
        executeRequest("register", "POST",
                new String[]{"name", "email", "password"},
                new String[]{name, email, password},
                null, callback);
    }

    // === СПИСКИ ПОКУПОК ===

    public void getShoppingLists(ApiCallback callback) {
        executeRequest("lists", "GET", null, null, null, callback);
    }

    public void createShoppingList(String name, ApiCallback callback) {
        executeRequest("lists", "POST",
                new String[]{"name"},
                new String[]{name},
                null, callback);
    }

    public void updateShoppingList(int listId, String name, Boolean isCompleted, ApiCallback callback) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("list_id", listId);
            if (name != null) jsonBody.put("name", name);
            if (isCompleted != null) jsonBody.put("is_completed", isCompleted);
        } catch (JSONException e) {
            callback.onError("JSON error: " + e.getMessage());
            return;
        }

        executeRequest("update_list", "POST", null, null, jsonBody.toString(), callback);
    }

    public void deleteShoppingList(int listId, ApiCallback callback) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("list_id", listId);
        } catch (JSONException e) {
            callback.onError("JSON error: " + e.getMessage());
            return;
        }

        executeRequest("delete_list", "POST", null, null, jsonBody.toString(), callback);
    }

    // === ТОВАРЫ ===

    public void getProducts(int listId, ApiCallback callback) {
        executeRequest("products?list_id=" + listId, "GET", null, null, null, callback);
    }

    public void addProduct(int listId, String name, int quantity, String category,
                           Double price, String notes, ApiCallback callback) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("list_id", listId);
            jsonBody.put("name", name);
            jsonBody.put("quantity", quantity);
            if (category != null) jsonBody.put("category", category);
            if (price != null) jsonBody.put("price", price);
            if (notes != null) jsonBody.put("notes", notes);
        } catch (JSONException e) {
            callback.onError("JSON error: " + e.getMessage());
            return;
        }

        executeRequest("add_product", "POST", null, null, jsonBody.toString(), callback);
    }

    public void updateProduct(int productId, Boolean isPurchased, Integer quantity,
                              String name, ApiCallback callback) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("product_id", productId);
            if (isPurchased != null) jsonBody.put("is_purchased", isPurchased);
            if (quantity != null) jsonBody.put("quantity", quantity);
            if (name != null) jsonBody.put("name", name);
        } catch (JSONException e) {
            callback.onError("JSON error: " + e.getMessage());
            return;
        }

        executeRequest("update_product", "POST", null, null, jsonBody.toString(), callback);
    }

    public void deleteProduct(int productId, ApiCallback callback) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("product_id", productId);
        } catch (JSONException e) {
            callback.onError("JSON error: " + e.getMessage());
            return;
        }

        executeRequest("delete_product", "POST", null, null, jsonBody.toString(), callback);
    }

    // === ОСНОВНОЙ МЕТОД ДЛЯ ВСЕХ ЗАПРОСОВ ===

    private void executeRequest(String endpoint, String method, String[] fields,
                                String[] data, String jsonBody, ApiCallback callback) {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                String url = BASE_URL + endpoint;
                PutData putData;

                if (jsonBody != null) {
                    putData = new PutData(url, method, jsonBody);
                } else if (fields != null && data != null) {
                    putData = new PutData(url, method, fields, data);
                } else {
                    putData = new PutData(url, method);
                }

                // Добавляем токен авторизации для защищенных endpoints
                if (!endpoint.equals("login") && !endpoint.equals("register")) {
                    String token = sharedPrefManager.getToken();
                    if (token != null) {
                        putData.setAuthToken(token);
                    }
                }

                if (putData.startPut() && putData.onComplete()) {
                    String result = putData.getResult();
                    JSONObject response = new JSONObject(result);

                    if (putData.isSuccess()) {
                        callback.onSuccess(response);
                    } else {
                        String errorMessage = response.optString("message",
                                response.optString("error", "Request failed with code: " + putData.getResponseCode()));
                        callback.onError(errorMessage);
                    }
                } else {
                    callback.onError("Network error: " + putData.getErrorMessage());
                }
            } catch (Exception e) {
                callback.onError("Exception: " + e.getMessage());
            }
        });
    }
}