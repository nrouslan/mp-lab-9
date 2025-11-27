package com.example.mp_lab_9.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.mp_lab_9.R;
import com.example.mp_lab_9.fragment.MyListsFragment;
import com.example.mp_lab_9.fragment.ProfileFragment;
import com.example.mp_lab_9.util.SharedPrefManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private SharedPrefManager sharedPrefManager;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Мониторинг блокировок главного потока
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build());

        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .build());

        setContentView(R.layout.activity_main);

        sharedPrefManager = SharedPrefManager.getInstance(this);

        // Проверяем авторизацию
        if (!sharedPrefManager.isLoggedIn()) {
            redirectToAuth();
            return;
        }

        initViews();
        setupBottomNavigation();

        // Показываем фрагмент по умолчанию
        if (savedInstanceState == null) {
            showMyListsFragment();
        }
    }

    private void initViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_my_lists) {
                showMyListsFragment();
                return true;
            } else if (itemId == R.id.nav_profile) {
                showProfileFragment();
                return true;
            }
            return false;
        });
    }

    private void showMyListsFragment() {
        Fragment fragment = new MyListsFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();

        setToolbarTitle(getString(R.string.title_my_lists));
    }

    private void showProfileFragment() {
        Fragment fragment = new ProfileFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();

        setToolbarTitle(getString(R.string.title_profile));
    }

    private void setToolbarTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    private void redirectToAuth() {
        startActivity(new Intent(this, AuthActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        // Если текущий фрагмент - не MyLists, возвращаемся к спискам
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (!(currentFragment instanceof MyListsFragment)) {
            bottomNavigationView.setSelectedItemId(R.id.nav_my_lists);
        } else {
            super.onBackPressed();
        }
    }
}