package com.joesemper.justweather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

    private static final int RC_SIGN_IN = 40404;
    private static final String TAG = "GoogleAuth";

    private GoogleSignInClient googleSignInClient;

    private com.google.android.gms.common.SignInButton buttonSignIn;
    private MaterialButton buttonSingOut;
    private TextView email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initViews();

        setOnUnitsClickListeners();

        loadPreferences();

        setOnButtonsClickListeners();

        initSignIn();

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
        location = findViewById(R.id.locatinon_text_input);
        buttonSingOut = findViewById(R.id.sing_out_button);
        email = findViewById(R.id.email);


        setButton = findViewById(R.id.set_button);
        back = findViewById(R.id.back);
        apply = findViewById(R.id.apply_button);
    }

    private void loadPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        tempUnitsText = sharedPreferences.getString("Temp", "°C");
        pressureUnitsText = sharedPreferences.getString("Pres", "mm Hg");
        windUnitsText = sharedPreferences.getString("Wind", "m/s");
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

        buttonSingOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });


        location.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
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

                if (location.getError() != null) {
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
        }
    }

    private void hideSoftKeyboard() {
        TextInputLayout textInputLayout = findViewById(R.id.textInputLayout);
        if (!textInputLayout.isFocused()) {
            return;
        }
        Activity activity = this;
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
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