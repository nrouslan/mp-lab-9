package com.example.mp_lab_9.network;

import java.util.HashMap;

public class RequestPackage {
    private String url;
    private String method = "GET";
    private HashMap<String, String> params;
    private HashMap<String, String> headers;
    private String jsonBody;

    public RequestPackage(String url) {
        this.url = url;
        this.params = new HashMap<>();
        this.headers = new HashMap<>();
    }

    public RequestPackage(String url, String method) {
        this.url = url;
        this.method = method;
        this.params = new HashMap<>();
        this.headers = new HashMap<>();
    }

    // Методы для работы с параметрами
    public void setParam(String key, String value) {
        params.put(key, value);
    }

    public String getParam(String key) {
        return params.get(key);
    }

    // Методы для работы с заголовками
    public void setHeader(String key, String value) {
        headers.put(key, value);
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    // Методы для работы с JSON телом
    public void setJsonBody(String jsonBody) {
        this.jsonBody = jsonBody;
    }

    // Геттеры
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public HashMap<String, String> getParams() { return params; }
    public void setParams(HashMap<String, String> params) { this.params = params; }

    public HashMap<String, String> getHeaders() { return headers; }
    public void setHeaders(HashMap<String, String> headers) { this.headers = headers; }

    public String getJsonBody() { return jsonBody; }

    // Конвертация параметров в массивы для PutData
    public String[] getFieldArray() {
        return params.keySet().toArray(new String[0]);
    }

    public String[] getDataArray() {
        return params.values().toArray(new String[0]);
    }

    // Создание PutData из RequestPackage
    public PutData toPutData() {
        if (jsonBody != null && !jsonBody.isEmpty()) {
            PutData putData = new PutData(url, method, jsonBody);
            // Добавляем заголовки
            for (HashMap.Entry<String, String> entry : headers.entrySet()) {
                putData.addHeader(entry.getKey(), entry.getValue());
            }
            return putData;
        } else {
            PutData putData = new PutData(url, method, getFieldArray(), getDataArray());
            // Добавляем заголовки
            for (HashMap.Entry<String, String> entry : headers.entrySet()) {
                putData.addHeader(entry.getKey(), entry.getValue());
            }
            return putData;
        }
    }
}