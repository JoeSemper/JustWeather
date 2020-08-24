package com.joesemper.justweather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity implements Constants{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Parcel parcel = (Parcel) Objects.requireNonNull(getIntent().getExtras()).getSerializable(SETTINGS);

       @SuppressLint("UseSwitchCompatOrMaterialCode") Switch wind = findViewById(R.id.wind_check);
       @SuppressLint("UseSwitchCompatOrMaterialCode") Switch pressure = findViewById(R.id.pressure_check);
        EditText location = findViewById(R.id.edit_location);


        if (parcel != null) {
            wind.setChecked(parcel.isWindOn);
            pressure.setChecked(parcel.isPressureOn);
            location.setText(parcel.location);
        }



        ImageButton back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Parcel parcel = new Parcel();

                @SuppressLint("UseSwitchCompatOrMaterialCode") Switch wind = findViewById(R.id.wind_check);
                @SuppressLint("UseSwitchCompatOrMaterialCode") Switch pressure = findViewById(R.id.pressure_check);
                EditText location = findViewById(R.id.edit_location);

                parcel.isPressureOn = pressure.isChecked();
                parcel.isWindOn = wind.isChecked();
                parcel.location = String.valueOf(location.getText());

                Intent intentResult = new Intent();
                intentResult.putExtra(SETTINGS, parcel);
                setResult(OK, intentResult);
                finish();
            }
        });

    }

}