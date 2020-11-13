package com.joesemper.justweather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsActivity extends AppCompatActivity {

    private TextView temperatureUnits;
    private TextView pressureUnits;
    private TextView windUnits;
    private MaterialButton buttonCurrentUnitSystem;

    public static final String CITY = "CITY";
    public static final String SETTINGS = "SETTINGS";
    public static final String UNITS = "UNITS";
    public static final String LAT = "lat";
    public static final String LON = "lon";
    public static final String STANDARD = "Standard";
    public static final String METRIC = "Metric";
    public static final String IMPERIAL = "Imperial";
    public static final String AUTO_LOCATION = "AUTO_LOCATION";

    private static final int RC_SIGN_IN = 40404;
    private static final String TAG = "GoogleAuth";

    private GoogleSignInClient googleSignInClient;

    private com.google.android.gms.common.SignInButton buttonSignIn;
    private MaterialButton buttonSingOut;
    private TextView email;
    private SwitchMaterial autoLocationSwitch;

    private String currentUnits;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initViewsById();
        initToolbar();
        loadPreferences();
        initSignIn();
        setOnButtonsClickListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkSignIn();
    }

    private void initViewsById() {
        buttonSingOut = findViewById(R.id.sing_out_button);
        email = findViewById(R.id.email);
        temperatureUnits = findViewById(R.id.current_temp_units);
        pressureUnits = findViewById(R.id.current_pressure_units);
        windUnits = findViewById(R.id.current_wind_units);
        buttonCurrentUnitSystem = findViewById(R.id.current_units_button);
        buttonSignIn = findViewById(R.id.sign_in_button);
        autoLocationSwitch = findViewById(R.id.switch_auto_location);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new OnBackClickListener());
    }

    private void loadPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(SETTINGS, MODE_PRIVATE);
        currentUnits = sharedPreferences.getString(UNITS, METRIC);
        autoLocationSwitch.setChecked(sharedPreferences.getBoolean(AUTO_LOCATION, false));
        setUnits();
    }

    private void savePreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(SETTINGS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(UNITS, currentUnits);
        editor.putBoolean(AUTO_LOCATION, autoLocationSwitch.isChecked());
        editor.apply();
    }

    private void initSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void setOnButtonsClickListeners() {
        buttonCurrentUnitSystem.setOnClickListener(new OnUnitSystemClickListener());
        buttonSignIn.setOnClickListener(new OnSignInClickListener());
        buttonSingOut.setOnClickListener(new OnSignOutClickListener());
        autoLocationSwitch.setOnCheckedChangeListener(new OnWitchChangeListener());
    }

    private void checkSignIn() {
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

    private void enableSign() {
        buttonSignIn.setEnabled(true);
        buttonSingOut.setEnabled(false);
    }

    private void disableSign() {
        buttonSignIn.setEnabled(false);
        buttonSingOut.setEnabled(true);
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

    private void setUnits() {
        switch (currentUnits) {
            case METRIC:
                buttonCurrentUnitSystem.setText(R.string.units_metric);
                temperatureUnits.setText(R.string.units_temp_celsius);
                pressureUnits.setText(R.string.units_pressure);
                windUnits.setText(R.string.units_wind_meter);
                break;
            case IMPERIAL:
                buttonCurrentUnitSystem.setText(R.string.units_imperial);
                temperatureUnits.setText(R.string.units_temp_far);
                pressureUnits.setText(R.string.units_pressure);
                windUnits.setText(R.string.units_wind_miles);
                break;
            default:
                buttonCurrentUnitSystem.setText(R.string.units_standard);
                temperatureUnits.setText(R.string.units_temp_kelvin);
                pressureUnits.setText(R.string.units_pressure);
                windUnits.setText(R.string.units_wind_meter);
        }
    }

    private class OnBackClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            finish();
        }
    }

    private class OnSignOutClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            signOut();
        }
    }

    private class OnSignInClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            signIn();
        }
    }

    private class OnWitchChangeListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            savePreferences();
        }
    }

    private class OnUnitSystemClickListener implements View.OnClickListener {

        String[] items = getResources().getStringArray(R.array.choose_unit_system);
        final int[] chosen = getChosenCurrentSystem();

        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
            builder.setTitle(R.string.units_header).setSingleChoiceItems(R.array.choose_unit_system, chosen[0], new DialogInterface.OnClickListener() {
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
                    currentUnits = items[chosen[0]];
                    setUnits();
                    savePreferences();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }

        private int[] getChosenCurrentSystem (){
            for (int i = 0; i<items.length ; i++) {
                if (currentUnits.equals(items[i])){
                    return new int[]{i};
                }
            }
            return new int[]{-1};
        }
    }
}