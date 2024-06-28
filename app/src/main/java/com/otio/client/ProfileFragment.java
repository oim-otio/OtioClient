package com.otio.client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.otio.client.databinding.FragmentProfileBinding;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfileFragment extends Fragment {
    FragmentProfileBinding binding;
    OtioRepository repository;
    ActivityAdapter adapter;
    ActivityViewModel viewModel;

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            adapter.removeItem(position);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        repository = new OtioRepository();
        repository.setSharedPreferences(getContext().getSharedPreferences("appPreferences", Context.MODE_PRIVATE));
        repository.setCalendarPreferences(getContext().getSharedPreferences("hourToActivityPrefs", Context.MODE_PRIVATE));
        updateUserProfile();

        viewModel = new ViewModelProvider(this).get(ActivityViewModel.class);

        adapter = new ActivityAdapter(getContext(), repository);

        binding.recyclerSavedActivities.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerSavedActivities.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(binding.recyclerSavedActivities);

        Executors.newSingleThreadExecutor().execute(() -> {
            repository.getSavedActivities(Executors.newSingleThreadExecutor(), viewModel);
        });

        viewModel.getActivityData().observe(getViewLifecycleOwner(), activities -> {
            if (activities != null) {
                adapter.setActivities(activities);
            }
        });

        binding.btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repository.logoutUser(Executors.newSingleThreadExecutor(), new Handler(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(@NonNull Message msg) {
                        if (msg.what == 1) {
                            Intent i = new Intent(getActivity(), LoggedOutActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            return true;
                        }
                        else {
                            Toast.makeText(getContext(), "Failed to log out. Please try again.", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    }
                }));


            }
        });

        binding.btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repository.deleteUserAccount(Executors.newSingleThreadExecutor(), new Handler(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(@NonNull Message msg) {
                        if (msg.what == 1) {
                            Intent i = new Intent(getActivity(), LoggedOutActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            Toast.makeText(getContext(), "Account is successfully deleted", Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        else {
                            Toast.makeText(getContext(), "Failed to delete the account. Please try again.", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    }
                }));


            }
        });

        return binding.getRoot();
    }

    private void updateUserProfile() {
        String username = getContext().getSharedPreferences("appPreferences", Context.MODE_PRIVATE).getString("Username", "");
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.obj instanceof User) {
                    User user = (User) msg.obj;
                    StringBuilder nameBuilder = new StringBuilder();
                    nameBuilder.append(user.getName()).append(" ").append(user.getLastname());
                    binding.textNameSurname.setText(nameBuilder.toString());
                    binding.textUsername.setText(user.getUsername());
                }
                else {
                    Toast.makeText(getContext(), "Failed to fetch user details.", Toast.LENGTH_SHORT).show();
                }
            }
        };

        Executors.newSingleThreadExecutor().execute(() -> {
            repository.getUser(Executors.newSingleThreadExecutor(), handler, username);
        });
    }
}
