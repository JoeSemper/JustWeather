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
        private final View dayView;

        private final TextView morTemperature;
        private final TextView dayTemperature;
        private final TextView eveTemperature;
        private final TextView nigTemperature;

        private final ImageView dayWeatherIcon;
        private final TextView dayWeather;

        private final TextView dayWind;
        private final TextView dayPressure;
        private final TextView dayPop;
        private final TextView dayCloudiness;


        public DaysViewHolder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.date_text);
            dayView = itemView.findViewById(R.id.day_forecast);

            morTemperature = itemView.findViewById(R.id.day_morning_temp);
            dayTemperature = itemView.findViewById(R.id.day_daytime_temp);
            eveTemperature = itemView.findViewById(R.id.day_evening_temp);
            nigTemperature = itemView.findViewById(R.id.day_night_temp);

            dayWeatherIcon = itemView.findViewById(R.id.day_weather_icon);
            dayWeather = itemView.findViewById(R.id.day_wether_text);

            dayWind = itemView.findViewById(R.id.day_wind_text);
            dayPressure = itemView.findViewById(R.id.day_pressure_text);
            dayPop = itemView.findViewById(R.id.day_pop_text);
            dayCloudiness = itemView.findViewById(R.id.day_cloudiness_text);
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
            if (weatherParser == null) {
                return;
            }

            morTemperature.setText(weatherParser.getMornTemp(position));
            dayTemperature.setText(weatherParser.getDayTemp(position));
            eveTemperature.setText(weatherParser.getEveTemp(position));
            nigTemperature.setText(weatherParser.getNightTemp(position));

            dayWeatherIcon.setImageResource(weatherParser.getDayWeatherIcon(position));
            dayWeather.setText(weatherParser.getDayWeather(position));

            dayWind.setText(weatherParser.getDayWindSpeed(position));
            dayPressure.setText(weatherParser.getDayPressure(position));
            dayPop.setText(weatherParser.getDayPop(position));
            dayCloudiness.setText(weatherParser.getDayCloudiness(position));
        }

        interface OnDayClickListener {
            void onClicked(String day);
        }
    }

}
