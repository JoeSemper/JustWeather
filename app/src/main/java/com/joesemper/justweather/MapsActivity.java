package com.joesemper.justweather;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.joesemper.justweather.SettingsActivity.CITY;
import static com.joesemper.justweather.SettingsActivity.LAT;
import static com.joesemper.justweather.SettingsActivity.LON;
import static com.joesemper.justweather.SettingsActivity.SETTINGS;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private EditText address;
    private Button applyButton;

    private LatLng currentLatLng;

    private Marker currentMarker;

    private static final int PERMISSION_REQUEST_CODE = 10;

    private GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initViews();

        requestPermissions();

        setOnApplyClickListener();
    }

    private void setOnApplyClickListener(){
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePreferences();
                finish();
            }
        });
    }

    private void savePreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(SETTINGS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CITY, address.getText().toString());
        editor.putFloat(LAT, (float) currentLatLng.latitude);
        editor.putFloat(LON, (float) currentLatLng.longitude);
        editor.apply();
    }

    private void initViews() {
        address = findViewById(R.id.search_address);
        applyButton = findViewById(R.id.button_apply_search);
        applyButton.setClickable(false);
        initSearchByAddress();
    }

    private void initSearchByAddress() {
        findViewById(R.id.buttonSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Geocoder geocoder = new Geocoder(MapsActivity.this);
                final String searchText = address.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            List<Address> addresses = geocoder.getFromLocationName(searchText, 1);
                            if (addresses.size() > 0) {
                                final LatLng location = new LatLng(addresses.get(0).getLatitude(),
                                        addresses.get(0).getLongitude());
                                currentLatLng = location;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mMap.clear();
                                        mMap.addMarker(new MarkerOptions()
                                                .position(location)
                                                .title(searchText)
                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, (float) 15));
                                        applyButton.setEnabled(true);
                                    }
                                });
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }


        private void requestPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            requestLocation();
        } else {
            requestLocationPermissions();
        }
    }

    private void requestLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);

        String provider = locationManager.getBestProvider(criteria, true);
        if (provider != null) {

            locationManager.requestLocationUpdates(provider, 10000, 10, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double lat = location.getLatitude(); // Широта
                    String latitude = Double.toString(lat);

                    double lng = location.getLongitude(); // Долгота
                    String longitude = Double.toString(lng);

                    String accuracy = Float.toString(location.getAccuracy());   // Точность

                    LatLng currentPosition = new LatLng(lat, lng);
                    currentMarker.setPosition(currentPosition);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, (float) 12));
                }
            });
        }
    }

    private void requestLocationPermissions() {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    PERMISSION_REQUEST_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length == 2 &&
                    (grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                requestLocation();
            }
        }
    }


    private List<Marker> markers = new ArrayList<Marker>();

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

//        LatLng sydney = new LatLng(-34, 151);
//        currentMarker = mMap.addMarker(new MarkerOptions().position(sydney).title("Текущая позиция"));
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                getAddress(latLng);
                addMarker(latLng);
                applyButton.setEnabled(true);
            }
        });

    }

    private void getAddress(final LatLng location) {
        final Geocoder geocoder = new Geocoder(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final List<Address> addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1);
                    address.post(new Runnable() {
                        @Override
                        public void run() {
                            address.setText(addresses.get(0).getLocality());
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void addMarker(LatLng location){
        currentLatLng = location;
        mMap.clear();
        String title = Integer.toString(markers.size());
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(location)
                .title(title)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
        markers.add(marker);
    }

//    private void hideSoftKeyboard() {
//        TextInputLayout textInputLayout = findViewById(R.id.textInputLayout);
//        if (!textInputLayout.isFocused()) {
//            return;
//        }
//        Activity activity = this;
//        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
//        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
//    }

}