package com.joesemper.justweather;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.joesemper.justweather.interfaces.Constants;
import com.joesemper.justweather.interfaces.OnDialogListener;

import java.util.Objects;
import java.util.regex.Pattern;

public class SettingsActivity extends AppCompatActivity implements Constants {

    private final String ERROR_TEXT = "Wrong location format";

    private TextInputEditText location;

    private String currentLocation;

    private Pattern checkLocation = Pattern.compile("^[A-Z][a-z]{2,}$");

    private final Settings settings = Settings.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch wind = findViewById(R.id.wind_check);
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch pressure = findViewById(R.id.pressure_check);
        location = findViewById(R.id.locatinon_text_input);
        final MaterialButton setButton = findViewById(R.id.set_button);

        Parcel parcel = (Parcel) Objects.requireNonNull(getIntent().getExtras()).getSerializable(SETTINGS);

        if (parcel != null) {
            wind.setChecked(parcel.isWindOn);
            pressure.setChecked(parcel.isPressureOn);
            location.setText(parcel.location);
            currentLocation = parcel.location;
        }

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


//                    Snackbar.make(view, "Change location?", Snackbar.LENGTH_INDEFINITE)
//                            .setAction("Yes", new OnSnackBarClickListener()).show();

                }
            }
        });


        ImageButton back = findViewById(R.id.back);
        MaterialButton apply = findViewById(R.id.apply_button);

        back.setOnClickListener(new OnBackClickListener());
        apply.setOnClickListener(new OnBackClickListener());

//        settings = Settings.getInstance();

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

    private class OnSnackBarClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            currentLocation = Objects.requireNonNull(location.getText()).toString();
        }
    }

    private class OnBackClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {

            Parcel parcel = new Parcel();

            @SuppressLint("UseSwitchCompatOrMaterialCode") Switch wind = findViewById(R.id.wind_check);
            @SuppressLint("UseSwitchCompatOrMaterialCode") Switch pressure = findViewById(R.id.pressure_check);

            parcel.isPressureOn = pressure.isChecked();
            parcel.isWindOn = wind.isChecked();
            parcel.location = currentLocation;

            settings.addLocation(currentLocation);

            Intent intentResult = new Intent();
            intentResult.putExtra(SETTINGS, parcel);
            setResult(OK, intentResult);
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