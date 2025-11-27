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
import com.example.mp_lab_9.data.model.User;
import com.example.mp_lab_9.network.PutData;
import com.example.mp_lab_9.util.SharedPrefManager;
import com.example.mp_lab_9.R;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LoginFragment extends Fragment {

    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private ProgressBar progressBar;
    private TextView textViewRegister;
    private SharedPrefManager sharedPrefManager;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPrefManager = SharedPrefManager.getInstance(requireContext());
        initViews(view);
        setupListeners();
    }

    private void initViews(View view) {
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        buttonLogin = view.findViewById(R.id.buttonLogin);
        progressBar = view.findViewById(R.id.progressBar);
        textViewRegister = view.findViewById(R.id.textViewRegister);
    }

    private void setupListeners() {
        buttonLogin.setOnClickListener(v -> attemptLogin());
        textViewRegister.setOnClickListener(v -> showRegisterFragment());
    }

    private void attemptLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (validateInput(email, password)) {
            performLogin(email, password);
        }
    }

    private boolean validateInput(String email, String password) {
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

        return true;
    }

    private void performLogin(String email, String password) {
        showLoading(true);

        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                // Подготовка данных для запроса
                String[] field = {"email", "password"};
                String[] data = {email, password};
                String url = "http://your-server.com/api/login"; // Замените на ваш URL

                PutData putData = new PutData(url, "POST", field, data);
                if (putData.startPut()) {
                    if (putData.onComplete()) {
                        String result = putData.getResult();
                        requireActivity().runOnUiThread(() -> handleLoginResponse(result));
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

    private void handleLoginResponse(String response) {
        showLoading(false);

        try {
            JSONObject jsonObject = new JSONObject(response);

            if (jsonObject.has("error")) {
                String error = jsonObject.getString("error");
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            } else if (jsonObject.has("token")) {
                String token = jsonObject.getString("token");
                JSONObject userObject = jsonObject.getJSONObject("user");

                User user = new User(
                        userObject.getInt("id"),
                        userObject.getString("email"),
                        userObject.getString("name")
                );

                // Сохраняем данные пользователя
                sharedPrefManager.userLogin(user, token);

                Toast.makeText(requireContext(), "Добро пожаловать, " + user.getName() + "!", Toast.LENGTH_SHORT).show();
                redirectToMain();
            }
        } catch (JSONException e) {
            Toast.makeText(requireContext(), "Ошибка parsing JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showRegisterFragment() {
        if (getActivity() instanceof AuthActivity) {
            ((AuthActivity) getActivity()).showRegisterFragment();
        }
    }

    private void redirectToMain() {
        if (getActivity() instanceof AuthActivity) {
            ((AuthActivity) getActivity()).redirectToMain();
        }
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        buttonLogin.setVisibility(show ? View.GONE : View.VISIBLE);
        buttonLogin.setEnabled(!show);
    }
}