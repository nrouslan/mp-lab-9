package com.example.mp_lab_9.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class PutData extends Thread {
    private String url;
    private String method;
    private String resultData = "{}";
    private String[] field;
    private String[] data;
    private HashMap<String, String> headers;
    private int responseCode = -1;
    private String errorMessage = "";
    private boolean isJson = false;
    private String jsonBody = "";

    public PutData(String url, String method, String[] field, String[] data) {
        this.url = url;
        this.method = method;
        this.data = new String[data.length];
        this.field = new String[field.length];
        System.arraycopy(field, 0, this.field, 0, field.length);
        System.arraycopy(data, 0, this.data, 0, data.length);
        this.headers = new HashMap<>();
    }

    public PutData(String url, String method, String jsonBody) {
        this.url = url;
        this.method = method;
        this.jsonBody = jsonBody;
        this.isJson = true;
        this.headers = new HashMap<>();
    }

    public PutData(String url, String method) {
        this.url = url;
        this.method = method;
        this.headers = new HashMap<>();
    }

    public void addHeader(String key, String value) {
        if (headers == null) {
            headers = new HashMap<>();
        }
        headers.put(key, value);
    }

    public void setAuthToken(String token) {
        addHeader("Authorization", "Bearer " + token);
    }

    @Override
    public void run() {
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(this.url);
            httpURLConnection = (HttpURLConnection) url.openConnection();

            // Настройка соединения
            httpURLConnection.setRequestMethod(this.method);
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setReadTimeout(15000);
            httpURLConnection.setDoInput(true);

            // Добавление заголовков
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    httpURLConnection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            // Для запросов с телом (POST, PUT)
            if (method.equals("POST") || method.equals("PUT")) {
                httpURLConnection.setDoOutput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                if (isJson && jsonBody != null) {
                    // Отправка JSON данных
                    bufferedWriter.write(jsonBody);
                } else if (field != null && data != null) {
                    // Отправка формы данных
                    StringBuilder postData = new StringBuilder();
                    for (int i = 0; i < this.field.length; i++) {
                        if (i > 0) postData.append("&");
                        postData.append(URLEncoder.encode(this.field[i], "UTF-8"))
                                .append("=")
                                .append(URLEncoder.encode(this.data[i], "UTF-8"));
                    }
                    bufferedWriter.write(postData.toString());
                }

                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
            }

            // Получение кода ответа
            responseCode = httpURLConnection.getResponseCode();

            // Чтение ответа
            InputStream inputStream;
            if (responseCode >= 200 && responseCode < 300) {
                inputStream = httpURLConnection.getInputStream();
            } else {
                inputStream = httpURLConnection.getErrorStream();
                if (inputStream == null) {
                    inputStream = httpURLConnection.getInputStream();
                }
            }

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder result = new StringBuilder();
            String resultLine;

            while ((resultLine = bufferedReader.readLine()) != null) {
                result.append(resultLine);
            }

            bufferedReader.close();
            inputStream.close();

            setData(result.toString());

        } catch (IOException e) {
            setData("{\"error\":\"Network error: " + e.getMessage() + "\"}");
            errorMessage = e.getMessage();
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
    }

    public boolean startPut() {
        if (url == null || url.isEmpty()) {
            setData("{\"error\":\"URL is empty\"}");
            return false;
        }

        if (method == null || method.isEmpty()) {
            setData("{\"error\":\"Method is empty\"}");
            return false;
        }

        try {
            this.start();
            return true;
        } catch (Exception e) {
            setData("{\"error\":\"Failed to start thread: " + e.getMessage() + "\"}");
            return false;
        }
    }

    public boolean onComplete() {
        try {
            this.join(20000); // 20 секунд timeout
            return !this.isAlive();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public String getResult() {
        return this.getData();
    }

    public void setData(String resultData) {
        this.resultData = resultData;
    }

    public String getData() {
        return resultData;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isSuccess() {
        return responseCode >= 200 && responseCode < 300;
    }
}