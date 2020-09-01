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
    private DaysViewHolder.OnDayClickListener onDayClickListener;
    private int dayView = R.layout.day_view;

    public void setDays (List<String> days) {
        this.days = days;
    }

    public void setOnDayClickListener(DaysViewHolder.OnDayClickListener onDayClickListener) {
        this.onDayClickListener = onDayClickListener;
    }


    @NonNull
    @Override
    public DaysViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new DaysViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.day_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DaysViewHolder holder, int position) {
        holder.bind(days.get(position), onDayClickListener);
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
        private final View dayView;

        public DaysViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date_text);
            weatherIcon = itemView.findViewById(R.id.weather_icon);
            weather = itemView.findViewById(R.id.day_temperature);
            dayView = itemView.findViewById(R.id.day_forecast);
        }

        void bind (final String date, final OnDayClickListener onDayClickListener) {
            this.date.setText(date);
            dayView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onDayClickListener != null) {
                        onDayClickListener.onClicked(date);
                    }

                }
            });
        }

        interface OnDayClickListener {
            void onClicked(String day);
        }

    }
}
