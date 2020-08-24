package com.joesemper.justweather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity {

    private static final String WIND = "Wind";
    private static final String PRESSURE = "Pressure";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ImageButton back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch wind = findViewById(R.id.wind_check);
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch pressure = findViewById(R.id.pressure_check);
        outState.putBoolean(WIND, wind.isChecked());
        outState.putBoolean(PRESSURE, pressure.isChecked());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch wind = findViewById(R.id.wind_check);
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch pressure = findViewById(R.id.pressure_check);
        wind.setChecked(savedInstanceState.getBoolean(WIND, false));
        pressure.setChecked(savedInstanceState.getBoolean(PRESSURE, false));
    }
}