package com.joesemper.justweather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
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

    private Date date = new Date();
    private String[] days = new String[7];

    private TextView city;
    private TextView temperature;
    private TextView pressure;
    private TextView windSpeed;
    private ImageView mainWeatherIcon;
    private TextView currentWeather;
    private TextView currentDate;
    private TextView maxMinTemperature;
    private TextView sunriseSunset;
    private TextView feelsLikeValue;
    private ImageView addToFavorite;


    private final ForecastRecyclerViewAdapter recyclerViewAdapter = new ForecastRecyclerViewAdapter();

    private RetrofitUpdater retrofitUpdater;

    private Location currentLocation;

    private SearchHistoryDao searchHistoryDao = App.getInstance().getLocationDao();

    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViewsByID();

        initRecyclerView();

        setButtonsClickListeners();

        initToolbar();

        setActualDates();

        initRetrofit();

        initBroadcastReceiver();

        initGetToken();

        initNotificationChannel();
    }

    @Override
    protected void onStart() {
        super.onStart();

        initReceiver();

        loadPreferences();

        updateWeather();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(broadcastReceiver);
    }


    private void initViewsByID() {
        city = findViewById(R.id.location);
        temperature = findViewById(R.id.temperature);
        pressure = findViewById(R.id.pressure_text);
        windSpeed = findViewById(R.id.wind_text);
        mainWeatherIcon = (ImageView) findViewById(R.id.main_weather_icon);
        currentWeather = findViewById(R.id.current_weather);
        currentDate = findViewById(R.id.current_date);
        maxMinTemperature = findViewById(R.id.max_min_temperature);
        sunriseSunset = findViewById(R.id.sunrise_sunset);
        feelsLikeValue = findViewById(R.id.feels_like_value);
        addToFavorite = findViewById(R.id.add_to_favarite);
    }

    public void initRecyclerView() {
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

    private void setButtonsClickListeners() {

        addToFavorite.setOnClickListener(view -> {
            SearchHistoryDao searchHistoryDao = App.getInstance().getLocationDao();
            List<Location> list = searchHistoryDao.getAllLocations();
            for (int i = 0; i <searchHistoryDao.getCountLocations() ; i++) {
                if(list.get(i).city.equals(city.getText().toString())){
                    addToFavorite.setImageResource(R.drawable.ic_star_border);
                    searchHistoryDao.deleteLocation(list.get(i));
                    return;
                }
            }
            addToFavorite.setImageResource(R.drawable.ic_star);
            searchHistoryDao.insertLocation(currentLocation);
        });

    }

    private void initReceiver(){
        registerReceiver(broadcastReceiver, new IntentFilter(UPDATE_FINISHED));
    }

    private void initBroadcastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getIntExtra(UPDATE_RESULT, -1) == UPDATE_RESULT_OK){
                    displayWeather(RetrofitUpdater.getWeatherParser());
                    recyclerViewAdapter.notifyDataSetChanged();
                }
            }
        };

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

    private void initRetrofit() {
        retrofitUpdater = new RetrofitUpdater(this);
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

    private void initNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager =
                    (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel = new NotificationChannel("2", "name", importance);
            notificationManager.createNotificationChannel(mChannel);
        }
    }


    private void loadPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(SETTINGS, MODE_PRIVATE);

        currentLocation = new Location(
                sharedPreferences.getString(CITY, "Moscow"),
                sharedPreferences.getFloat(LAT, -34),
                sharedPreferences.getFloat(LON, 151));
    }


    private void onDayClicked(String day) {
        Intent intent = new Intent(this, ExtendedActivity.class);
        intent.putExtra("Date", day);
        startActivity(intent);
    }


//    private String getHoursAndMinutes(Date d) {
//        StringBuilder sb = new StringBuilder();
//        String[] date = d.toString().split(" ", 4);
//        String[] hours = date[3].split(":", 3);
//        sb.append(hours[0] + ":" + hours[1]);
//        return sb.toString();
//    }

    private void updateWeather(){
        retrofitUpdater.executeUpdate(currentLocation);
    }

    private void displayWeather(WeatherParser weatherParser){
        displayCurrentLocation();
        temperature.setText(weatherParser.getCurrentTemperature());
        pressure.setText(weatherParser.getCurrentPressure());
        windSpeed.setText(weatherParser.getCurrentWindSpeed());
        currentWeather.setText(weatherParser.getCurrentWeather());
        feelsLikeValue.setText(weatherParser.getCurrentFeelsLike());
        sunriseSunset.setText(weatherParser.getSunriseSunset());
        maxMinTemperature.setText(weatherParser.getCurrentMinMaxTemp());
        mainWeatherIcon.setImageResource(weatherParser.getMainWeatherIcon());
    }

    private void displayCurrentLocation(){
        city.setText(currentLocation.city);
        checkFavorite();
    }

    private void checkFavorite() {
        SearchHistoryDao searchHistoryDao = App.getInstance().getLocationDao();
        List<Location> list = searchHistoryDao.getAllLocations();
        for (int i = 0; i < searchHistoryDao.getCountLocations(); i++) {
            if (list.get(i).city.equals(currentLocation.city)) {
                addToFavorite.setImageResource(R.drawable.ic_star);
                return;
            } else {
                addToFavorite.setImageResource(R.drawable.ic_star_border);
            }
        }
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
            items = new String[]{"No favorite locations"};
        } else {
            items = new String[(int) searchHistoryDao.getCountLocations()];
            for (int i = 0; i <searchHistoryDao.getCountLocations(); i++) {
                items[i] = locations.get(i).city;
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.history)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (items[i].equals("No favorite locations")){
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

    private void onAddLocationClicked(){
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    private void savePreferences(){
        SharedPreferences sharedPreferences = getSharedPreferences(SETTINGS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CITY, currentLocation.city);
        editor.putFloat(LAT, currentLocation.lat);
        editor.putFloat(LON, currentLocation.lon);
        editor.apply();
    }
}