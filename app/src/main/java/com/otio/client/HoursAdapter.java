package com.otio.client;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class HoursAdapter extends RecyclerView.Adapter<HoursAdapter.HourViewHolder> {
    private String date;
    private List<String> hours;
    private Map<String, String> hourToActivity;

    private OnHourClickListener listener;

    public interface OnHourClickListener {
        void onHourClick(String date, String hour);
    }

    public HoursAdapter(List<String> hours, Map<String, String> hourToActivity, String date, OnHourClickListener listener) {
        this.hours = hours;
        this.hourToActivity = hourToActivity;
        this.date = date;
        this.listener = listener;
    }

    public void setHours(List<String> hours) {
        this.hours = hours;
    }

    public void setHourToActivity(Map<String, String> hourToActivity) {
        this.hourToActivity = hourToActivity;
    }

    @NonNull
    @Override
    public HourViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hour_row, parent, false);
        return new HourViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HourViewHolder holder, int position) {
        String hour = hours.get(position);

        String activity = hourToActivity.getOrDefault(hour, "");
        if (activity.equals("")) {
            holder.hourText.setText(hour);
        }
        else {
            holder.hourText.setText(hour + " - " + activity);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onHourClick(date, hour);
            };
        });
    }

    @Override
    public int getItemCount() {
        return hours.size();
    }

    class HourViewHolder extends RecyclerView.ViewHolder {
        TextView hourText;

        HourViewHolder(View itemView) {
            super(itemView);
            hourText = itemView.findViewById(R.id.txtHour);
        }
    }

}
