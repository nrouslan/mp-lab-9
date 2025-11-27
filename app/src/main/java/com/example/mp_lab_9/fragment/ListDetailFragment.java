package com.example.mp_lab_9.fragment;

import com.example.mp_lab_9.R;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mp_lab_9.adapter.ProductAdapter;
import com.example.mp_lab_9.data.model.Product;
import com.example.mp_lab_9.data.model.ShoppingList;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
        // TODO: Загрузка деталей списка из БД
        // Временные данные
        currentList = new ShoppingList(listId, "Мой список", "2024-01-20", 5, 2, false);
        updateProgress();
    }

    private void loadProducts() {
        showLoading(true);

        // TODO: Загрузка товаров из БД или с сервера
        // Временные данные
        List<Product> tempProducts = new ArrayList<>();
        tempProducts.add(new Product(1, "Молоко", 2, false));
        tempProducts.add(new Product(2, "Хлеб", 1, true));
        tempProducts.add(new Product(3, "Яйца", 10, false));
        tempProducts.add(new Product(4, "Сыр", 1, false));
        tempProducts.add(new Product(5, "Масло", 1, true));

        products.clear();
        products.addAll(tempProducts);
        adapter.notifyDataSetChanged();

        showLoading(false);
        updateEmptyState();
        updateProgress();
    }

    private void updateProgress() {
        if (currentList != null) {
            int total = products.size();
            int completed = (int) products.stream().filter(Product::isPurchased).count();

            String progressText = completed + "/" + total + " товаров";
            textViewProgress.setText(progressText);

            // TODO: Обновить прогресс-бар если есть
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
        // TODO: Добавление товара в БД и на сервер
        Product newProduct = new Product(products.size() + 1, productName, quantity, false);

        products.add(newProduct);
        adapter.notifyItemInserted(products.size() - 1);
        updateEmptyState();
        updateProgress();
    }

    @Override
    public void onProductClick(Product product) {
        // Переключение статуса покупки
        toggleProductPurchaseStatus(product);
    }

    @Override
    public void onProductLongClick(Product product) {
        showProductOptionsDialog(product);
    }

    @Override
    public void onProductDeleteClick(Product product) {
        showDeleteProductDialog(product);
    }

    private void toggleProductPurchaseStatus(Product product) {
        product.setPurchased(!product.isPurchased());

        // TODO: Обновление в БД и на сервере
        int position = products.indexOf(product);
        if (position != -1) {
            adapter.notifyItemChanged(position);
            updateProgress();
        }
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
        // TODO: Обновление товара в БД и на сервере
        product.setName(newName);
        product.setQuantity(newQuantity);

        int position = products.indexOf(product);
        if (position != -1) {
            adapter.notifyItemChanged(position);
        }
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
        // TODO: Удаление товара из БД и с сервера
        int position = products.indexOf(product);
        if (position != -1) {
            products.remove(position);
            adapter.notifyItemRemoved(position);
            updateEmptyState();
            updateProgress();
        }
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }
}