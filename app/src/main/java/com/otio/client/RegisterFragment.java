package com.otio.client;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.otio.client.databinding.FragmentRegisterBinding;

import java.util.Objects;
import java.util.concurrent.Executors;

public class RegisterFragment extends Fragment {
    FragmentRegisterBinding binding;
    OtioRepository repository;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        repository = new OtioRepository();
        repository.setSharedPreferences(getContext().getSharedPreferences("appPreferences", Context.MODE_PRIVATE));
        repository.setCalendarPreferences(getContext().getSharedPreferences("hourToActivityPrefs", Context.MODE_PRIVATE));
        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = binding.editTextName.getText().toString();
                String surname = binding.editTextSurname.getText().toString();
                String username = binding.editTextUsername.getText().toString();
                String password = binding.editTextPassword.getText().toString();

                if (validateInput(name, surname, username, password)) {
                    registerUser(name, surname, username, password);
                } else {
                    Toast.makeText(getContext(), "Please fill all fields correctly.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return binding.getRoot();
    }
    private boolean validateInput(String name, String surname, String username, String password) {
        return !name.isEmpty() && !surname.isEmpty() && !username.isEmpty() && !password.isEmpty();
    }

    private void registerUser(String name, String surname, String username, String password) {
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    Navigation.findNavController(getView()).navigate(R.id.action_registerFragment_to_signedUpFragment);
                }
                else if ((!Objects.isNull(msg.obj)) && (msg.obj.equals("Server is not working"))) {
                    Toast.makeText(getContext(), msg.obj.toString(), Toast.LENGTH_LONG).show();
                    Navigation.findNavController(getView()).navigate(R.id.action_registerFragment_to_loginFragment);
                    Navigation.findNavController(getView()).popBackStack(R.id.registerFragment, true);
                }
                else if (!Objects.isNull(msg.obj)) {
                    Toast.makeText(getContext(), "Registration failed: " + msg.obj, Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getContext(), "Registration failed", Toast.LENGTH_LONG).show();
                }
            }
        };

        Executors.newSingleThreadExecutor().execute(() -> {
            repository.registerUser(Executors.newSingleThreadExecutor(), handler, name, surname, username, password);
        });
    }
}
