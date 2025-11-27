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
import com.example.mp_lab_9.network.PutData;
import com.example.mp_lab_9.R;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RegisterFragment extends Fragment {

    private EditText editTextName, editTextEmail, editTextPassword, editTextConfirmPassword;
    private Button buttonRegister;
    private ProgressBar progressBar;
    private TextView textViewLogin;

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

        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                String[] field = {"name", "email", "password"};
                String[] data = {name, email, password};
                String url = "http://your-server.com/api/register"; // Замените на ваш URL

                PutData putData = new PutData(url, "POST", field, data);
                if (putData.startPut()) {
                    if (putData.onComplete()) {
                        String result = putData.getResult();
                        requireActivity().runOnUiThread(() -> handleRegistrationResponse(result));
                    }
                }
            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(requireContext(), "Ошибка сети: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void handleRegistrationResponse(String response) {
        showLoading(false);

        try {
            JSONObject jsonObject = new JSONObject(response);

            if (jsonObject.has("error")) {
                String error = jsonObject.getString("error");
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            } else if (jsonObject.has("message")) {
                String message = jsonObject.getString("message");
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();

                // Возвращаемся к экрану логина после успешной регистрации
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }
        } catch (JSONException e) {
            Toast.makeText(requireContext(), "Ошибка parsing JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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