package com.joesemper.justweather;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.joesemper.justweather.forecast.MainForecast;
import com.joesemper.justweather.interfaces.Constants;
import com.joesemper.justweather.interfaces.ForecastUpdater;

import java.util.Arrays;
import java.util.Date;
import java.util.Objects;


public class MainActivity extends AppCompatActivity implements Constants {

    private Date date = new Date();
    private String[] days = new String[7];

    private TextView city;
    private TextView temperature;
    private TextView pressure;
    private ImageView pressureIcon;
    private TextView windSpeed;
    private ImageView windIcon;
    private ImageView mainWeatherIcon;
    private TextView currentWeather;
    private ImageButton settingsButton;
    private TextView currentDate;

    private final ForecastRecyclerViewAdapter recyclerViewAdapter = new ForecastRecyclerViewAdapter();

    private static ForecastUpdater forecastUpdater = new ForecastUpdateExecutor();

    private MainForecast mainForecast;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViewsByID();

        initRecyclerView();

        setButtonsClickListeners();

        setActualDates();
    }

    private void initViewsByID() {
        city = findViewById(R.id.location);
        temperature = findViewById(R.id.temperature);
        pressure = findViewById(R.id.pressure_text);
        windSpeed = findViewById(R.id.wind_text);
        windIcon = findViewById(R.id.wind_icon);
        pressureIcon = findViewById(R.id.pressure_icon);
        mainWeatherIcon = (ImageView) findViewById(R.id.main_weather_icon);
        currentWeather = findViewById(R.id.current_weather);
        settingsButton = findViewById(R.id.settings);
        currentDate = findViewById(R.id.current_date);
    }

    private void initRecyclerView() {
        final RecyclerView recyclerView = findViewById(R.id.recycler_view_forecast);
        recyclerView.setLayoutManager(new LinearLayoutManager(
                this, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.setDays(Arrays.asList(days));
        recyclerViewAdapter.setOnDayClickListener(new ForecastRecyclerViewAdapter.DaysViewHolder.OnDayClickListener() {
            @Override
            public void onClicked(String day) {
                onDayClicked(day);
            }
        });

        DividerItemDecoration itemDecoration = new DividerItemDecoration(this,  LinearLayoutManager.VERTICAL);
        itemDecoration.setDrawable(getDrawable(R.drawable.decorator));
        recyclerView.addItemDecoration(itemDecoration);
    }

    private void onDayClicked(String day) {
        Intent intent = new Intent(this, ExtendedActivity.class);
        intent.putExtra("Date", day);
        startActivity(intent);
    }

    private void setButtonsClickListeners() {

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSettingsClicked();
            }
        });

    }

    private void onSettingsClicked() {

        Parcel parcel = new Parcel();

        parcel.isPressureOn = pressure.getVisibility() == View.VISIBLE;
        parcel.isWindOn = windSpeed.getVisibility() == View.VISIBLE;
        parcel.location = (String) city.getText();

        Intent intent = new Intent(this, SettingsActivity.class);
        intent.putExtra(SETTINGS, parcel);
        startActivityForResult(intent, REQUEST_CODE);

    }

    private void setActualDates() {
        long oneDay = 86400000;

        currentDate.setText(getDate(date));

        for (int i = 0; i <days.length ; i++) {
            date.setTime(date.getTime() + oneDay);
            days[i] = getDate(date);
        }
    }

    private String getDate(Date d) {
        StringBuilder sb = new StringBuilder();
        String[] date = d.toString().split(" ", 4);
        for (int i = 0; i < 3; i++) {
            sb.append(date[i]).append(" ");
        }
        return sb.toString();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode != REQUEST_CODE) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
        if (resultCode == OK) {
            Parcel parcel = null;
            if (data != null) {
                parcel = (Parcel) Objects.requireNonNull(data.getExtras()).getSerializable(SETTINGS);
            }
            if (parcel == null){
                return;
            }
            updateSettings(parcel);
        }
    }


    private void updateSettings(Parcel parcel) {
        if (!parcel.isWindOn) {
            windIcon.setVisibility(View.GONE);
            windSpeed.setVisibility(View.GONE);
        } else {
            windIcon.setVisibility(View.VISIBLE);
            windSpeed.setVisibility(View.VISIBLE);
        }

        if (!parcel.isPressureOn) {
            pressureIcon.setVisibility(View.GONE);
            pressure.setVisibility(View.GONE);
        } else {
            pressureIcon.setVisibility(View.VISIBLE);
            pressure.setVisibility(View.VISIBLE);
        }
        city.setText(parcel.location);
    }


    @Override
    protected void onStart() {
        super.onStart();

        updateForecast();
        displayWeather(mainForecast);
    }

    private void updateForecast(){
        mainForecast = forecastUpdater.getForecast(city.getText().toString());
        if (mainForecast == null){
            showFailToUpdateSnackBar("Fail to update data");
        }
    }

    @SuppressLint("DefaultLocale")
    private void displayWeather(MainForecast mainForecast){
        temperature.setText(String.format("%.0f°С", mainForecast.getMain().getTemp() - 273));
        pressure.setText(String.format("%d hPa", mainForecast.getMain().getPressure()));
        windSpeed.setText(String.format("%.1f mph", mainForecast.getWind().getSpeed()));
        currentWeather.setText(mainForecast.getWeather()[0].getMain());
//        mainWeatherIcon.setImageResource(R.drawable.settings);
    }

    private void showFailToUpdateSnackBar(String msg){
        View view = findViewById(R.id.main_constraint);
        Snackbar.make(view, msg, Snackbar.LENGTH_INDEFINITE)
                .setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateForecast();
                    }
                }).show();

    }
}