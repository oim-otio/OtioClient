package com.otio.client;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SharedViewModel extends AndroidViewModel {
    private final MutableLiveData<Map<String, Map<String, String>>> hourToActivityMap = new MutableLiveData<>(new HashMap<>());

    SharedPreferences sharedPreferences = getApplication().getSharedPreferences("appPreferences", Context.MODE_PRIVATE);

    private String PREFS_NAME = "hourToActivityPrefs";
    private String HOUR_TO_ACTIVITY_MAP_KEY = sharedPreferences.getString("Username", "");

    MutableLiveData<List<Activity>> activityData = new MutableLiveData<>();

    MutableLiveData<Activity> selectedActivity = new MutableLiveData<>();

    public SharedViewModel(@NonNull Application application) {
        super(application);
        HOUR_TO_ACTIVITY_MAP_KEY += "_map";
        loadHourToActivityMap();
    }

    public LiveData<Map<String, Map<String, String>>> getHourToActivityMap() {
        return hourToActivityMap;
    }

    public void setActivityForHour(String date, String hour, String activityName) {
        Map<String, Map<String, String>> dateMap = hourToActivityMap.getValue();
        if (!dateMap.containsKey(date)) {
            dateMap.put(date, new HashMap<>());
        }
        dateMap.get(date).put(hour, activityName);
        hourToActivityMap.setValue(dateMap);
        saveHourToActivityMap(dateMap);
    }

    private void saveHourToActivityMap(Map<String, Map<String, String>> map) {
        SharedPreferences calendarPreferences = getApplication().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = calendarPreferences.edit();
        StringBuilder mapAsString = new StringBuilder();
        for (Map.Entry<String, Map<String, String>> entry : map.entrySet()) {
            mapAsString.append(entry.getKey()).append("!");
            for (Map.Entry<String, String> subEntry : entry.getValue().entrySet()) {
                mapAsString.append(subEntry.getKey()).append("-").append(subEntry.getValue()).append(",");
            }
            mapAsString.append(";");
        }
        editor.putString(HOUR_TO_ACTIVITY_MAP_KEY, mapAsString.toString());
        editor.apply();
    }


    private void loadHourToActivityMap() {
        SharedPreferences sharedPreferences = getApplication().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String savedMapString = sharedPreferences.getString(HOUR_TO_ACTIVITY_MAP_KEY, "");
        Map<String, Map<String, String>> map = new HashMap<>();
        if (!savedMapString.isEmpty()) {
            String[] dateEntries = savedMapString.split(";");
            for (String dateEntry : dateEntries) {
                if (!dateEntry.isEmpty()) {
                    String[] parts = dateEntry.split("!");
                    String date = parts[0];
                    Map<String, String> hourMap = new HashMap<>();
                    if (parts.length > 1) {
                        String[] hourEntries = parts[1].split(",");
                        for (String hourEntry : hourEntries) {
                            if (!hourEntry.isEmpty()) {
                                String[] hourParts = hourEntry.split("-");
                                if (hourParts.length == 2) {
                                    hourMap.put(hourParts[0], hourParts[1]);
                                }
                            }
                        }
                    }
                    map.put(date, hourMap);
                }
            }
        }
        hourToActivityMap.setValue(map);
    }

    public void setSelectedActivity(Activity selectedActivity) {
        this.selectedActivity.setValue(selectedActivity);
    }

    public MutableLiveData<Activity> getSelectedActivity() {
        return selectedActivity;
    }

    public void setActivityData(List<Activity> activityData) {
        this.activityData.setValue(activityData);
    }

    public MutableLiveData<List<Activity>> getActivityData() {
        return activityData;
    }


}
