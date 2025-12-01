package com.example.mp_lab_9.network;

import android.content.Context;
import android.util.Log;

import com.example.mp_lab_9.util.SharedPrefManager;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ApiClient {
    private Context context;
    private SharedPrefManager sharedPrefManager;
    private static final String TAG = "ApiClient";

    public ApiClient(Context context) {
        this.context = context;
        this.sharedPrefManager = SharedPrefManager.getInstance(context);
        Log.d(TAG, "ApiClient initialized");
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

        executeRequest(ApiConfig.LOGIN, "POST", jsonBody.toString(), callback);
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

        executeRequest(ApiConfig.REGISTER, "POST", jsonBody.toString(), callback);
    }

    // === СПИСКИ ПОКУПОК ===

    public void getShoppingLists(ApiCallback callback) {
        executeRequest(ApiConfig.GET_LISTS, "GET", null, callback);
    }

    public void createShoppingList(String name, ApiCallback callback) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("name", name);
        } catch (JSONException e) {
            callback.onError("JSON error: " + e.getMessage());
            return;
        }

        executeRequest(ApiConfig.CREATE_LIST, "POST", jsonBody.toString(), callback);
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

        executeRequest(ApiConfig.UPDATE_LIST, "POST", jsonBody.toString(), callback);
    }

    public void deleteShoppingList(int listId, ApiCallback callback) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("list_id", listId);
        } catch (JSONException e) {
            callback.onError("JSON error: " + e.getMessage());
            return;
        }

        executeRequest(ApiConfig.DELETE_LIST, "POST", jsonBody.toString(), callback);
    }

    // === ТОВАРЫ ===

    public void getProducts(int listId, ApiCallback callback) {
        String url = ApiConfig.GET_PRODUCTS + "?list_id=" + listId;
        Log.d(TAG, "Getting products for listId: " + listId + ", URL: " + url);
        executeRequest(url, "GET", null, callback);
    }

    public void addProduct(int listId, String name, int quantity, ApiCallback callback) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("list_id", listId);
            jsonBody.put("name", name);
            jsonBody.put("quantity", quantity);

            Log.d(TAG, "Adding product - List: " + listId +
                    ", Name: " + name +
                    ", Quantity: " + quantity +
                    ", JSON: " + jsonBody.toString());

            executeRequest(ApiConfig.ADD_PRODUCT, "POST", jsonBody.toString(), callback);

        } catch (JSONException e) {
            Log.e(TAG, "Add product JSON error: " + e.getMessage(), e);
            callback.onError("JSON error: " + e.getMessage());
        }
    }

    public void updateProduct(int productId, Boolean isPurchased, Integer quantity,
                              String name, ApiCallback callback) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("product_id", productId);
            if (isPurchased != null) jsonBody.put("is_purchased", isPurchased);
            if (quantity != null) jsonBody.put("quantity", quantity);
            if (name != null) jsonBody.put("name", name);

            Log.d(TAG, "Updating product - ID: " + productId +
                    ", Purchased: " + isPurchased +
                    ", Quantity: " + quantity +
                    ", Name: " + name +
                    ", JSON: " + jsonBody.toString());

            executeRequest(ApiConfig.UPDATE_PRODUCT, "POST", jsonBody.toString(), callback);

        } catch (JSONException e) {
            Log.e(TAG, "Update product JSON error: " + e.getMessage(), e);
            callback.onError("JSON error: " + e.getMessage());
        }
    }

    public void deleteProduct(int productId, ApiCallback callback) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("product_id", productId);

            Log.d(TAG, "Deleting product - ID: " + productId +
                    ", JSON: " + jsonBody.toString());

            executeRequest(ApiConfig.DELETE_PRODUCT, "POST", jsonBody.toString(), callback);

        } catch (JSONException e) {
            Log.e(TAG, "Delete product JSON error: " + e.getMessage(), e);
            callback.onError("JSON error: " + e.getMessage());
        }
    }

    // === ОСНОВНОЙ МЕТОД ДЛЯ ВСЕХ ЗАПРОСОВ ===

    private void executeRequest(String url, String method, String jsonBody, ApiCallback callback) {
        Log.d(TAG, "Executing request - Method: " + method + ", URL: " + url);

        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                PutData putData;

                if (jsonBody != null) {
                    putData = new PutData(url, method, jsonBody);
                    putData.addHeader("Content-Type", "application/json");
                    Log.d(TAG, "Request body: " + jsonBody);
                } else {
                    putData = new PutData(url, method);
                    Log.d(TAG, "Request body: null");
                }

                // Добавляем токен авторизации для защищенных endpoints
                if (sharedPrefManager.isLoggedIn() &&
                        !url.contains("register") && !url.contains("login")) {
                    String token = sharedPrefManager.getToken();
                    if (token != null && !token.isEmpty()) {
                        putData.addHeader("Authorization", "Bearer " + token);
                        Log.d(TAG, "Authorization header added");
                    }
                }

                Log.d(TAG, "Starting request...");
                if (putData.startPut()) {
                    Log.d(TAG, "Request started successfully");

                    if (putData.onComplete()) {
                        String result = putData.getResult();
                        Log.d(TAG, "Request completed. Result: " + (result != null ? result.substring(0, Math.min(result.length(), 200)) : "null"));

                        // Проверяем, является ли ответ пустым
                        if (result == null || result.trim().isEmpty()) {
                            Log.e(TAG, "Empty response from server");
                            callback.onError("Empty response from server");
                            return;
                        }

                        try {
                            JSONObject response = new JSONObject(result);

                            if (putData.isSuccess()) {
                                Log.d(TAG, "Request successful for URL: " + url);
                                callback.onSuccess(response);
                            } else {
                                // Пытаемся получить сообщение об ошибке из ответа
                                String errorMessage = "Request failed";
                                if (response.has("message")) {
                                    errorMessage = response.getString("message");
                                } else if (response.has("error")) {
                                    errorMessage = response.getString("error");
                                }
                                Log.e(TAG, "Request failed for URL: " + url + ", Error: " + errorMessage);
                                callback.onError(errorMessage);
                            }
                        } catch (JSONException e) {
                            // Если ответ не JSON, возвращаем как есть
                            if (putData.isSuccess()) {
                                Log.d(TAG, "Non-JSON successful response for URL: " + url);
                                JSONObject successResponse = new JSONObject();
                                successResponse.put("success", true);
                                successResponse.put("raw_response", result);
                                callback.onSuccess(successResponse);
                            } else {
                                Log.e(TAG, "Non-JSON error response for URL: " + url + ", Response: " + result);
                                callback.onError("Server error: " + result);
                            }
                        }
                    } else {
                        Log.e(TAG, "Request timeout for URL: " + url);
                        callback.onError("Request timeout");
                    }
                } else {
                    Log.e(TAG, "Failed to start request for URL: " + url);
                    callback.onError("Failed to start request");
                }
            } catch (Exception e) {
                Log.e(TAG, "Network error for URL: " + url + ", Error: " + e.getMessage(), e);
                callback.onError("Network error: " + e.getMessage());
            }
        });
    }
}