package com.joesemper.justweather;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;

public class MainFragment extends Fragment {

    private Date date = new Date();

    private static final String LOCATION = "loc";
    private static final String WIND = "wind";
    private static final String PRESSURE = "pressure";


    private String location;
    private boolean isWindOn;
    private boolean isPressureOn;

    public MainFragment() {
        // Required empty public constructor
    }


    public static MainFragment newInstance(String location, boolean isWindOn,  boolean isPressureOn) {
        MainFragment fragment = new MainFragment();
        Bundle args;
        if (MainActivity.bundle == null){
            args = new Bundle();
        } else {
            args = MainActivity.bundle;
        }

        args.putString(LOCATION, location);
        args.putBoolean(WIND, isWindOn);
        args.putBoolean(PRESSURE, isPressureOn);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getArguments() != null) {
            location = getArguments().getString(LOCATION);
            isWindOn = getArguments().getBoolean(WIND);
            isPressureOn = getArguments().getBoolean(PRESSURE);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        TextView currentDate = view.findViewById(R.id.current_date);
        TextView location = view.findViewById(R.id.main_location);
        ImageView windIcon = view.findViewById(R.id.wind_icon);
        TextView windText = view.findViewById(R.id.current_wind_speed);
        TextView pressureText = view.findViewById(R.id.current_pressure);

        location.setText(this.location);

        if(isWindOn) {
            windIcon.setVisibility(View.VISIBLE);
            windText.setVisibility(View.VISIBLE);
        } else {
            windIcon.setVisibility(View.GONE);
            windText.setVisibility(View.GONE);
        }

        if(isPressureOn) {
            pressureText.setVisibility(View.VISIBLE);
        } else {
            pressureText.setVisibility(View.GONE);
        }

        currentDate.setText(getDate());
        setDates(view);

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(LOCATION, location);
        outState.putBoolean(WIND, isWindOn);
        outState.putBoolean(PRESSURE, isPressureOn);
        MainActivity.bundle = outState;
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

    private void setDates(View view){
        long oneDay = 86400000;

        TextView[] dates = new TextView[7];
        dates[0] = view.findViewById(R.id.date_1);
        dates[1] = view.findViewById(R.id.date_2);
        dates[2] = view.findViewById(R.id.date_3);
        dates[3] = view.findViewById(R.id.date_4);
        dates[4] = view.findViewById(R.id.date_5);
        dates[5] = view.findViewById(R.id.date_6);
        dates[6] = view.findViewById(R.id.date_7);

        for (int i = 0; i <dates.length ; i++) {
            date.setTime(date.getTime() + oneDay);
            dates[i].setText(getDate(date));
        }

    }


}


