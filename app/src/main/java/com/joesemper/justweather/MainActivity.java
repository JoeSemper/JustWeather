package com.joesemper.justweather;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;
import com.joesemper.justweather.forecast.MainForecast;
import com.joesemper.justweather.interfaces.Constants;
import com.joesemper.justweather.interfaces.ForecastUpdater;
import com.joesemper.justweather.maintenance.Settings;
import com.joesemper.justweather.services.ForecastIntentService;

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
    private TextView currentDate;
    private TextView maxMinTemperature;
    private TextView sunriseSunset;
    private TextView feelsLikeValue;

    private final ForecastRecyclerViewAdapter recyclerViewAdapter = new ForecastRecyclerViewAdapter();

    private static ForecastUpdater forecastUpdater = new ForecastUpdateExecutor();

    private MainForecast mainForecast;

    private Settings settings = Settings.getInstance();

    public static final String BROADCAST_ACTION_UPDATEFINISHED = "com.joesemper.justweather.UPDATEFINISHED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViewsByID();

        initRecyclerView();

        setButtonsClickListeners();

        setActualDates();

        initToolbar();

        initNotificationChannel();
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
        currentDate = findViewById(R.id.current_date);
        maxMinTemperature = findViewById(R.id.max_min_temperature);
        sunriseSunset = findViewById(R.id.sunrise_sunset);
        feelsLikeValue = findViewById(R.id.feels_like_value);


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

    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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

    private String getHoursAndMinutes(Date d) {
        StringBuilder sb = new StringBuilder();
        String[] date = d.toString().split(" ", 4);
        String[] hours = date[3].split(":", 3);
        sb.append(hours[0] + ":" + hours[1]);
        return sb.toString();
    }

    private void initNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager =
                    (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel = new NotificationChannel("2", "name", importance);
            notificationManager.createNotificationChannel(mChannel);
        }
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

        registerReceiver(updateFinishedReceiver, new IntentFilter(BROADCAST_ACTION_UPDATEFINISHED));
        city.setText(settings.getCurrentLocation());
        ForecastIntentService.startActionUpdate(MainActivity.this, settings.getCurrentLocation());
//        updateForecast();
//        displayWeather(mainForecast);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(updateFinishedReceiver);
    }

    private void updateForecast(){
        mainForecast = forecastUpdater.getForecast(settings.getCurrentLocation());
        if (mainForecast == null){
            showFailToUpdateSnackBar("Fail to update data");
        }
    }


    private BroadcastReceiver updateFinishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final MainForecast result = (MainForecast) intent.getSerializableExtra(ForecastIntentService.EXTRA_RESULT);
            mainForecast = result;
            displayWeather(result);
        }
    };
    @SuppressLint("DefaultLocale")
    private void displayWeather(MainForecast mainForecast){
        temperature.setText(String.format("%.0f°С", mainForecast.getMain().getTemp() - 273));
        pressure.setText(String.format("%d hPa", mainForecast.getMain().getPressure()));
        windSpeed.setText(String.format("%.1f m/ph", mainForecast.getWind().getSpeed()));
        currentWeather.setText(mainForecast.getWeather()[0].getMain());
        feelsLikeValue.setText(String.format("%.0f°С", mainForecast.getMain().getFeels_like() - 273));
        sunriseSunset.setText(String.format("%s/%s",
                getHoursAndMinutes(getDateByMs(mainForecast.getSys().getSunrise())),
                getHoursAndMinutes(getDateByMs(mainForecast.getSys().getSunset()))));
        maxMinTemperature.setText(String.format("%.0f°С/%.0f°С",
                mainForecast.getMain().getTemp_min() - 273,
                mainForecast.getMain().getTemp_max() - 273));

//        mainWeatherIcon.setImageResource(R.drawable.settings);
    }

    private Date getDateByMs(long ms){
        date.setTime(ms);
        return date;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                onSettingsClicked();
                return true;
            case R.id.action_history:
                onHistoryClicked();
                return true;
        }

        return super.onOptionsItemSelected(item);
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

    private void onHistoryClicked(){
        String[] items = new String[settings.getLocationsHistory().size()];
        settings.getLocationsHistory().toArray(items);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.history)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        settings.setCurrentLocation(settings.getLocationsHistory().get(i));
                        city.setText(settings.getCurrentLocation());
                        updateForecast();
                        displayWeather(mainForecast);
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}