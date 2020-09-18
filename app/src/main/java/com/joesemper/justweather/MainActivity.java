package com.joesemper.justweather;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.joesemper.justweather.forecast.MainForecast;
import com.joesemper.justweather.interfaces.Constants;
import com.joesemper.justweather.interfaces.ForecastUpdater;

import java.util.Arrays;
import java.util.Date;
import java.util.Objects;


public class MainActivity extends AppCompatActivity implements Constants,
        NavigationView.OnNavigationItemSelectedListener {

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

    private Settings settings;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViewsByID();

        initRecyclerView();

        setButtonsClickListeners();

        setActualDates();

        Toolbar toolbar = initToolbar();

        initDrawer(toolbar);

        settings = Settings.getInstance();
    }

    private Toolbar initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        return toolbar;
    }

    private void initDrawer(Toolbar toolbar) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
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

        city.setText(Settings.getCurrentLocation());

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
        // Обработка выбора пункта меню приложения (активити)
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                onSettingsClicked();
                return true;
            case R.id.action_add_location:
                Intent intent = new Intent(this, HistorySearchActivity.class);
                startActivity(intent);
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.nav_settings:
                onSettingsClicked();
                return true;
            case R.id.nav_cities_list:
                Intent intent = new Intent(this, HistorySearchActivity.class);
                startActivity(intent);
                return true;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }
}