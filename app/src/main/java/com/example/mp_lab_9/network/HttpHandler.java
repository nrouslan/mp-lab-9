package com.example.mp_lab_9.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import org.json.JSONException;
import org.json.JSONObject;

public class HttpHandler {

    private Context context;

    public HttpHandler(Context context) {
        this.context = context;
    }

    // Проверка наличия интернет-соединения
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // Создание JSON объекта для запросов
    public static String createJsonBody(String... keyValuePairs) {
        if (keyValuePairs.length % 2 != 0) {
            throw new IllegalArgumentException("Key-value pairs must be even");
        }

        try {
            JSONObject jsonObject = new JSONObject();
            for (int i = 0; i < keyValuePairs.length; i += 2) {
                jsonObject.put(keyValuePairs[i], keyValuePairs[i + 1]);
            }
            return jsonObject.toString();
        } catch (JSONException e) {
            return "{}";
        }
    }

    // Парсинг ответа от сервера
    public static JSONObject parseResponse(String response) {
        try {
            return new JSONObject(response);
        } catch (JSONException e) {
            try {
                JSONObject errorObj = new JSONObject();
                errorObj.put("error", "Invalid JSON response: " + e.getMessage());
                return errorObj;
            } catch (JSONException ex) {
                return new JSONObject();
            }
        }
    }

    // Проверка на наличие ошибки в ответе
    public static boolean hasError(JSONObject response) {
        return response.has("error") || response.has("message") &&
                response.optString("message").toLowerCase().contains("error");
    }

    // Получение сообщения об ошибке
    public static String getErrorMessage(JSONObject response) {
        if (response.has("error")) {
            return response.optString("error");
        } else if (response.has("message")) {
            return response.optString("message");
        } else {
            return "Unknown error occurred";
        }
    }
}