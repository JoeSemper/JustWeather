package com.joesemper.justweather;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.DaysViewHolder>  {

    private List<String> days;

    public void setDays (List<String> days) {
        this.days = days;
    }

    @NonNull
    @Override
    public DaysViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new DaysViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.day_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DaysViewHolder holder, int position) {
        holder.bind(days.get(position));
    }

    @Override
    public int getItemCount() {
        if (days == null) {
            return 0;
        }
        return days.size();
    }


    static class DaysViewHolder extends RecyclerView.ViewHolder {

        private final TextView date;
        private final ImageView weatherIcon;
        private final TextView weather;

        public DaysViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date_text);
            weatherIcon = itemView.findViewById(R.id.weather_icon);
            weather = itemView.findViewById(R.id.day_temperature);
        }

        void bind (final String date) {
            this.date.setText(date);
        }

    }
}
