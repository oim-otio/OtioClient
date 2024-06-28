package com.otio.client;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.otio.client.databinding.FragmentCalendarBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CalendarFragment extends Fragment implements HoursAdapter.OnHourClickListener {
    FragmentCalendarBinding binding;
    OtioRepository repository;

    private void notifyDayAdapter(Day updatedDay) {
        DayAdapter dayAdapter = (DayAdapter) binding.dayRecycler.getAdapter();
        if (dayAdapter != null) {
            int position = dayAdapter.getDays().indexOf(updatedDay);
            if (position != -1) {
                dayAdapter.notifyItemChanged(position);
            }
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        repository = new OtioRepository();
        repository.setSharedPreferences(getContext().getSharedPreferences("appPreferences", Context.MODE_PRIVATE));
        repository.setCalendarPreferences(getContext().getSharedPreferences("hourToActivityPrefs", Context.MODE_PRIVATE));
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<Day> days = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            String date = String.format(Locale.getDefault(), "2024-06-%02d", i + 1);
            days.add(new Day(date));
        }

        DayAdapter dayAdapter = new DayAdapter(days, this::onHourClick);

        binding.dayRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.dayRecycler.setAdapter(dayAdapter);

        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.getHourToActivityMap().observe(getViewLifecycleOwner(), hourToActivity -> {
            for (Day day: days) {
                Map<String, String> dailyActivities = hourToActivity.get(day.getDate());
                if (dailyActivities != null) {
                    day.setHourToActivity(dailyActivities);
                    notifyDayAdapter(day);
                }
            }
        });

    }

    @Override
    public void onHourClick(String date, String hour) {
        List<String> timeslot = new ArrayList<>();
        String formattedHour = String.format(Locale.getDefault(), "%02d:00:00", Integer.parseInt(hour.substring(0, 2)));
        timeslot.add(formattedHour);
        int nextHour = (Integer.parseInt(hour.substring(0, 2)) + 1) % 24;
        String formattedNextHour = String.format(Locale.getDefault(), "%02d:00:00", nextHour);
        timeslot.add(formattedNextHour);

        Handler uiHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 1) {
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("timeslot", (ArrayList<String>)timeslot);
                    bundle.putString("selectedDate", date);
                    Navigation.findNavController((AppCompatActivity)getContext(), R.id.login_nav_host).navigate(R.id.action_calendarFragment_to_selectorFragment, bundle);
                }
            }
        };

        repository.addTimeslot(Executors.newSingleThreadExecutor(), uiHandler, timeslot);

    }
}