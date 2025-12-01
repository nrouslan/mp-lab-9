package com.example.mp_lab_9.fragment;

import com.example.mp_lab_9.R;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mp_lab_9.adapter.ProductAdapter;
import com.example.mp_lab_9.data.model.Product;
import com.example.mp_lab_9.data.model.ShoppingList;
import com.example.mp_lab_9.network.ApiClient;
import com.example.mp_lab_9.util.JsonParser;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ListDetailFragment extends Fragment implements ProductAdapter.OnProductClickListener {

    private static final String ARG_LIST_ID = "list_id";

    private RecyclerView recyclerView;
    private FloatingActionButton fabAddProduct;
    private ProgressBar progressBar;
    private TextView textViewEmpty, textViewProgress;
    private ProductAdapter adapter;
    private List<Product> products;
    private int listId;
    private ShoppingList currentList;
    private ApiClient apiClient;

    public static ListDetailFragment newInstance(int listId) {
        ListDetailFragment fragment = new ListDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_LIST_ID, listId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            listId = getArguments().getInt(ARG_LIST_ID);
        }

        apiClient = new ApiClient(requireContext());
        initViews(view);
        setupRecyclerView();
        setupListeners();
        loadListDetails();
        loadProducts();
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        fabAddProduct = view.findViewById(R.id.fabAddProduct);
        progressBar = view.findViewById(R.id.progressBar);
        textViewEmpty = view.findViewById(R.id.textViewEmpty);
        textViewProgress = view.findViewById(R.id.textViewProgress);
    }

    private void setupRecyclerView() {
        products = new ArrayList<>();
        adapter = new ProductAdapter(products, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupListeners() {
        fabAddProduct.setOnClickListener(v -> showAddProductDialog());
    }

    private void loadListDetails() {
        apiClient.getShoppingLists(new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                requireActivity().runOnUiThread(() -> {
                    try {
                        if (response.getBoolean("success")) {
                            JSONArray listsArray = response.getJSONArray("lists");
                            for (int i = 0; i < listsArray.length(); i++) {
                                JSONObject listJson = listsArray.getJSONObject(i);
                                if (listJson.getInt("id") == listId) {
                                    currentList = JsonParser.parseShoppingList(listJson);
                                    updateProgress();
                                    break;
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            public void onError(String error) {
                currentList = new ShoppingList(listId, "Мой список", "2024-01-20", false);
                updateProgress();
            }
        });
    }

    private void loadProducts() {
        showLoading(true);

        apiClient.getProducts(listId, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                requireActivity().runOnUiThread(() -> {
                    showLoading(false);
                    try {
                        if (response.getBoolean("success")) {
                            JSONArray productsArray = response.getJSONArray("products");
                            List<Product> productList = JsonParser.parseProducts(productsArray);

                            products.clear();
                            products.addAll(productList);
                            adapter.notifyDataSetChanged();
                            updateEmptyState();
                            updateProgress();
                        } else {
                            Toast.makeText(requireContext(), "Не удалось загрузить товары", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(requireContext(), "Ошибка parsing JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String error) {
                requireActivity().runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(requireContext(), "Ошибка сети: " + error, Toast.LENGTH_SHORT).show();
                    loadLocalProducts();
                });
            }
        });
    }

    private void loadLocalProducts() {
        List<Product> tempProducts = new ArrayList<>();
        tempProducts.add(new Product(1, "Молоко", 2, false));
        tempProducts.add(new Product(2, "Хлеб", 1, true));
        tempProducts.add(new Product(3, "Яйца", 10, false));

        products.clear();
        products.addAll(tempProducts);
        adapter.notifyDataSetChanged();
        updateEmptyState();
        updateProgress();
    }

    private void updateProgress() {
        if (!products.isEmpty()) {
            int total = products.size();
            int completed = 0;

            for (Product product : products) {
                if (product.isPurchased()) {
                    completed++;
                }
            }

            String progressText = completed + "/" + total + " товаров";
            textViewProgress.setText(progressText);
        } else {
            textViewProgress.setText("0/0 товаров");
        }
    }

    private void updateEmptyState() {
        if (products.isEmpty()) {
            textViewEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            textViewEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void showAddProductDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Добавить товар");

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_product, null);
        EditText editTextProductName = dialogView.findViewById(R.id.editTextProductName);
        EditText editTextQuantity = dialogView.findViewById(R.id.editTextQuantity);

        builder.setView(dialogView);
        builder.setPositiveButton("Добавить", (dialog, which) -> {
            String productName = editTextProductName.getText().toString().trim();
            String quantityStr = editTextQuantity.getText().toString().trim();
            int quantity = quantityStr.isEmpty() ? 1 : Integer.parseInt(quantityStr);

            if (!productName.isEmpty()) {
                addNewProduct(productName, quantity);
            }
        });
        builder.setNegativeButton("Отмена", null);

        builder.show();
    }

    private void addNewProduct(String productName, int quantity) {
        apiClient.addProduct(listId, productName, quantity, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                requireActivity().runOnUiThread(() -> {
                    try {
                        if (response.getBoolean("success")) {
                            JSONObject productJson = response.getJSONObject("product");
                            Product newProduct = JsonParser.parseProduct(productJson);

                            products.add(newProduct);
                            adapter.notifyItemInserted(products.size() - 1);
                            updateEmptyState();
                            updateProgress();

                            Toast.makeText(requireContext(), "Товар добавлен", Toast.LENGTH_SHORT).show();
                        } else {
                            String error = response.optString("message", "Не удалось добавить товар");
                            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(requireContext(), "Ошибка добавления товара", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String error) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Ошибка сети: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }


    @Override
    public void onProductClick(Product product) {
        // Инвертируем статус покупки
        boolean newPurchasedStatus = !product.isPurchased();

        Log.println(Log.INFO, "newPurchasedStatus", String.valueOf(newPurchasedStatus));

        apiClient.updateProduct(product.getId(), newPurchasedStatus, null, null,
                new ApiClient.ApiCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        requireActivity().runOnUiThread(() -> {
                            try {
                                if (response.getBoolean("success")) {
                                    // Обновляем локально только после успешного ответа от сервера
                                    product.setPurchased(newPurchasedStatus);
                                    int position = products.indexOf(product);
                                    if (position != -1) {
                                        adapter.notifyItemChanged(position);
                                        updateProgress();

                                        // Дополнительный Toast при успешном изменении статуса
                                        String status = newPurchasedStatus ? "куплен" : "не куплен";
                                        Toast.makeText(requireContext(), "Товар " + status,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    // Откатываем изменение в случае ошибки
                                    product.setPurchased(!newPurchasedStatus);
                                    adapter.notifyItemChanged(products.indexOf(product));
                                    String error = JsonParser.getErrorMessage(response);
                                    Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                product.setPurchased(!newPurchasedStatus);
                                adapter.notifyItemChanged(products.indexOf(product));
                                Toast.makeText(requireContext(), "Ошибка обновления", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onError(String error) {
                        requireActivity().runOnUiThread(() -> {
                            // Откатываем изменение в случае ошибки сети
                            product.setPurchased(!newPurchasedStatus);
                            adapter.notifyItemChanged(products.indexOf(product));
                            Toast.makeText(requireContext(), "Ошибка сети: " + error, Toast.LENGTH_SHORT).show();
                        });
                    }
                });
    }

    @Override
    public void onProductLongClick(Product product) {
        showProductOptionsDialog(product);
    }

    @Override
    public void onProductDeleteClick(Product product) {
        showDeleteProductDialog(product);
    }

    private void showProductOptionsDialog(Product product) {
        String[] options = {"Редактировать", "Удалить"};

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(product.getName());
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    showEditProductDialog(product);
                    break;
                case 1:
                    showDeleteProductDialog(product);
                    break;
            }
        });
        builder.show();
    }

    private void showEditProductDialog(Product product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Редактировать товар");

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_product, null);
        EditText editTextProductName = dialogView.findViewById(R.id.editTextProductName);
        EditText editTextQuantity = dialogView.findViewById(R.id.editTextQuantity);

        editTextProductName.setText(product.getName());
        editTextQuantity.setText(String.valueOf(product.getQuantity()));

        builder.setView(dialogView);
        builder.setPositiveButton("Сохранить", (dialog, which) -> {
            String newName = editTextProductName.getText().toString().trim();
            String quantityStr = editTextQuantity.getText().toString().trim();
            int newQuantity = quantityStr.isEmpty() ? 1 : Integer.parseInt(quantityStr);

            if (!newName.isEmpty()) {
                updateProduct(product, newName, newQuantity);
            }
        });
        builder.setNegativeButton("Отмена", null);

        builder.show();
    }

    private void updateProduct(Product product, String newName, int newQuantity) {
        apiClient.updateProduct(product.getId(), null, newQuantity, newName,
                new ApiClient.ApiCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        requireActivity().runOnUiThread(() -> {
                            try {
                                if (response.getBoolean("success")) {
                                    product.setName(newName);
                                    product.setQuantity(newQuantity);
                                    int position = products.indexOf(product);
                                    if (position != -1) {
                                        adapter.notifyItemChanged(position);
                                    }
                                    Toast.makeText(requireContext(), "Товар обновлен", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(requireContext(), "Не удалось обновить товар", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                Toast.makeText(requireContext(), "Ошибка обновления товара", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onError(String error) {
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), "Ошибка сети: " + error, Toast.LENGTH_SHORT).show();
                        });
                    }
                });
    }

    private void showDeleteProductDialog(Product product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Удаление товара");
        builder.setMessage("Удалить \"" + product.getName() + "\" из списка?");
        builder.setPositiveButton("Удалить", (dialog, which) -> {
            deleteProduct(product);
        });
        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    private void deleteProduct(Product product) {
        apiClient.deleteProduct(product.getId(), new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                requireActivity().runOnUiThread(() -> {
                    try {
                        if (response.getBoolean("success")) {
                            int position = products.indexOf(product);
                            if (position != -1) {
                                products.remove(position);
                                adapter.notifyItemRemoved(position);
                                updateEmptyState();
                                updateProgress();
                                Toast.makeText(requireContext(), "Товар удален", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(requireContext(), "Не удалось удалить товар", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(requireContext(), "Ошибка удаления товара", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String error) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Ошибка сети: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }
}