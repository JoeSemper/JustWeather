package com.joesemper.justweather;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.joesemper.justweather.forecast.MainForecast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity implements Constants {

    private Date date = new Date();
    final ForecastAdapter adapter = new ForecastAdapter();
    private String[] days = new String[31];

    private TextView city;
    private TextView temperature;
    private TextView pressure;
    private TextView windSpeed;

    private static final String TAG = "WEATHER";
    private static final String WEATHER_URL =
            "https://api.openweathermap.org/data/2.5/weather?q=Moscow,ru&appid=33da5092c9df576f30d4bfe2788922a4";

    private static final String ID = "33da5092c9df576f30d4bfe2788922a4";

//    public TextView getCity() {
//        return city;
//    }
//
//    public TextView getTemperature() {
//        return temperature;
//    }
//
//    public TextView getPressure() {
//        return pressure;
//    }
//
//    public TextView getWindSpeed() {
//        return windSpeed;
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        city = findViewById(R.id.location);
        temperature = findViewById(R.id.temperature);
        pressure = findViewById(R.id.pressure_text);
        windSpeed = findViewById(R.id.wind_text);



        setDate();

        final RecyclerView recyclerView = findViewById(R.id.recycler_view_forecast);
        recyclerView.setLayoutManager(new LinearLayoutManager(
                this, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        adapter.setDays(Arrays.asList(days));
        adapter.setOnDayClickListener(new ForecastAdapter.DaysViewHolder.OnDayClickListener() {
            @Override
            public void onClicked(String day) {
//                Toast.makeText(getApplicationContext(), day, Toast.LENGTH_SHORT).show();
                onDayClicked(day);
            }
        });



        DividerItemDecoration itemDecoration = new DividerItemDecoration(this,  LinearLayoutManager.VERTICAL);
        itemDecoration.setDrawable(getDrawable(R.drawable.decorator));
        recyclerView.addItemDecoration(itemDecoration);

        ImageButton settingsButton = findViewById(R.id.settings);


        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSettingsClicked();
            }
        });

//        updateData();


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

            ImageView windIcon = findViewById(R.id.wind_icon);
            TextView wind = findViewById(R.id.wind_text);
            TextView pressure = findViewById(R.id.pressure_text);
            TextView location = findViewById(R.id.location);

            if (parcel == null){
                return;
            }
            if (!parcel.isWindOn) {
                windIcon.setVisibility(View.GONE);
                wind.setVisibility(View.GONE);
            } else {
                windIcon.setVisibility(View.VISIBLE);
                wind.setVisibility(View.VISIBLE);
            }

            if (!parcel.isPressureOn) {
                pressure.setVisibility(View.GONE);
            } else {
                pressure.setVisibility(View.VISIBLE);
            }

            location.setText(parcel.location);

        }
        updateData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateData();
    }

    private void onDayClicked(String day) {
        Intent intent = new Intent(this, ExtendedActivity.class);
        intent.putExtra("Date", day);
        startActivity(intent);
    }

    private void onSettingsClicked() {
        TextView wind = findViewById(R.id.wind_text);
        TextView pressure = findViewById(R.id.pressure_text);
        TextView location = findViewById(R.id.location);
        Parcel parcel = new Parcel();

        parcel.isPressureOn = pressure.getVisibility() == View.VISIBLE;
        parcel.isWindOn = wind.getVisibility() == View.VISIBLE;
        parcel.location = (String) location.getText();

        Intent intent = new Intent(this, SettingsActivity.class);
        intent.putExtra(SETTINGS, parcel);
        startActivityForResult(intent, REQUEST_CODE);

    }

    private void setDate() {
        long oneDay = 86400000;

        TextView currentDate = findViewById(R.id.current_date);
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
            sb.append(date[i] + " ");
        }
        return sb.toString();
    }

    public void updateData() {

        try {
            TextView city = findViewById(R.id.location);
            final URL uri = new URL(String.format(
                    "https://api.openweathermap.org/data/2.5/weather?q=%s,ru&appid=33da5092c9df576f30d4bfe2788922a4", city.getText()));
            final Handler handler = new Handler(); // Запоминаем основной поток
            new Thread(new Runnable() {
                public void run() {
                    HttpsURLConnection urlConnection = null;
                    try {
                        urlConnection = (HttpsURLConnection) uri.openConnection();
                        urlConnection.setRequestMethod("GET"); // установка метода получения данных -GET
                        urlConnection.setReadTimeout(10000); // установка таймаута - 10 000 миллисекунд
                        BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream())); // читаем  данные в поток
                        String result = getLines(in);
                        // преобразование данных запроса в модель
                        Gson gson = new Gson();
                        final MainForecast mainForecast = gson.fromJson(result, MainForecast.class);
                        // Возвращаемся к основному потоку
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                displayWeather(mainForecast);
                            }
                        });
                    } catch (Exception e) {
                        Log.e(TAG, "Fail connection", e);
                        e.printStackTrace();
                    } finally {
                        if (null != urlConnection) {
                            urlConnection.disconnect();
                        }
                    }
                }
            }).start();
        } catch (MalformedURLException e) {
            Log.e(TAG, "Fail URI", e);
            e.printStackTrace();
        }
    }



    private String getLines(BufferedReader in) {
        return in.lines().collect(Collectors.joining("\n"));
    }

    private void displayWeather(MainForecast mainForecast){
        temperature.setText(String.format("%.0f°С", mainForecast.getMain().getTemp() - 273));
        pressure.setText(String.format("%d mm Hg", mainForecast.getMain().getPressure()));
        windSpeed.setText(String.format("%.1f mph", mainForecast.getWind().getSpeed()));
    }
}