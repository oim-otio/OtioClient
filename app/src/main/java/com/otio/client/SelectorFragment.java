package com.otio.client;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.otio.client.databinding.FragmentSelectorBinding;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SelectorFragment extends Fragment {
    FragmentSelectorBinding binding;
    SharedViewModel viewModel;

    OtioRepository repository;

    List<Activity> activities;

    SharedActivityAdapter activityAdapter;

    String currentCategory = null;
    double currentRating = -1;

    private void filterActivities() {
        if (activities == null) {
            return;
        }

        Stream<Activity> filteredStream = activities.stream();

        if (currentCategory != null) {
            filteredStream = filteredStream.filter(activity -> activity.getId().contains(currentCategory));
        }
        if (currentRating != -1) {
            filteredStream = filteredStream.filter(activity -> activity.getRating() >= currentRating);
        }

        List<Activity> filteredActivities = filteredStream.collect(Collectors.toList());
        activityAdapter.setActivities(filteredActivities);
        activityAdapter.notifyDataSetChanged();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSelectorBinding.inflate(inflater, container, false);


        repository = new OtioRepository();
        repository.setSharedPreferences(getContext().getSharedPreferences("appPreferences", Context.MODE_PRIVATE));
        repository.setCalendarPreferences(getContext().getSharedPreferences("hourToActivityPrefs", Context.MODE_PRIVATE));

        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        List<String> categories = Arrays.asList("Category", "Dining", "Entertainment", "Arts & Culture", "Nightlife", "Outdoor & Adventure", "Relaxation & Wellness");
        List<String> categoryIds = Arrays.asList("Empty", "DNN", "ENT", "ART", "NHT", "OUT", "WEL");

        List<String> ratings = Arrays.asList("Rating", "1.0", "2.0", "3.0", "4.0", "5.0");

        activities = viewModel.getActivityData().getValue();
        String selectedDate = getArguments().getString("selectedDate");
        String selectedHour = getArguments().getStringArrayList("timeslot").get(0);
        activityAdapter = new SharedActivityAdapter(getContext(), selectedHour, viewModel);
        activityAdapter.setListener(activity -> {
            viewModel.setActivityForHour(selectedDate, selectedHour.substring(0, 5), activity.getName());
            repository.saveActivity(Executors.newSingleThreadExecutor(), new Handler(Looper.getMainLooper()), activity);
            Navigation.findNavController(getView()).navigate(R.id.action_selectorFragment_to_calendarFragment);
            Navigation.findNavController(getView()).popBackStack(R.id.registerFragment, true);
        });

        Handler searchHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 1) {
                    activities = (List<Activity>)msg.obj;

                    activityAdapter.setActivities(activities);
                    activityAdapter.filterActivitiesByTimeSlot(getArguments().getStringArrayList("timeslot"));
                    activityAdapter.notifyDataSetChanged();
                }
                else {
                    Toast.makeText(getContext(), "Failed to load activities", Toast.LENGTH_SHORT).show();
                }
            }
        };

        binding.searchActivityName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                repository.searchByName(Executors.newSingleThreadExecutor(), searchHandler, s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.recyclerActivities.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerActivities.setAdapter(activityAdapter);

        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 1) {
                    activities = (List<Activity>)msg.obj;
                    activityAdapter.setActivities(activities);
                    activityAdapter.filterActivitiesByTimeSlot(getArguments().getStringArrayList("timeslot"));
                }
                else {
                    Toast.makeText(getContext(), "Failed to load available activities", Toast.LENGTH_SHORT).show();
                }
            }
        };

        // Fetch all activities
        repository.searchByName(Executors.newSingleThreadExecutor(), handler, "");

        ArrayAdapter<String> subcategoryAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, categories);
        binding.spinnerSubcategory.setAdapter(subcategoryAdapter);

        //setOnItemClickListener cannot be used with a spinner
        binding.spinnerSubcategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentCategory = position > 0 ? categoryIds.get(position) : null;
                filterActivities();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        ArrayAdapter<String> ratingAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, ratings);
        binding.spinnerRating.setAdapter(ratingAdapter);

        binding.spinnerRating.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentRating = position > 0 ? Double.parseDouble(parent.getItemAtPosition(position).toString()) : -1;
                filterActivities();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        binding.recyclerActivities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getArguments().getStringArrayList("timeslot") != null) {
                    repository.removeTimeslot(Executors.newSingleThreadExecutor(), new Handler(Looper.getMainLooper()), getArguments().getStringArrayList("timeslot"));
                }

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });

        binding.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getArguments().getStringArrayList("timeslot") != null) {
                    repository.removeTimeslot(Executors.newSingleThreadExecutor(), new Handler(Looper.getMainLooper()), getArguments().getStringArrayList("timeslot"));
                    if ((!Objects.isNull(viewModel.getHourToActivityMap().getValue().get(selectedDate).get(selectedHour.substring(0, 5)))) && (!(viewModel.getHourToActivityMap().getValue().get(selectedDate).get(selectedHour.substring(0, 5)).equals("")))) {
                        viewModel.setActivityForHour(selectedDate, selectedHour.substring(0, 5), "");
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                    else {
                        Toast.makeText(getContext(), "There is no activity for the selected hour", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getContext(), "Failed to fetch the timeslot", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return binding.getRoot();
    }

}