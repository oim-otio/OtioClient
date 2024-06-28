package com.otio.client;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.otio.client.databinding.ActivityLoggedoutBinding;

import java.util.concurrent.Executors;

public class LoggedOutActivity extends AppCompatActivity {
    ActivityLoggedoutBinding binding;
    OtioRepository repository;
    View root;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoggedoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        root = new View(this);


        SharedPreferences sharedPreferences = getSharedPreferences("appPreferences", MODE_PRIVATE);
        String token = sharedPreferences.getString("AuthToken", "");

        if (!(token.equals(""))) {
            repository = new OtioRepository();
            repository.setSharedPreferences(this.getSharedPreferences("appPreferences", Context.MODE_PRIVATE));
            repository.setCalendarPreferences(this.getSharedPreferences("hourToActivityPrefs", Context.MODE_PRIVATE));
            repository.checkToken(Executors.newSingleThreadExecutor(), new Handler(Looper.getMainLooper()), isTokenValid -> {
                if (isTokenValid) {
                    Intent i = new Intent(this, LoggedInActivity.class);
                    startActivity(i);
                    finish();
                }
                else {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("AuthToken");
                    editor.remove("Username");
                    editor.apply();
                }
            });
        }
    }
}
