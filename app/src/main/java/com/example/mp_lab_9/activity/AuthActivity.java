package com.example.mp_lab_9.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.mp_lab_9.R;
import com.example.mp_lab_9.fragment.LoginFragment;
import com.example.mp_lab_9.fragment.RegisterFragment;
import com.example.mp_lab_9.util.SharedPrefManager;


public class AuthActivity extends AppCompatActivity {

    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        sharedPrefManager = SharedPrefManager.getInstance(this);

        // Проверяем, не авторизован ли уже пользователь
        if (sharedPrefManager.isLoggedIn()) {
            redirectToMain();
            return;
        }

        // Показываем фрагмент логина по умолчанию
        if (savedInstanceState == null) {
            showLoginFragment();
        }
    }

    public void showLoginFragment() {
        Fragment loginFragment = new LoginFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, loginFragment);
        transaction.commit();
    }

    public void showRegisterFragment() {
        Fragment registerFragment = new RegisterFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, registerFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void redirectToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}