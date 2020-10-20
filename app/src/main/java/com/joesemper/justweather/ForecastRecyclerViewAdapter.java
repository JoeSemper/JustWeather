package com.joesemper.justweather;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.joesemper.justweather.forecast.WeatherParser;
import com.joesemper.justweather.retrofit.RetrofitUpdater;

import java.util.List;

public class ForecastRecyclerViewAdapter extends RecyclerView.Adapter<ForecastRecyclerViewAdapter.DaysViewHolder>  {

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
        holder.bind(position, days.get(position), onDayClickListener);
    }

    @Override
    public int getItemCount() {
        if (days == null) {
            return 0;
        }
        return days.size();
    }


    public static class DaysViewHolder extends RecyclerView.ViewHolder {

        private final TextView date;
        private final ImageView weatherIcon;
        private final TextView weather;
        private final View dayView;

        private ImageView dayWeatherIcon;
        private TextView dayWeather;
        private TextView dayMinMaxTemp;
        private TextView dayWind;


        public DaysViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date_text);
            weatherIcon = itemView.findViewById(R.id.weather_icon);
            weather = itemView.findViewById(R.id.day_temperature);
            dayView = itemView.findViewById(R.id.day_forecast);

            dayWeatherIcon = itemView.findViewById(R.id.weather_icon);
            dayWeather = itemView.findViewById(R.id.day_weather);
            dayMinMaxTemp = itemView.findViewById(R.id.day_temperature);
            dayWind = itemView.findViewById(R.id.day_wind_text);
        }

        void bind (final int position, final String date, final OnDayClickListener onDayClickListener) {
            setWeather(position);
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

        private void setWeather (int position){
            WeatherParser weatherParser = RetrofitUpdater.getWeatherParser();
            dayWeatherIcon.setImageResource(weatherParser.getDayWeatherIcon(position));
            dayWeather.setText(weatherParser.getDayWeather(position));
            dayMinMaxTemp.setText(weatherParser.getDayMinMaxTemp(position));
            dayWind.setText(weatherParser.getDayWindSpeed(position));
        }

        interface OnDayClickListener {
            void onClicked(String day);
        }
    }

}
