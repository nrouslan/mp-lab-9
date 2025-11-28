package com.example.mp_lab_9.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mp_lab_9.R;
import com.example.mp_lab_9.activity.AuthActivity;
import com.example.mp_lab_9.data.model.User;
import com.example.mp_lab_9.util.SharedPrefManager;

public class ProfileFragment extends Fragment {

    private TextView textViewName, textViewEmail;
    private Button buttonLogout;
    private SharedPrefManager sharedPrefManager;

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
        initViews(view);
        loadUserData();
        setupListeners();
    }

    private void initViews(View view) {
        textViewName = view.findViewById(R.id.textViewName);
        textViewEmail = view.findViewById(R.id.textViewEmail);
        buttonLogout = view.findViewById(R.id.buttonLogout);
    }

    private void loadUserData() {
        User user = sharedPrefManager.getUser();
        if (user != null) {
            textViewName.setText(user.getName());
            textViewEmail.setText(user.getEmail());
        }
    }

    private void setupListeners() {
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