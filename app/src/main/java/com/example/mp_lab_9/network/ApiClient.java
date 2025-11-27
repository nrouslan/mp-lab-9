package com.example.mp_lab_9.network;

import android.content.Context;
import com.example.mp_lab_9.util.SharedPrefManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ApiClient {
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
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", email);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            callback.onError("JSON error: " + e.getMessage());
            return;
        }

        executeRequest(ApiConfig.LOGIN, "POST", null, null, jsonBody.toString(), callback);
    }

    public void register(String name, String email, String password, ApiCallback callback) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("name", name);
            jsonBody.put("email", email);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            callback.onError("JSON error: " + e.getMessage());
            return;
        }

        executeRequest(ApiConfig.REGISTER, "POST", null, null, jsonBody.toString(), callback);
    }

    // === СПИСКИ ПОКУПОК ===

    public void getShoppingLists(ApiCallback callback) {
        executeRequest(ApiConfig.GET_LISTS, "GET", null, null, null, callback);
    }

    public void createShoppingList(String name, ApiCallback callback) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("name", name);
        } catch (JSONException e) {
            callback.onError("JSON error: " + e.getMessage());
            return;
        }

        executeRequest(ApiConfig.CREATE_LIST, "POST", null, null, jsonBody.toString(), callback);
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

        executeRequest(ApiConfig.UPDATE_LIST, "POST", null, null, jsonBody.toString(), callback);
    }

    public void deleteShoppingList(int listId, ApiCallback callback) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("list_id", listId);
        } catch (JSONException e) {
            callback.onError("JSON error: " + e.getMessage());
            return;
        }

        executeRequest(ApiConfig.DELETE_LIST, "POST", null, null, jsonBody.toString(), callback);
    }

    // === ТОВАРЫ ===

    public void getProducts(int listId, ApiCallback callback) {
        String url = ApiConfig.GET_PRODUCTS + "?list_id=" + listId;
        executeRequest(url, "GET", null, null, null, callback);
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

        executeRequest(ApiConfig.ADD_PRODUCT, "POST", null, null, jsonBody.toString(), callback);
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

        executeRequest(ApiConfig.UPDATE_PRODUCT, "POST", null, null, jsonBody.toString(), callback);
    }

    public void deleteProduct(int productId, ApiCallback callback) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("product_id", productId);
        } catch (JSONException e) {
            callback.onError("JSON error: " + e.getMessage());
            return;
        }

        executeRequest(ApiConfig.DELETE_PRODUCT, "POST", null, null, jsonBody.toString(), callback);
    }

    // === ОСНОВНОЙ МЕТОД ДЛЯ ВСЕХ ЗАПРОСОВ ===

    private void executeRequest(String url, String method, String[] fields,
                                String[] data, String jsonBody, ApiCallback callback) {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                PutData putData;

                if (jsonBody != null) {
                    putData = new PutData(url, method, jsonBody);
                    putData.addHeader(ApiConfig.HEADER_CONTENT_TYPE, ApiConfig.CONTENT_TYPE_JSON);
                } else if (fields != null && data != null) {
                    putData = new PutData(url, method, fields, data);
                    putData.addHeader(ApiConfig.HEADER_CONTENT_TYPE, "application/x-www-form-urlencoded");
                } else {
                    putData = new PutData(url, method);
                }

                // Добавляем токен авторизации для защищенных endpoints
                if (!url.contains("register.php") && !url.contains("login.php")) {
                    String token = sharedPrefManager.getToken();
                    if (token == null) {
                        // Если токена нет, сразу возвращаем ошибку
                        callback.onError("Not authenticated");
                        return;
                    }
                    putData.addHeader(ApiConfig.HEADER_AUTHORIZATION,
                            ApiConfig.BEARER_PREFIX + token);
                }

                if (putData.startPut()) {
                    if (putData.onComplete()) {
                        String result = putData.getResult();
                        try {
                            JSONObject response = new JSONObject(result);

                            if (putData.isSuccess()) {
                                callback.onSuccess(response);
                            } else {
                                String errorMessage = response.optString("message",
                                        response.optString("error", "Request failed with code: " + putData.getResponseCode()));
                                callback.onError(errorMessage);
                            }
                        } catch (JSONException e) {
                            callback.onError("Invalid JSON response: " + result);
                        }
                    } else {
                        callback.onError("Request timeout");
                    }
                } else {
                    callback.onError("Failed to start request");
                }
            } catch (Exception e) {
                callback.onError("Exception: " + e.getMessage());
            }
        });
    }
}