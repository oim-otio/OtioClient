package com.otio.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Day {
    private String date;
    private List<String> hours;
    private Map<String, String> hourToActivity;

    public Day(String date) {
        this.date = date;
        this.hours = new ArrayList<>();
        this.hourToActivity = new HashMap<>();
        for (int i = 0; i < 24; i++) {
            String hour = String.format(Locale.getDefault(), "%02d:00", i);
            this.hours.add(hour);
            this.hourToActivity.put(hour, "");
        }
    }

    public String getDate() {
        return date;
    }

    public List<String> getHours() {
        return hours;
    }

    public Map<String, String> getHourToActivity() {
        return hourToActivity;
    }

    public void setHourToActivity(Map<String, String> hourToActivity) {
        this.hourToActivity = hourToActivity;
    }

    public void setActivityForHour(String hour, String activity) {
        this.hourToActivity.put(hour, activity);
    }
}
