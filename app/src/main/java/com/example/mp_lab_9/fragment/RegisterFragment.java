package com.example.mp_lab_9.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mp_lab_9.activity.AuthActivity;
import com.example.mp_lab_9.network.ApiClient;
import com.example.mp_lab_9.R;
import org.json.JSONException;
import org.json.JSONObject;

public class RegisterFragment extends Fragment {

    private EditText editTextName, editTextEmail, editTextPassword, editTextConfirmPassword;
    private Button buttonRegister;
    private ProgressBar progressBar;
    private TextView textViewLogin;
    private ApiClient apiClient;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        apiClient = new ApiClient(requireContext());
        initViews(view);
        setupListeners();
    }

    private void initViews(View view) {
        editTextName = view.findViewById(R.id.editTextName);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        editTextConfirmPassword = view.findViewById(R.id.editTextConfirmPassword);
        buttonRegister = view.findViewById(R.id.buttonRegister);
        progressBar = view.findViewById(R.id.progressBar);
        textViewLogin = view.findViewById(R.id.textViewLogin);
    }

    private void setupListeners() {
        buttonRegister.setOnClickListener(v -> attemptRegistration());
        textViewLogin.setOnClickListener(v -> showLoginFragment());
    }

    private void attemptRegistration() {
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        if (validateInput(name, email, password, confirmPassword)) {
            performRegistration(name, email, password);
        }
    }

    private boolean validateInput(String name, String email, String password, String confirmPassword) {
        if (name.isEmpty()) {
            editTextName.setError("Введите имя");
            editTextName.requestFocus();
            return false;
        }

        if (email.isEmpty()) {
            editTextEmail.setError("Введите email");
            editTextEmail.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Введите пароль");
            editTextPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Пароль должен содержать минимум 6 символов");
            editTextPassword.requestFocus();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            editTextConfirmPassword.setError("Пароли не совпадают");
            editTextConfirmPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void performRegistration(String name, String email, String password) {
        showLoading(true);

        apiClient.register(name, email, password, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                requireActivity().runOnUiThread(() -> {
                    showLoading(false);
                    try {
                        if (response.getBoolean("success")) {
                            String message = response.getString("message");
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();

                            if (getActivity() != null) {
                                getActivity().onBackPressed();
                            }
                        } else {
                            String error = response.optString("message", "Registration failed");
                            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
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
                });
            }
        });
    }

    private void showLoginFragment() {
        if (getActivity() instanceof AuthActivity) {
            ((AuthActivity) getActivity()).showLoginFragment();
        }
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        buttonRegister.setVisibility(show ? View.GONE : View.VISIBLE);
        buttonRegister.setEnabled(!show);
    }
}