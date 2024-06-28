package com.otio.client;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ActivityViewModel extends ViewModel {
    MutableLiveData<List<Activity>> activityData = new MutableLiveData<>();

    public ActivityViewModel() {}

    public void setActivityData(List<Activity> activityData) {
        this.activityData.setValue(activityData);
    }

    public MutableLiveData<List<Activity>> getActivityData() {
        return activityData;
    }

}