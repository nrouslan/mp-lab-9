package com.example.mp_lab_9.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mp_lab_9.R;
import com.example.mp_lab_9.activity.AuthActivity;
import com.example.mp_lab_9.data.model.User;
import com.example.mp_lab_9.network.ApiClient;
import com.example.mp_lab_9.util.SharedPrefManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProfileFragment extends Fragment {

    private TextView textViewName, textViewEmail, textViewStats;
    private Switch switchNotifications;
    private Button buttonLogout;
    private SharedPrefManager sharedPrefManager;
    private ApiClient apiClient;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPrefManager = SharedPrefManager.getInstance(requireContext());
        apiClient = new ApiClient(requireContext());
        initViews(view);
        loadUserData();
        setupListeners();
        loadUserStatistics();
    }

    private void initViews(View view) {
        textViewName = view.findViewById(R.id.textViewName);
        textViewEmail = view.findViewById(R.id.textViewEmail);
        textViewStats = view.findViewById(R.id.textViewStats);
        switchNotifications = view.findViewById(R.id.switchNotifications);
        buttonLogout = view.findViewById(R.id.buttonLogout);
    }

    private void loadUserData() {
        User user = sharedPrefManager.getUser();
        if (user != null) {
            textViewName.setText(user.getName());
            textViewEmail.setText(user.getEmail());
        }
    }

    private void loadUserStatistics() {
        apiClient.getShoppingLists(new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                requireActivity().runOnUiThread(() -> {
                    try {
                        if (response.getBoolean("success")) {
                            JSONArray listsArray = response.getJSONArray("lists");
                            int totalLists = listsArray.length();
                            int completedLists = 0;
                            int activeLists = 0;

                            for (int i = 0; i < listsArray.length(); i++) {
                                JSONObject list = listsArray.getJSONObject(i);
                                if (list.getBoolean("is_completed")) {
                                    completedLists++;
                                } else {
                                    activeLists++;
                                }
                            }

                            updateStats(totalLists, completedLists, activeLists);
                        }
                    } catch (JSONException e) {
                        updateStats(0, 0, 0);
                    }
                });
            }

            @Override
            public void onError(String error) {
                requireActivity().runOnUiThread(() -> {
                    updateStats(0, 0, 0);
                });
            }
        });
    }

    private void updateStats(int totalLists, int completedLists, int activeLists) {
        String stats = "Всего списков: " + totalLists + "\n"
                + "Завершено: " + completedLists + "\n"
                + "Активных: " + activeLists;
        textViewStats.setText(stats);
    }

    private void setupListeners() {
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(requireContext(), "Уведомления включены", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Уведомления выключены", Toast.LENGTH_SHORT).show();
            }
        });

        buttonLogout.setOnClickListener(v -> showLogoutConfirmation());
    }

    private void showLogoutConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Выход из аккаунта");
        builder.setMessage("Вы уверены, что хотите выйти?");
        builder.setPositiveButton("Выйти", (dialog, which) -> performLogout());
        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    private void performLogout() {
        sharedPrefManager.logout();

        Intent intent = new Intent(requireContext(), AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();

        Toast.makeText(requireContext(), "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show();
    }
}