package com.joesemper.justweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

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


    }

    private void onSettingsClicked() {
        startActivity(new Intent(this, Settings.class));
    }

    private String getDate(){
        StringBuilder sb = new StringBuilder();
        String[] date = this.date.toString().split(" ", 4);
        for (int i = 0; i <3; i++) {
            sb.append(date[i]+" ");
        }
        return sb.toString();
    }
    private String getDate(Date d){
        StringBuilder sb = new StringBuilder();
        String[] date = d.toString().split(" ", 4);
        for (int i = 0; i <3; i++) {
            sb.append(date[i]+" ");
        }
        return sb.toString();
    }

    private void setDate(){
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