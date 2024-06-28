package com.otio.client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.otio.client.databinding.FragmentLoginBinding;

import java.net.ConnectException;
import java.util.Objects;
import java.util.concurrent.Executors;

public class LoginFragment extends Fragment {
    FragmentLoginBinding binding;

    OtioRepository repository;

    Handler uiHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == 1) {
                Intent i = new Intent(getActivity(), LoggedInActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
            else if ((!Objects.isNull(msg.obj)) && (msg.obj.equals("Server is not working"))) {
                Toast.makeText(getActivity(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getActivity(), "Login failed. Please check your credentials.", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        repository = new OtioRepository();
        repository.setSharedPreferences(getContext().getSharedPreferences("appPreferences", Context.MODE_PRIVATE));
        repository.setCalendarPreferences(getContext().getSharedPreferences("hourToActivityPrefs", Context.MODE_PRIVATE));
        binding.signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_registerFragment);
            }
        });

        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = binding.usernameTxt.getText().toString();
                String password = binding.passwordTxt.getText().toString();
                if (repository != null) {
                    repository.loginUser(Executors.newSingleThreadExecutor(), uiHandler, username, password);
                }
                else {
                    Toast.makeText(getContext(), "Server is not working", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return binding.getRoot();
    }
}
