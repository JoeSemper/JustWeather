package com.joesemper.justweather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.joesemper.justweather.forecast.WeatherParser;
import com.joesemper.justweather.database.App;
import com.joesemper.justweather.database.Location;
import com.joesemper.justweather.database.SearchHistoryDao;
import com.joesemper.justweather.interfaces.WeatherUpdater;
import com.joesemper.justweather.recycler.ForecastRecyclerViewAdapter;
import com.joesemper.justweather.retrofit.RetrofitUpdater;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.joesemper.justweather.SettingsActivity.CITY;
import static com.joesemper.justweather.SettingsActivity.LAT;
import static com.joesemper.justweather.SettingsActivity.LON;
import static com.joesemper.justweather.SettingsActivity.SETTINGS;
import static com.joesemper.justweather.retrofit.RetrofitUpdater.UPDATE_FINISHED;
import static com.joesemper.justweather.retrofit.RetrofitUpdater.UPDATE_RESULT;
import static com.joesemper.justweather.retrofit.RetrofitUpdater.UPDATE_RESULT_OK;


public class MainActivity extends AppCompatActivity {

    private UiUpdater uiUpdater;
    private ImageView addToFavorite;
    private Location currentLocation;
    private WeatherUpdater weatherUpdater;
    private BroadcastReceiver broadcastReceiver;
    private SearchHistoryDao searchHistoryDao = App.getInstance().getLocationDao();
    private final ForecastRecyclerViewAdapter recyclerViewAdapter = new ForecastRecyclerViewAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
        initRecyclerView();
        setButtonsClickListeners();
        initToolbar();
        initRetrofit();
        initBroadcastReceiver();
        initGetToken();
        initNotificationChannel();
    }

    @Override
    protected void onStart() {
        super.onStart();

        regReceiver();
        loadPreferences();
        updateWeather();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(broadcastReceiver);
    }

    private void initUI() {
        uiUpdater = new UiUpdater();
        uiUpdater.initUi();

        addToFavorite = findViewById(R.id.add_to_favorite);
    }

    public void initRecyclerView() {
        final RecyclerView recyclerView = findViewById(R.id.recycler_view_forecast);
        recyclerView.setLayoutManager(new LinearLayoutManager(
                this, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.setDays(Arrays.asList(uiUpdater.getActualDates()));
    }

    private void setButtonsClickListeners() {
        addToFavorite.setOnClickListener(new OnAddToFavoriteClickListener());
    }

    private void initBroadcastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getIntExtra(UPDATE_RESULT, -1) == UPDATE_RESULT_OK) {
                    uiUpdater.updateUI();
                }
            }
        };
    }

    private void regReceiver() {
        registerReceiver(broadcastReceiver, new IntentFilter(UPDATE_FINISHED));
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initRetrofit() {
        weatherUpdater = RetrofitUpdater.getInstance();
    }

    private void initGetToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("PushMessage", "getInstanceId failed", task.getException());
                            return;
                        }
                        String token = task.getResult().getToken();
                        Log.d("PushMessage", "Token " + token);
                    }
                });
    }

    private void initNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel = new NotificationChannel("2", "name", importance);
            notificationManager.createNotificationChannel(mChannel);
        }
    }


    private void loadPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(SETTINGS, MODE_PRIVATE);

        currentLocation = new Location(
                sharedPreferences.getString(CITY, "Moscow"),
                sharedPreferences.getFloat(LAT, 55),
                sharedPreferences.getFloat(LON, 37));
    }

    private void savePreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(SETTINGS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CITY, currentLocation.city);
        editor.putFloat(LAT, currentLocation.lat);
        editor.putFloat(LON, currentLocation.lon);
        editor.apply();
    }

    private void updateWeather() {
        weatherUpdater.executeUpdate(this, currentLocation);
    }

    private boolean checkFavorite() {
        SearchHistoryDao searchHistoryDao = App.getInstance().getLocationDao();
        List<Location> list = searchHistoryDao.getAllLocations();
        for (int i = 0; i < searchHistoryDao.getCountLocations(); i++) {
            if (list.get(i).city.equals(currentLocation.city)) {
                return true;
            }
        }
        return false;
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
            case R.id.action_location:
                onAddLocationClicked();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void onSettingsClicked() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void onHistoryClicked() {
        final String[] items;
        List<Location> locations = searchHistoryDao.getAllLocations();
        if (locations.isEmpty()) {
            items = new String[]{getString(R.string.no_favorite)};
        } else {
            items = new String[(int) searchHistoryDao.getCountLocations()];
            for (int i = 0; i < searchHistoryDao.getCountLocations(); i++) {
                items[i] = locations.get(i).city;
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.history)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (items[i].equals(getString(R.string.no_favorite))) {
                            return;
                        }
                        currentLocation = locations.get(i);
                        savePreferences();
                        updateWeather();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void onAddLocationClicked() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    private class OnAddToFavoriteClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            SearchHistoryDao searchHistoryDao = App.getInstance().getLocationDao();
            if (checkFavorite()) {
                addToFavorite.setImageResource(R.drawable.ic_star_border);
                searchHistoryDao.deleteLocation(currentLocation);
            } else {
                addToFavorite.setImageResource(R.drawable.ic_star);
                searchHistoryDao.insertLocation(currentLocation);
            }
        }
    }

    private class UiUpdater {
        private Date date = new Date();
        private String[] days = new String[7];

        private TextView city;
        private TextView temperature;
        private TextView pressure;
        private TextView windSpeed;
        private ImageView mainWeatherIcon;
        private TextView currentWeather;
        private TextView currentDate;
        private TextView currentHumidity;
        private TextView currentCloudiness;
        private TextView feelsLikeValue;

        private ImageView hourlyIcon01;
        private ImageView hourlyIcon02;
        private ImageView hourlyIcon03;
        private ImageView hourlyIcon04;

        private TextView hourlyTemp01;
        private TextView hourlyTemp02;
        private TextView hourlyTemp03;
        private TextView hourlyTemp04;

        private TextView hourlyTime01;
        private TextView hourlyTime02;
        private TextView hourlyTime03;
        private TextView hourlyTime04;

        private void initUi() {
            initViewsByID();
            setActualDates();
        }

        private void updateUI() {
            displayWeather();
            recyclerViewAdapter.notifyDataSetChanged();
        }

        private void initViewsByID() {

            city = findViewById(R.id.location);
            temperature = findViewById(R.id.temperature);
            pressure = findViewById(R.id.pressure_text);
            windSpeed = findViewById(R.id.wind_text);
            mainWeatherIcon = (ImageView) findViewById(R.id.main_weather_icon);
            currentWeather = findViewById(R.id.current_weather);
            currentDate = findViewById(R.id.current_date);
            currentHumidity = findViewById(R.id.current_humidity);
            currentCloudiness = findViewById(R.id.current_cloudniess);
            feelsLikeValue = findViewById(R.id.feels_like_value);

            hourlyIcon01 = findViewById(R.id.weather_icon_00);
            hourlyIcon02 = findViewById(R.id.weather_icon_06);
            hourlyIcon03 = findViewById(R.id.weather_icon_12);
            hourlyIcon04 = findViewById(R.id.weather_icon_18);

            hourlyTemp01 = findViewById(R.id.hourly_temp_00);
            hourlyTemp02 = findViewById(R.id.hourly_temp_06);
            hourlyTemp03 = findViewById(R.id.hourly_temp_12);
            hourlyTemp04 = findViewById(R.id.hourly_temp_18);

            hourlyTime01 = findViewById(R.id.time_text_00);
            hourlyTime02 = findViewById(R.id.time_text_06);
            hourlyTime03 = findViewById(R.id.time_text_12);
            hourlyTime04 = findViewById(R.id.time_text_18);
        }

        private void displayWeather() {
            WeatherParser weatherParser = weatherUpdater.getWeatherParser();
            displayCurrentLocation();
            temperature.setText(weatherParser.getCurrentTemperature());
            pressure.setText(weatherParser.getCurrentPressure());
            windSpeed.setText(weatherParser.getCurrentWindSpeed());
            currentWeather.setText(weatherParser.getCurrentWeather());
            feelsLikeValue.setText(weatherParser.getCurrentFeelsLike());
            currentCloudiness.setText(weatherParser.getCurrentCloudiness());
            currentHumidity.setText(weatherParser.getCurrentHumidity());
            mainWeatherIcon.setImageResource(weatherParser.getMainWeatherIcon());

            hourlyIcon01.setImageResource(weatherParser.getHourlyWeatherIcon(3));
            hourlyIcon02.setImageResource(weatherParser.getHourlyWeatherIcon(6));
            hourlyIcon03.setImageResource(weatherParser.getHourlyWeatherIcon(12));
            hourlyIcon04.setImageResource(weatherParser.getHourlyWeatherIcon(18));

            hourlyTemp01.setText(weatherParser.getHourlyTemp(3));
            hourlyTemp02.setText(weatherParser.getHourlyTemp(6));
            hourlyTemp03.setText(weatherParser.getHourlyTemp(12));
            hourlyTemp04.setText(weatherParser.getHourlyTemp(18));

            hourlyTime01.setText(weatherParser.getTime(3));
            hourlyTime02.setText(weatherParser.getTime(6));
            hourlyTime03.setText(weatherParser.getTime(12));
            hourlyTime04.setText(weatherParser.getTime(18));
        }

        private void displayCurrentLocation() {
            city.setText(currentLocation.city);
            displayFavorite();
        }

        private void displayFavorite() {
            if (checkFavorite()) {
                addToFavorite.setImageResource(R.drawable.ic_star);
            } else {
                addToFavorite.setImageResource(R.drawable.ic_star_border);
            }
        }

        private void setActualDates() {
            long oneDay = 86400000;
            currentDate.setText(getDate(date));
            for (int i = 0; i < days.length; i++) {
                days[i] = getDate(date);
                date.setTime(date.getTime() + oneDay);
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

        public String[] getActualDates() {
            return days;
        }
    }
}