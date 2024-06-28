package com.otio.client;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder> {
    Context context;
    List<Activity> activities;

    OtioRepository repository;

    public ActivityAdapter(Context context, OtioRepository repository) {
        this.context = context;
        this.activities = new ArrayList<>();
        this.repository = repository;
    }

    public void setActivities(List<Activity> activities) {
        this.activities = activities;
        notifyDataSetChanged();
    }

    public class ActivityViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView activityName;
        TextView activityRating;
        TextView activityMapsLink;
        ConstraintLayout row;

        public ActivityViewHolder(View itemView) {
            super(itemView);
            row = (ConstraintLayout)itemView;
            image = itemView.findViewById(R.id.activityImage);
            activityName = itemView.findViewById(R.id.activityName);
            activityRating = itemView.findViewById(R.id.activityRating);
            activityMapsLink = itemView.findViewById(R.id.activityMapsLink);
        }
    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.activity_row, parent, false);
        ActivityViewHolder holder = new ActivityViewHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder activityViewHolder, int position) {
        Activity activity = activities.get(position);

        activityViewHolder.activityName.setText(activity.getName());
        activityViewHolder.activityRating.setText(String.valueOf(activity.getRating()));
        activityViewHolder.activityMapsLink.setText(activity.getMapsLink());
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }

    public void removeItem(int position) {
        String activityId = activities.get(position).getId();

        repository.deleteSavedActivity(Executors.newSingleThreadExecutor(), new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 1) {
                    post(() -> Toast.makeText(context, "Activity is unsaved", Toast.LENGTH_SHORT).show());
                }
                else {
                    post(() -> Toast.makeText(context, "Failed to unsave the activity", Toast.LENGTH_SHORT).show());
                }
            }
        }, activityId);
        activities.remove(position);
        notifyItemRemoved(position);
    }
}