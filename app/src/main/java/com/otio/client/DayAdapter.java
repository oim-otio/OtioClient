package com.otio.client;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// DayAdapter
public class DayAdapter extends RecyclerView.Adapter<DayAdapter.DayViewHolder> {
    private List<Day> days;
    private HoursAdapter.OnHourClickListener listener;

    public DayAdapter(List<Day> days, HoursAdapter.OnHourClickListener listener) {
        this.days = days;
        this.listener = listener;
    }

    public List<Day> getDays() {
        return days;
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.day_row, parent, false);
        return new DayViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        Day day = days.get(position);
        holder.dateText.setText(day.getDate());

        if (holder.hoursAdapter == null) {
            holder.hoursAdapter = new HoursAdapter(day.getHours(), day.getHourToActivity(), day.getDate(), listener);
            holder.hoursRecyclerView.setAdapter(holder.hoursAdapter);
        }
        else {
            holder.hoursAdapter.setHours(day.getHours());
            holder.hoursAdapter.setHourToActivity(day.getHourToActivity());
            holder.hoursAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    public class DayViewHolder extends RecyclerView.ViewHolder {
        TextView dateText;
        RecyclerView hoursRecyclerView;
        HoursAdapter hoursAdapter;

        public DayViewHolder(View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.dateText);
            hoursRecyclerView = itemView.findViewById(R.id.hoursRecyclerView);
            hoursRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.VERTICAL, false));
        }
    }
}
