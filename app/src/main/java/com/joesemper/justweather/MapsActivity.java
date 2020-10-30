package com.joesemper.justweather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.ListFragment;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.joesemper.justweather.services.SearchWorker;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.joesemper.justweather.SettingsActivity.CITY;
import static com.joesemper.justweather.SettingsActivity.LAT;
import static com.joesemper.justweather.SettingsActivity.LON;
import static com.joesemper.justweather.SettingsActivity.SETTINGS;

public class MapsActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 10;

    private int myResult;

    private GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

//        requestPermissions();

        initToolbar();

        initFragment(new SearchListFragment());
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.map_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                Toast.makeText(MapsActivity.this, searchView.getQuery().toString(), Toast.LENGTH_SHORT).show();
                Data input = new Data.Builder().putString(CITY, query).build();
                OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest
                        .Builder(SearchWorker.class)
                        .setInputData(input)
                        .build();
                WorkManager workManager = WorkManager.getInstance(MapsActivity.this);
                workManager.enqueue(oneTimeWorkRequest);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Toolbar toolbar = findViewById(R.id.map_toolbar);
        switch (id) {
            case R.id.search:
                SearchView searchView = findViewById(R.id.search);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    searchView.setFocusedByDefault(true);
                }
                return true;
            case R.id.action_map:
                initFragment(new MapFragment());
                onPrepareOptionsMenu(toolbar.getMenu());
                return true;
            case R.id.action_list:
                initFragment(new SearchListFragment());
                onPrepareOptionsMenu(toolbar.getMenu());
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private synchronized void initFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_frame, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(menu.getItem(1).isVisible()){
            menu.getItem(1).setVisible(false);
            menu.getItem(2).setVisible(true);
        } else {
            menu.getItem(1).setVisible(true);
            menu.getItem(2).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }





//    private void initSearchByAddress() {
//        findViewById(R.id.buttonSearch).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final Geocoder geocoder = new Geocoder(MapsActivity.this);
//                final String searchText = address.getText().toString();
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            List<Address> addresses = geocoder.getFromLocationName(searchText, 1);
//                            if (addresses.size() > 0) {
//                                final LatLng location = new LatLng(addresses.get(0).getLatitude(),
//                                        addresses.get(0).getLongitude());
//                                currentLatLng = location;
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        mMap.clear();
//                                        mMap.addMarker(new MarkerOptions()
//                                                .position(location)
//                                                .title(searchText)
//                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
//                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, (float) 15));
//                                        applyButton.setEnabled(true);
//                                    }
//                                });
//                            }
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }).start();
//            }
//        });
//    }


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
//                    currentMarker.setPosition(currentPosition);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, (float) 12));
                }
            });
        }
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