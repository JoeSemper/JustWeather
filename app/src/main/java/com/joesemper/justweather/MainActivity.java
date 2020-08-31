package com.joesemper.justweather;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements Constants {

    private Date date = new Date();
    final ForecastAdapter adapter = new ForecastAdapter();
    private String[] days = new String[7];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final RecyclerView recyclerView = findViewById(R.id.recycler_view_forecast);
        recyclerView.setLayoutManager(new LinearLayoutManager(
                this, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        adapter.setDays(Arrays.asList(days));

        setDate();



        TextView temperature = findViewById(R.id.temperature);
        ImageButton settingsButton = findViewById(R.id.settings);


        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSettingsClicked();
            }
        });

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

    private String getDate() {
        StringBuilder sb = new StringBuilder();
        String[] date = this.date.toString().split(" ", 4);
        for (int i = 0; i < 3; i++) {
            sb.append(date[i] + " ");
        }
        return sb.toString();
    }

    private String getDate(Date d) {
        StringBuilder sb = new StringBuilder();
        String[] date = d.toString().split(" ", 4);
        for (int i = 0; i < 3; i++) {
            sb.append(date[i] + " ");
        }
        return sb.toString();
    }

    private void setDate() {
        long oneDay = 86400000;

        TextView currentDate = findViewById(R.id.current_date);
        currentDate.setText(getDate());

        for (int i = 0; i <7 ; i++) {
            date.setTime(date.getTime() + oneDay);
            days[i] = getDate(date);
        }
    }

}