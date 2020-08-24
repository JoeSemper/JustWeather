package com.joesemper.justweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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


        ImageView windIcon = findViewById(R.id.wind_icon);
        TextView wind = findViewById(R.id.wind_check);
        TextView pressure = findViewById(R.id.pressure_check);


        String  instanceState;

        if (savedInstanceState == null){
            instanceState = "Первый запуск!";
        }
        else{
            instanceState = "Повторный запуск!";
        }
        Toast.makeText(getApplicationContext(), instanceState + " - onCreate()", Toast.LENGTH_SHORT).show();
        Log.d("test", "onCreate()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Toast.makeText(getApplicationContext(), "onStart()", Toast.LENGTH_SHORT).show();
        Log.d("test", "onStart()");
    }

    @Override
    protected void onRestoreInstanceState(Bundle saveInstanceState){
        super.onRestoreInstanceState(saveInstanceState);
        Toast.makeText(getApplicationContext(), "Повторный запуск!! - onRestoreInstanceState()", Toast.LENGTH_SHORT).show();
        Log.d("test", "Повторный запуск!! - onRestoreInstanceState()");


    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(getApplicationContext(), "onResume()", Toast.LENGTH_SHORT).show();
        Log.d("test", "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Toast.makeText(getApplicationContext(), "onPause()", Toast.LENGTH_SHORT).show();
        Log.d("test", "onPause()");
    }

    @Override
    protected void onSaveInstanceState(Bundle saveInstanceState){
        super.onSaveInstanceState(saveInstanceState);
        Toast.makeText(getApplicationContext(), "onSaveInstanceState()", Toast.LENGTH_SHORT).show();
        Log.d("test", "onSaveInstanceState()");
    }


    @Override
    protected void onStop() {
        super.onStop();
        Toast.makeText(getApplicationContext(), "onStop()", Toast.LENGTH_SHORT).show();
        Log.d("test", "onStop()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Toast.makeText(getApplicationContext(), "onRestart()", Toast.LENGTH_SHORT).show();
        Log.d("test", "onRestart()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(getApplicationContext(), "onDestroy()", Toast.LENGTH_SHORT).show();
        Log.d("test", "onDestroy()");
    }




    private void onSettingsClicked() {
        startActivity(new Intent(this, SettingsActivity.class));
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