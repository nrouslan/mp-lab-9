package com.example.mp_lab_9.network;

public class ApiConfig {

    // Для эмулятора Android
    public static final String BASE_URL = "http://10.0.2.2/smart-shopping-api/";

    // Endpoints
    public static final String REGISTER = BASE_URL + "register.php";
    public static final String LOGIN = BASE_URL + "login.php";
    public static final String GET_LISTS = BASE_URL + "get_lists.php";
    public static final String CREATE_LIST = BASE_URL + "create_list.php";
    public static final String UPDATE_LIST = BASE_URL + "update_list.php";
    public static final String DELETE_LIST = BASE_URL + "delete_list.php";
    public static final String GET_PRODUCTS = BASE_URL + "get_products.php";
    public static final String ADD_PRODUCT = BASE_URL + "add_product.php";
    public static final String UPDATE_PRODUCT = BASE_URL + "update_product.php";
    public static final String DELETE_PRODUCT = BASE_URL + "delete_product.php";
}