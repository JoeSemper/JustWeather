package com.joesemper.justweather;

import androidx.annotation.NonNull;
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
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.joesemper.justweather.forecast.MainForecast;
import com.joesemper.justweather.interfaces.Constants;
import com.joesemper.justweather.interfaces.ForecastUpdater;
import com.joesemper.justweather.interfaces.WeatherRequest;
import com.joesemper.justweather.maintenance.App;
import com.joesemper.justweather.maintenance.Location;
import com.joesemper.justweather.maintenance.SearchHistoryDao;
import com.joesemper.justweather.maintenance.Settings;
import com.joesemper.justweather.openweather.OpenWeather;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


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

    private String tempUnits;
    private String pressureUnits;
    private String windUnits;

    private final ForecastRecyclerViewAdapter recyclerViewAdapter = new ForecastRecyclerViewAdapter();

    private MainForecast mainForecast;
    private OpenWeather openWeather;
    private WeatherRequest weatherRequest;
    private static final String ID = BuildConfig.WEATHER_API_KEY;

    private Settings settings = Settings.getInstance();

    private SearchHistoryDao searchHistoryDao = App.getInstance().getLocationDao();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViewsByID();

        initRecyclerView();

        setButtonsClickListeners();

        setActualDates();

        initToolbar();

        initGetToken();

        initNotificationChannel();

        initRetrofit();

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


    private void loadPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        tempUnits = sharedPreferences.getString("Temp", "°C");
        pressureUnits = sharedPreferences.getString("Pres", "mm Hg");
        windUnits  = sharedPreferences.getString("Wind", "m/s");
        city.setText(sharedPreferences.getString("City", "Moscow"));
    }

    private void initRetrofit() {
        Retrofit retrofit;
        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create()).build();
        weatherRequest = retrofit.create(WeatherRequest.class);
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
    protected void onStart() {
        super.onStart();

        loadPreferences();

        requestRetrofit(city.getText().toString(), ID);
    }

    private void requestRetrofit(final String city, String keyApi) {
        weatherRequest.loadWeather(city, keyApi)
                .enqueue(new Callback<MainForecast>() {
                    @Override
                    public void onResponse(Call<MainForecast> call, Response<MainForecast> response) {
                        if (response.body() != null) {
                            mainForecast = response.body();
                            displayWeather(mainForecast);
                            Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onFailure(Call<MainForecast> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    private void updateForecast(){
        requestRetrofit(city.getText().toString(), ID);
        if (mainForecast == null){
            showFailToUpdateSnackBar("Fail to update data");
        }
    }

    @SuppressLint("DefaultLocale")
    private void displayWeather(MainForecast mainForecast){
        temperature.setText(String.format("%.0f%s", mainForecast.getMain().getTemp() - 273, tempUnits));
        pressure.setText(String.format("%d %s", mainForecast.getMain().getPressure(), pressureUnits));
        windSpeed.setText(String.format("%.1f %s", mainForecast.getWind().getSpeed(), windUnits));
        currentWeather.setText(mainForecast.getWeather()[0].getMain());
        feelsLikeValue.setText(String.format("%.0f %s", mainForecast.getMain().getFeels_like() - 273, tempUnits));
        sunriseSunset.setText(String.format("%s/%s",
                getHoursAndMinutes(getDateByMs(mainForecast.getSys().getSunrise())),
                getHoursAndMinutes(getDateByMs(mainForecast.getSys().getSunset()))));
        maxMinTemperature.setText(String.format("%.0f%s/%.0f%s",
                mainForecast.getMain().getTemp_min() - 273,
                tempUnits,
                mainForecast.getMain().getTemp_max() - 273,
                tempUnits));

//        mainWeatherIcon.setImageResource();
//
//        setMainWeatherIcon(mainForecast);

    }

//    private void  setMainWeatherIcon(MainForecast mainForecast){
//        switch  {
//            case "10n"||"10d" :
//
//        }
//
//        mainWeatherIcon.setImageResource();
//    }

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

    private void onHistoryClicked(){
        final String[] items;
        List<Location>  locations = searchHistoryDao.getAllLocations();
        if (locations.isEmpty()) {
            items = new String[]{city.getText().toString()};
        } else {
            items = new String[(int) searchHistoryDao.getCountLocations()];
            for (int i = 0; i <searchHistoryDao.getCountLocations() ; i++) {
                items[i] = locations.get(i).location;
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.history)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("City", items[i]);
                        editor.apply();
                        city.setText(items[i]);
                        updateForecast();
                        displayWeather(mainForecast);
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void onAddLocationClicked(){
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}