package com.joesemper.justweather;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.joesemper.justweather.fragments.BottomDialogFragment;
import com.joesemper.justweather.interfaces.Constants;
import com.joesemper.justweather.interfaces.OnDialogListener;
import com.joesemper.justweather.maintenance.App;
import com.joesemper.justweather.maintenance.Location;
import com.joesemper.justweather.maintenance.SearchHistoryDao;
import com.joesemper.justweather.maintenance.Settings;

import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.regex.Pattern;

public class SettingsActivity extends AppCompatActivity implements Constants {

    private final String ERROR_TEXT = "Wrong location format";

    private TextInputEditText location;
    private String currentLocation;
    private TableRow rowTemperature;
    private TableRow rowPressure;
    private TableRow rowWindSpeed;
    private TextView temperatureUnits;
    private TextView pressureUnits;
    private TextView windUnits;

    private String tempUnitsText;
    private String pressureUnitsText;
    private String windUnitsText;

    private MaterialButton setButton;
    private ImageButton back;
    private MaterialButton apply;

    private Pattern checkLocation = Pattern.compile("^[A-Z][a-z]{2,}$");

    private final Settings settings = Settings.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initViews();

        setOnUnitsClickListeners();

        loadPreferences();

        setOnButtonsClickListeners();

    }

    private void initViews() {
        rowTemperature = findViewById(R.id.temperaure_row);
        rowPressure = findViewById(R.id.pressure_row);
        rowWindSpeed = findViewById(R.id.wind_row);
        temperatureUnits = findViewById(R.id.temp_units);
        pressureUnits = findViewById(R.id.pressure_units);
        windUnits = findViewById(R.id.wind_units);
        location = findViewById(R.id.locatinon_text_input);

        setButton = findViewById(R.id.set_button);
        back = findViewById(R.id.back);
        apply = findViewById(R.id.apply_button);
    }

    private void loadPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        tempUnitsText = sharedPreferences.getString("Temp", "°C");
        pressureUnitsText = sharedPreferences.getString("Pres", "mm Hg");
        windUnitsText  = sharedPreferences.getString("Wind", "m/s");
        currentLocation = sharedPreferences.getString("City", "Moscow");

        temperatureUnits.setText(tempUnitsText);
        pressureUnits.setText(pressureUnitsText);
        windUnits.setText(windUnitsText);
        location.setText(currentLocation);
    }

    private void setOnUnitsClickListeners() {
        rowTemperature.setOnClickListener(new OnUnitsClickListener(OnUnitsClickListener.TEMPERATURE));
        rowPressure.setOnClickListener(new OnUnitsClickListener(OnUnitsClickListener.PRESSURE));
        rowWindSpeed.setOnClickListener(new OnUnitsClickListener(OnUnitsClickListener.WIND));
    }

    private void setOnButtonsClickListeners() {

        location.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus) {
                    return;
                }
                TextView tv = (TextView) view;
                validate(tv);
            }
        });

        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                validate(location);

                if (location.getError() != null){
                    return;
                } else {

                    hideSoftKeyboard();

                    BottomDialogFragment bottomSheetDialogFragment = BottomDialogFragment.newInstance();
                    bottomSheetDialogFragment.setOnDialogListener(dialogListener);
                    bottomSheetDialogFragment.show(getSupportFragmentManager(), "dialog_fragment");

                }
            }
        });

        back.setOnClickListener(new OnBackClickListener());
        apply.setOnClickListener(new OnBackClickListener());
    }

    private class OnUnitsClickListener implements View.OnClickListener {

        public static final String TEMPERATURE = "TEMP";
        public static final String PRESSURE = "PRES";
        public static final String WIND = "WIND";

        private String title;

        private TextView textView;

        String[] items;

        final int[] chosen = {-1};

        OnUnitsClickListener(String unit){
            if(unit.equals(TEMPERATURE)){
                items = getResources().getStringArray(R.array.choose_temp);
                title = "Temperature Units";
                textView = temperatureUnits;
            } else if (unit.equals(PRESSURE)){
                items = getResources().getStringArray(R.array.choose_pressure);
                title = "Pressure Units";
                textView = pressureUnits;
            } else {
                items = getResources().getStringArray(R.array.choose_wind_speed);
                title = "Wind Speed Units";
                textView = windUnits;
            }
        }

        @Override
        public void onClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
            builder.setTitle(title).setSingleChoiceItems(items, chosen[0], new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    chosen[0] = i;
                }
            }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (chosen[0] == -1){
                        return;
                    }
                    textView.setText(items[chosen[0]]);
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    private void validate(TextView tv) {

        String value = tv.getText().toString();
        if (checkLocation.matcher(value).matches()) {
            hideError(tv);
        } else {
            showError(tv);
        }

    }

    private void showError(TextView view) {
        view.setError(ERROR_TEXT);
    }

    private void hideError(TextView view) {
        view.setError(null);
    }


    private class OnBackClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {

            SharedPreferences sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("Temp", temperatureUnits.getText().toString());
            editor.putString("Pres", pressureUnits.getText().toString());
            editor.putString("Wind", windUnits.getText().toString());
            editor.putString("City", currentLocation);
            editor.commit();

            SearchHistoryDao searchHistoryDao = App.getInstance().getLocationDao();
            List<Location> list = searchHistoryDao.getAllLocations();
            for (int i = 0; i <searchHistoryDao.getCountLocations() ; i++) {
                if(list.get(i).location.equals(currentLocation)){
                    finish();
                }
            }
            Location location = new Location();
            location.location = currentLocation;
            searchHistoryDao.insertLocation(location);
            finish();
        }
    }

    private void hideSoftKeyboard () {
        TextInputLayout textInputLayout = findViewById(R.id.textInputLayout);
        if(!textInputLayout.isFocused()){
            return;
        }
        Activity activity = this;
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService (Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow (activity.getCurrentFocus (). getWindowToken (), 0);
    }

    private OnDialogListener dialogListener = new OnDialogListener() {
        @Override
        public void onDialogNo() {

        }
        @Override
        public void onDialogYes() {
            currentLocation = Objects.requireNonNull(location.getText()).toString();
        }
    };

}