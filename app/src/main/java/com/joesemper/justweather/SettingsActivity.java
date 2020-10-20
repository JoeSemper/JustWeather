package com.joesemper.justweather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.data.SingleRefDataBufferIterator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;
import java.util.regex.Pattern;

public class SettingsActivity extends AppCompatActivity {

    private final String ERROR_TEXT = "Wrong location format";

    private TextInputEditText location;
    private String currentLocation;
    private TableRow rowTemperature;
    private TableRow rowPressure;
    private TableRow rowWindSpeed;
    private TextView temperatureUnits;
    private TextView pressureUnits;
    private TextView windUnits;

    public static final String CITY = "CITY";
    public static final String SETTINGS = "SETTINGS";
    public static final String UNITS = "UNITS";
    public static final String LAT = "lat";
    public static final String LON = "lon";
    public static final String STANDARD = "standard";
    public static final String METRIC = "metric";
    public static final String IMPERIAL = "imperial";

    private Toolbar toolbar;

    private static final int RC_SIGN_IN = 40404;
    private static final String TAG = "GoogleAuth";

    private GoogleSignInClient googleSignInClient;

    private com.google.android.gms.common.SignInButton buttonSignIn;
    private MaterialButton buttonSingOut;
    private TextView email;

    private Spinner spinner;
    private String currentUnits;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initViews();

        setOnUnitsClickListeners();

        loadPreferences();

        setOnButtonsClickListeners();

        initSignIn();

        initToolbar();
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(new OnBackClickListener());
        setSupportActionBar(toolbar);
    }

    private void initSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        buttonSignIn = findViewById(R.id.sign_in_button);
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        googleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI("email");
                        enableSign();
                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();
        enableSign();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            disableSign();
            buttonSignIn.setEnabled(false);
            updateUI(account.getEmail());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }


    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            disableSign();
            updateUI(account.getEmail());
        } catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void updateUI(String idToken) {
        email.setText(idToken);
    }

    private void enableSign(){
        buttonSignIn.setEnabled(true);
        buttonSingOut.setEnabled(false);
    }

    private void disableSign(){
        buttonSignIn.setEnabled(false);
        buttonSingOut.setEnabled(true);
    }

    private void initViews() {
        rowTemperature = findViewById(R.id.temperaure_row);
        rowPressure = findViewById(R.id.pressure_row);
        rowWindSpeed = findViewById(R.id.wind_row);
        temperatureUnits = findViewById(R.id.temp_units);
        pressureUnits = findViewById(R.id.pressure_units);
        windUnits = findViewById(R.id.wind_units);
        buttonSingOut = findViewById(R.id.sing_out_button);
        email = findViewById(R.id.email);
        spinner = findViewById(R.id.spinner_unit_system);
    }

    private void loadPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(SETTINGS, MODE_PRIVATE);
        currentUnits = sharedPreferences.getString(UNITS, METRIC);
        setUnits(currentUnits);
    }

    private void setUnits(String units){
        switch (units) {
            case METRIC:
                temperatureUnits.setText(R.string.units_temp_celsius);
                pressureUnits.setText(R.string.units_pressure);
                windUnits.setText(R.string.units_wind_meter);
                break;
            case IMPERIAL:
                temperatureUnits.setText(R.string.units_temp_far);
                pressureUnits.setText(R.string.units_pressure);
                windUnits.setText(R.string.units_wind_miles);
                break;
            default:
                temperatureUnits.setText(R.string.units_temp_kelvin);
                pressureUnits.setText(R.string.units_pressure);
                windUnits.setText(R.string.units_wind_meter);
        }
    }

    private void setOnUnitsClickListeners() {
        rowTemperature.setOnClickListener(new OnUnitsClickListener(OnUnitsClickListener.TEMPERATURE));
        rowPressure.setOnClickListener(new OnUnitsClickListener(OnUnitsClickListener.PRESSURE));
        rowWindSpeed.setOnClickListener(new OnUnitsClickListener(OnUnitsClickListener.WIND));
    }

    private void setOnButtonsClickListeners() {

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String[] strings = getResources().getStringArray(R.array.choose_unit_system);
                switch (strings[i]){
                    case "Standard":
                        setUnits(STANDARD);
                        currentUnits = STANDARD;
                        break;
                    case "Imperial":
                        setUnits(IMPERIAL);
                        currentUnits = IMPERIAL;
                        break;
                    default:
                        setUnits(METRIC);
                        currentUnits = METRIC;
                }
                savePreferences();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        buttonSingOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

    }

    private class OnUnitsClickListener implements View.OnClickListener {

        public static final String TEMPERATURE = "TEMP";
        public static final String PRESSURE = "PRES";
        public static final String WIND = "WIND";

        private String title;

        private TextView textView;

        String[] items;

        final int[] chosen = {-1};

        OnUnitsClickListener(String unit) {
            if (unit.equals(TEMPERATURE)) {
                items = getResources().getStringArray(R.array.choose_temp);
                title = "Temperature Units";
                textView = temperatureUnits;
            } else if (unit.equals(PRESSURE)) {
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
                    if (chosen[0] == -1) {
                        return;
                    }
                    textView.setText(items[chosen[0]]);
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    private void savePreferences(){
        SharedPreferences sharedPreferences = getSharedPreferences(SETTINGS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(UNITS, currentUnits);
        editor.apply();
    }


    private class OnBackClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            savePreferences();
            finish();
        }
    }
}