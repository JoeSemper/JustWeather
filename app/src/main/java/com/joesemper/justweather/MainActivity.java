package com.joesemper.justweather;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements Constants {

    private Date date = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView temperature = findViewById(R.id.temperature);
        ImageButton settingsButton = findViewById(R.id.settings);

        setDate();

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSettingsClicked();
            }
        });

        String instanceState;

        if (savedInstanceState == null) {
            instanceState = "Первый запуск!";
        } else {
            instanceState = "Повторный запуск!";
        }
        Toast.makeText(getApplicationContext(), instanceState + " - onCreate()", Toast.LENGTH_SHORT).show();
        Log.d("test", "onCreate()");
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


//        startActivity(new Intent(this, SettingsActivity.class));
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

        TextView day2 = findViewById(R.id.day_2_date);
        TextView day3 = findViewById(R.id.day_3_date);
        TextView day4 = findViewById(R.id.day_4_date);
        TextView day5 = findViewById(R.id.day_5_date);

        date.setTime(date.getTime() + oneDay);
        day2.setText(getDate(date));
        date.setTime(date.getTime() + oneDay);
        day3.setText(getDate(date));
        date.setTime(date.getTime() + oneDay);
        day4.setText(getDate(date));
        date.setTime(date.getTime() + oneDay);
        day5.setText(getDate(date));
    }

}