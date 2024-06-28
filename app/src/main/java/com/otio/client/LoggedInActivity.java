package com.otio.client;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.otio.client.databinding.ActivityLoggedinBinding;

public class LoggedInActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavBar;

    ActivityLoggedinBinding binding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoggedinBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        bottomNavBar = binding.bottomNavigationView;

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.login_nav_host);
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(bottomNavBar, navController);
    }

    @Override
    protected void onResume() {
        super.onResume();

        NavController navController = Navigation.findNavController(this, R.id.login_nav_host);
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {
                if (navDestination.getId() == R.id.selectorFragment) {
                    bottomNavBar.setVisibility(View.GONE);
                }
                else {
                    bottomNavBar.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
