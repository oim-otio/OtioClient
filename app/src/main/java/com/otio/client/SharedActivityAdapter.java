package com.otio.client;

import android.content.Context;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class SharedActivityAdapter extends RecyclerView.Adapter<SharedActivityAdapter.SharedViewHolder> {
    private Context context;
    private List<Activity> activities;
    private List<Activity> allActivities;
    private SharedViewModel sharedViewModel;
    private String selectedHour;

    interface ActivityClickListener {
        void activityClicked(Activity activity);
    }

    private ActivityClickListener listener;

    public SharedActivityAdapter(Context context, String selectedHour, SharedViewModel sharedViewModel) {
        this.context = context;
        this.selectedHour = selectedHour;
        this.sharedViewModel = sharedViewModel;
        this.activities = new ArrayList<>();
        this.allActivities = new ArrayList<>();
    }

    public boolean isOverlap(List<String> selectedTime, List<String> timeSlot) {
        LocalTime selectedStart = LocalTime.parse(selectedTime.get(0));
        LocalTime selectedEnd = LocalTime.parse(selectedTime.get(1));
        LocalTime slotStart = LocalTime.parse(timeSlot.get(0));
        LocalTime slotEnd = LocalTime.parse(timeSlot.get(1));

        if (slotStart.isAfter(slotEnd)) {
            LocalTime temp = slotEnd;
            slotEnd = slotStart;
            slotStart = temp;
        }

        return !selectedStart.isAfter(slotEnd) && !(selectedEnd.minusMinutes(1)).isBefore(slotStart);
    }

    public void filterActivitiesByTimeSlot(List<String> timeslot) {
        activities.clear();
        for (Activity activity: allActivities) {
            if (isOverlap(timeslot, activity.getTimeSlots())) {
                activities.add(activity);
            }
        }

        notifyDataSetChanged();
    }

    public void setActivities(List<Activity> activities) {
        this.activities = activities;
        this.allActivities = new ArrayList<>(activities);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SharedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_row, parent, false);
        return new SharedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SharedViewHolder holder, int position) {
        Activity activity = activities.get(position);
        holder.activityName.setText(activity.getName());
        holder.activityRating.setText(String.valueOf(activity.getRating()));
        holder.activityMapsLink.setText(activity.getMapsLink());

        holder.row.setOnClickListener(v -> {
            if (listener != null) {
                listener.activityClicked(activity);
            }
        });
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }

    public void setListener(ActivityClickListener listener) {
        this.listener = listener;
    }

    class SharedViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView activityName;
        TextView activityRating;
        TextView activityMapsLink;
        ConstraintLayout row;

        public SharedViewHolder(View itemView) {
            super(itemView);
            row = (ConstraintLayout)itemView;
            image = itemView.findViewById(R.id.activityImage);
            activityName = itemView.findViewById(R.id.activityName);
            activityRating = itemView.findViewById(R.id.activityRating);
            activityMapsLink = itemView.findViewById(R.id.activityMapsLink);
        }
    }
}
