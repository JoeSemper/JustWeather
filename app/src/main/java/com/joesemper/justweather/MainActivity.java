package com.joesemper.justweather;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements Constants {

    private Date date = new Date();
    MainFragment mainFragment;

    static Bundle bundle = new Bundle();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainFragment = MainFragment.newInstance("Kaluga", true, false);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.linear_parrent, mainFragment);
        fragmentTransaction.commit();


        ImageButton settingsButton = findViewById(R.id.settings_button);
        ImageButton mainButton = findViewById(R.id.main_button);

        settingsButton.setOnClickListener(new SettingsListener());
        mainButton.setOnClickListener(new MainListener());

    }

    class SettingsListener implements View.OnClickListener, Constants {

        Fragment fragment;

        ImageButton settingsButton = findViewById(R.id.settings_button);
        ImageButton mainButton = findViewById(R.id.main_button);

        SettingsListener() {
            this.fragment = new SettingsFragment();
        }

        @Override
        public void onClick(View v) {

            settingsButton.setVisibility(View.GONE);
            mainButton.setVisibility(View.VISIBLE);

            replaceFragment();
        }

        // Заменить фрагмент
        private void replaceFragment() {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.addToBackStack("");
            fragmentTransaction.replace(R.id.linear_parrent, fragment);
            fragmentTransaction.commit();
        }
    }

    class MainListener implements View.OnClickListener {

        Fragment fragment;

        ImageButton settingsButton = findViewById(R.id.settings_button);
        ImageButton mainButton = findViewById(R.id.main_button);

        MainListener() {
            this.fragment = new MainFragment();
        }

        @Override
        public void onClick(View v) {

            mainButton.setVisibility(View.GONE);
            settingsButton.setVisibility(View.VISIBLE);

            replaceFragment();
        }

        private void replaceFragment() {
            getSupportFragmentManager().popBackStack();
        }
    }


}