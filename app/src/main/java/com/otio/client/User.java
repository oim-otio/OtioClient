package com.otio.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User {

    private String username;

    private String name;
    private String lastname;

    private String ppPath;

    List<List<String>> availableTimeslots;

    // Saved items
    List<Activity> savedActivities;

    public User() {}

    public User(String username, String name, String lastname) {
        this.username = username;
        this.name = name;
        this.lastname = lastname;
        this.availableTimeslots = new ArrayList<List<String>>();
        availableTimeslots.add(new ArrayList<>());
        this.savedActivities = new ArrayList<>();
    }

    public User(String username, String name, String lastname, String ppPath) {
        this.username = username;
        this.name = name;
        this.lastname = lastname;
        this.ppPath = ppPath;
        this.availableTimeslots = new ArrayList<List<String>>();
        availableTimeslots.add(new ArrayList<>());
        this.savedActivities = new ArrayList<>();
    }
    //Sadctivity
    public User(String username, String name, String lastname, String ppPath, List<List<String>> availableTimeslots,
                List<Activity> savedActivities) {
        this.username = username;
        this.name = name;
        this.lastname = lastname;
        this.ppPath = ppPath;
        this.availableTimeslots = availableTimeslots;
        this.savedActivities = savedActivities;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPpPath() {
        return ppPath;
    }

    public void setPpPath(String ppPath) {
        this.ppPath = ppPath;
    }

    public List<List<String>> getAvailableTimeslots() {
        return availableTimeslots;
    }

    public void setAvailableTimeslots(List<List<String>> availableTimeslots) {
        this.availableTimeslots = availableTimeslots;
    }

    public void addAvailableTimeslot(List<String> timeslotToAdd) {
        if (Objects.isNull(this.availableTimeslots)) {
            this.availableTimeslots = new ArrayList<List<String>>();
        }

        if (!availableTimeslots.contains(timeslotToAdd)) {
            availableTimeslots.add(timeslotToAdd);
        }
    }

    public void deleteTimeslot(List<String> timeslotToRemove) {
        if (availableTimeslots.contains(timeslotToRemove)) {
            availableTimeslots.remove(timeslotToRemove);
        }
    }

    public Activity findSavedActivity(String id) {
        if ((!Objects.isNull(savedActivities)) && (!savedActivities.isEmpty())) {
            for (Activity savedActivity: savedActivities) {
                if (savedActivity.getId().equals(id)) {
                    return savedActivity;
                }
            }
        }

        return null;
    }

    public List<Activity> getSavedActivities() {
        return savedActivities;
    }

    public void setSavedActivities(List<Activity> savedActivities) {
        this.savedActivities = savedActivities;
    }

    public void saveNewActivity(Activity activityToSave) {
        if (Objects.isNull(this.savedActivities)) {
            this.savedActivities = new ArrayList<Activity>();
        }

        if (!savedActivities.contains(activityToSave)) {
            savedActivities.add(activityToSave);
        }
    }

    public void deleteSavedActivity(Activity activityToDelete) {
        if (savedActivities.contains(activityToDelete)) {
            savedActivities.remove(activityToDelete);
        }
    }

    public boolean isComplete() {
        return ((!Objects.isNull(name) && (!Objects.isNull(lastname)) && (!Objects.isNull(username)) &&
                ((name != null) && (lastname != null) && (username != null))));
    }
}
