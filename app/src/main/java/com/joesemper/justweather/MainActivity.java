package com.joesemper.justweather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.joesemper.justweather.forecast.WeatherParser;
import com.joesemper.justweather.interfaces.Constants;
import com.joesemper.justweather.interfaces.WeatherRequest;
import com.joesemper.justweather.database.App;
import com.joesemper.justweather.database.Location;
import com.joesemper.justweather.database.SearchHistoryDao;
import com.joesemper.justweather.forecast.openweather.OpenWeather;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
    private ImageView addToFavorite;

//    private String tempUnits;
//    private String pressureUnits;
//    private String windUnits;

    private final ForecastRecyclerViewAdapter recyclerViewAdapter = new ForecastRecyclerViewAdapter();

//    private MainForecast mainForecast;
    private OpenWeather openWeather;
    private WeatherRequest weatherRequest;
    private static final String ID = BuildConfig.WEATHER_API_KEY;

    private Location currentLocation;

    private float lat;
    private float lon;


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
//        tempUnits = sharedPreferences.getString("Temp", "°C");
//        pressureUnits = sharedPreferences.getString("Pres", "mm Hg");
//        windUnits  = sharedPreferences.getString("Wind", "m/s");
//        city.setText(sharedPreferences.getString("City", "Moscow"));
//        lat = sharedPreferences.getFloat("lat", -34);
//        lon = sharedPreferences.getFloat("lng", 151);

        currentLocation = new Location(
                sharedPreferences.getString("City", "Moscow"),
                sharedPreferences.getFloat("lat", -34),
                sharedPreferences.getFloat("lng", 151));

        city.setText(currentLocation.city);
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
        addToFavorite = findViewById(R.id.add_to_favarite);
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
//            Location location = new Location();
//            location.city = city.getText().toString();
            searchHistoryDao.insertLocation(currentLocation);
        });

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

        requestRetrofit(lat, lon, "hourly", ID);

        checkFavorite();
    }

    private void checkFavorite() {
        SearchHistoryDao searchHistoryDao = App.getInstance().getLocationDao();
        List<Location> list = searchHistoryDao.getAllLocations();
        for (int i = 0; i < searchHistoryDao.getCountLocations(); i++) {
            if (list.get(i).city.equals(city.getText().toString())) {
                addToFavorite.setImageResource(R.drawable.ic_star);
            } else {
                addToFavorite.setImageResource(R.drawable.ic_star_border);
            }
        }
    }

//    private void requestRetrofit(final String city, String keyApi) {
//        weatherRequest.loadWeather(city, keyApi)
//                .enqueue(new Callback<MainForecast>() {
//                    @Override
//                    public void onResponse(Call<MainForecast> call, Response<MainForecast> response) {
//                        if (response.body() != null) {
//                            mainForecast = response.body();
//                            displayWeather(mainForecast);
////                            Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
//
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<MainForecast> call, Throwable t) {
////                        Toast.makeText(MainActivity.this, "Fail", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }

    private void requestRetrofit(float lat, float lon, String exclude, String keyApi) {
        weatherRequest.loadWeather(lat, lon, exclude, keyApi)
                .enqueue(new Callback<OpenWeather>() {
                    @Override
                    public void onResponse(Call<OpenWeather> call, Response<OpenWeather> response) {
                        if (response.body() != null) {
                            openWeather = response.body();
                            WeatherParser weatherParser = new WeatherParser(MainActivity.this, openWeather);
                            displayWeather(weatherParser);
                            Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onFailure(Call<OpenWeather> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    private void updateForecast(){
        requestRetrofit(lat, lon, "hourly", ID);
        if (openWeather == null){
            showFailToUpdateSnackBar("Fail to update data");
        }
    }

//    @SuppressLint("DefaultLocale")
//    private void displayWeather(MainForecast mainForecast){
//        temperature.setText(String.format("%.0f%s", mainForecast.getMain().getTemp() - 273, tempUnits));
//        pressure.setText(String.format("%d %s", mainForecast.getMain().getPressure(), pressureUnits));
//        windSpeed.setText(String.format("%.1f %s", mainForecast.getWind().getSpeed(), windUnits));
//        currentWeather.setText(mainForecast.getWeather()[0].getMain());
//        feelsLikeValue.setText(String.format("%.0f %s", mainForecast.getMain().getFeels_like() - 273, tempUnits));
//        sunriseSunset.setText(String.format("%s/%s",
//                getHoursAndMinutes(getDateByMs(mainForecast.getSys().getSunrise())),
//                getHoursAndMinutes(getDateByMs(mainForecast.getSys().getSunset()))));
//        maxMinTemperature.setText(String.format("%.0f%s/%.0f%s",
//                mainForecast.getMain().getTemp_min() - 273,
//                tempUnits,
//                mainForecast.getMain().getTemp_max() - 273,
//                tempUnits));
//
//
//        setMainWeatherIcon(mainForecast);
//
//    }

    private void displayWeather(WeatherParser weatherParser){
        temperature.setText(weatherParser.getCurrentTemperature());
        pressure.setText(weatherParser.getCurrentPressure());
        windSpeed.setText(weatherParser.getCurrentWindSpeed());
        currentWeather.setText(weatherParser.getCurrentWeather());
        feelsLikeValue.setText(weatherParser.getCurrentFeelsLike());
        sunriseSunset.setText(weatherParser.getSunriseSunset());
        maxMinTemperature.setText(weatherParser.getCurrentMinMaxTemp());


//        setMainWeatherIcon(mainForecast);

    }

    private void  setMainWeatherIcon(MainForecast mainForecast){
        switch (mainForecast.getWeather()[0].getIcon()) {
            case "01d":
                mainWeatherIcon.setImageResource(R.drawable.sunny);
                return;
            case "02d":
                mainWeatherIcon.setImageResource(R.drawable.partly_cloudy);
                return;
            case "03d":
                mainWeatherIcon.setImageResource(R.drawable.clouds);
                return;
            case "04d":
                mainWeatherIcon.setImageResource(R.drawable.clouds);
                return;
            case "09d":
                mainWeatherIcon.setImageResource(R.drawable.heavy_rain);
                return;
            case "10d":
                mainWeatherIcon.setImageResource(R.drawable.rain);
                return;
            case "11d":
                mainWeatherIcon.setImageResource(R.drawable.storm);
                return;
            case "13d":
                mainWeatherIcon.setImageResource(R.drawable.snow);
                return;
            default:
                mainWeatherIcon.setImageResource(R.drawable.partly_cloudy);
        }
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
                items[i] = locations.get(i).city;
            }
            checkFavorite();
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
//                        displayWeather(mainForecast);
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void onAddLocationClicked(){
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }


    public TextView getTemperature() {
        return temperature;
    }

    public TextView getPressure() {
        return pressure;
    }

    public TextView getWindSpeed() {
        return windSpeed;
    }

    public TextView getCurrentWeather() {
        return currentWeather;
    }

    public TextView getMaxMinTemperature() {
        return maxMinTemperature;
    }

    public TextView getSunriseSunset() {
        return sunriseSunset;
    }
}