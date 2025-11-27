package com.example.mp_lab_9.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mp_lab_9.R;
import com.example.mp_lab_9.adapter.ShoppingListAdapter;
import com.example.mp_lab_9.data.model.ShoppingList;
import com.example.mp_lab_9.util.SharedPrefManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class MyListsFragment extends Fragment implements ShoppingListAdapter.OnListClickListener {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton fabAddList;
    private TextView textViewEmpty;
    private ShoppingListAdapter adapter;
    private List<ShoppingList> shoppingLists;
    private SharedPrefManager sharedPrefManager;

    public MyListsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_lists, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPrefManager = SharedPrefManager.getInstance(requireContext());
        initViews(view);
        setupRecyclerView();
        setupListeners();
        loadShoppingLists();
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        fabAddList = view.findViewById(R.id.fabAddList);
        textViewEmpty = view.findViewById(R.id.textViewEmpty);
    }

    private void setupRecyclerView() {
        shoppingLists = new ArrayList<>();
        adapter = new ShoppingListAdapter(shoppingLists, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupListeners() {
        fabAddList.setOnClickListener(v -> showCreateListDialog());

        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadShoppingLists();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void loadShoppingLists() {
        // TODO: Загрузка списков из локальной БД или с сервера
        // Временные данные для демонстрации
        List<ShoppingList> tempLists = new ArrayList<>();
        tempLists.add(new ShoppingList(1, "Покупки на неделю", "2024-01-15", 5, 2, false));
        tempLists.add(new ShoppingList(2, "Для вечеринки", "2024-01-16", 8, 8, true));
        tempLists.add(new ShoppingList(3, "Продукты", "2024-01-17", 12, 3, false));

        shoppingLists.clear();
        shoppingLists.addAll(tempLists);
        adapter.notifyDataSetChanged();

        updateEmptyState();
    }

    private void updateEmptyState() {
        if (shoppingLists.isEmpty()) {
            textViewEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            textViewEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void showCreateListDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Создать новый список");

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_create_list, null);
        EditText editTextListName = dialogView.findViewById(R.id.editTextListName);

        builder.setView(dialogView);
        builder.setPositiveButton("Создать", (dialog, which) -> {
            String listName = editTextListName.getText().toString().trim();
            if (!listName.isEmpty()) {
                createNewShoppingList(listName);
            }
        });
        builder.setNegativeButton("Отмена", null);

        builder.show();
    }

    private void createNewShoppingList(String listName) {
        // TODO: Создание нового списка в БД и на сервере
        ShoppingList newList = new ShoppingList(
                shoppingLists.size() + 1,
                listName,
                "2024-01-20",
                0, 0, false
        );

        shoppingLists.add(0, newList);
        adapter.notifyItemInserted(0);
        updateEmptyState();

        // Прокрутка к новому элементу
        recyclerView.scrollToPosition(0);
    }

    @Override
    public void onListClick(ShoppingList shoppingList) {
        // Переход к деталям списка
        ListDetailFragment listDetailFragment = ListDetailFragment.newInstance(shoppingList.getId());
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, listDetailFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onListLongClick(ShoppingList shoppingList) {
        showListOptionsDialog(shoppingList);
    }

    private void showListOptionsDialog(ShoppingList shoppingList) {
        String[] options = {"Редактировать", "Удалить"};

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(shoppingList.getName());
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    showEditListDialog(shoppingList);
                    break;
                case 1:
                    showDeleteListDialog(shoppingList);
                    break;
            }
        });
        builder.show();
    }

    private void showEditListDialog(ShoppingList shoppingList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Редактировать список");

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_create_list, null);
        EditText editTextListName = dialogView.findViewById(R.id.editTextListName);
        editTextListName.setText(shoppingList.getName());

        builder.setView(dialogView);
        builder.setPositiveButton("Сохранить", (dialog, which) -> {
            String newName = editTextListName.getText().toString().trim();
            if (!newName.isEmpty() && !newName.equals(shoppingList.getName())) {
                updateShoppingList(shoppingList, newName);
            }
        });
        builder.setNegativeButton("Отмена", null);

        builder.show();
    }

    private void updateShoppingList(ShoppingList shoppingList, String newName) {
        // TODO: Обновление списка в БД и на сервере
        shoppingList.setName(newName);
        int position = shoppingLists.indexOf(shoppingList);
        if (position != -1) {
            adapter.notifyItemChanged(position);
        }
    }

    private void showDeleteListDialog(ShoppingList shoppingList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Удаление списка");
        builder.setMessage("Вы уверены, что хотите удалить список \"" + shoppingList.getName() + "\"?");
        builder.setPositiveButton("Удалить", (dialog, which) -> {
            deleteShoppingList(shoppingList);
        });
        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    private void deleteShoppingList(ShoppingList shoppingList) {
        // TODO: Удаление списка из БД и с сервера
        int position = shoppingLists.indexOf(shoppingList);
        if (position != -1) {
            shoppingLists.remove(position);
            adapter.notifyItemRemoved(position);
            updateEmptyState();
        }
    }
}