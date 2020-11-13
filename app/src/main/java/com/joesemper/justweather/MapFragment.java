package com.joesemper.justweather;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.work.WorkManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.joesemper.justweather.database.Location;
import com.joesemper.justweather.services.SearchWorker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;
import static com.joesemper.justweather.SettingsActivity.AUTO_LOCATION;
import static com.joesemper.justweather.SettingsActivity.CITY;
import static com.joesemper.justweather.SettingsActivity.LAT;
import static com.joesemper.justweather.SettingsActivity.LON;
import static com.joesemper.justweather.SettingsActivity.SETTINGS;
import static com.joesemper.justweather.retrofit.RetrofitUpdater.UPDATE_FINISHED;
import static com.joesemper.justweather.retrofit.RetrofitUpdater.UPDATE_RESULT;
import static com.joesemper.justweather.retrofit.RetrofitUpdater.UPDATE_RESULT_OK;
import static com.joesemper.justweather.services.SearchWorker.SEARCH_PARCEL;
import static com.joesemper.justweather.services.SearchWorker.SEARCH_RESULT_LAT;
import static com.joesemper.justweather.services.SearchWorker.SEARCH_RESULT_LON;
import static com.joesemper.justweather.services.SearchWorker.WORK_MANAGER;

public class MapFragment extends Fragment {

    private TextView address;
    private LatLng currentPosition;
    private Location currentCity;
    private MapView mapView;
    private Boolean isAutoLocationAllowed;

    private Marker currentMarker;

    private static final int PERMISSION_REQUEST_CODE = 10;

    private GoogleMap mMap;

    private BroadcastReceiver broadcastReceiver;

    public MapFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadPreferences();
        initBroadcastReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        initViews(view);
        initMap(savedInstanceState);
//        initSearchOnMap();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        regReceiver();
    }

    @Override
    public void onStop() {
        super.onStop();
        unRegReceiver();
    }

    private void regReceiver() {
        Objects.requireNonNull(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter(WORK_MANAGER));
    }

    private void unRegReceiver() {
        Objects.requireNonNull(getActivity()).unregisterReceiver(broadcastReceiver);
    }

    private void initBroadcastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                SearchWorker.AddressParcel parcel = intent.getParcelableExtra(SEARCH_PARCEL);
                List<Address> addresses = Objects.requireNonNull(parcel).getAddresses();
                currentPosition = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                getAddress(currentPosition);
                addMarker(currentPosition);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, (float) 10));

//                currentPosition = new LatLng(intent.getDoubleExtra(SEARCH_RESULT_LAT, 56),
//                        intent.getDoubleExtra(SEARCH_RESULT_LON, 56));
//
//                getAddress(currentPosition);
//                addMarker(currentPosition);
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, (float) 10));
            }
        };
    }

    private void loadPreferences() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SETTINGS, MODE_PRIVATE);
        currentCity = new Location(
                sharedPreferences.getString(CITY, "Moscow"),
                sharedPreferences.getFloat(LAT, 55),
                sharedPreferences.getFloat(LON, 37));
        currentPosition = new LatLng(currentCity.lat, currentCity.lon);
        isAutoLocationAllowed = sharedPreferences.getBoolean(AUTO_LOCATION, false);
    }

    private void savePreferences() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SETTINGS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CITY, address.getText().toString());
        editor.putFloat(LAT, (float) currentPosition.latitude);
        editor.putFloat(LON, (float) currentPosition.longitude);
        editor.apply();
    }

    private void initViews(View view) {
        mapView = (MapView) view.findViewById(R.id.mapView);
        address = (TextView) view.findViewById(R.id.current_location);
        address.setText(currentCity.city);
    }

    private void initMap(Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mapView.getMapAsync(new MyOnMapReadyCallback());
    }

    private class MyOnMapReadyCallback implements OnMapReadyCallback {
        @SuppressLint("MissingPermission")
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            if (isAutoLocationAllowed) {
                requestPermissions();
            }
            addMarker(currentPosition);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, (float) 10));
            mMap.setOnMapLongClickListener(new MyOnMapLongClickListener());
        }
    }

    private void requestPermissions() {
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            requestLocationPermissions();
        }
    }

    private void requestLocationPermissions() {
        if (!isAutoLocationAllowed) {
            return;
        }
        if (!ActivityCompat.shouldShowRequestPermissionRationale(Objects.requireNonNull(getActivity()), Manifest.permission.CALL_PHONE)) {
            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()),
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
                if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mMap.setMyLocationEnabled(true);
            }
        }
    }

    private class MyOnMapLongClickListener implements GoogleMap.OnMapLongClickListener {
        @Override
        public void onMapLongClick(LatLng latLng) {
            getAddress(latLng);
            addMarker(latLng);
        }
    }

//    private void onSearch() {
//        final Geocoder geocoder = new Geocoder(getActivity());
//        final SearchView searchView = Objects.requireNonNull(getActivity()).findViewById(R.id.search);
//        final String searchText = searchView.getQuery().toString();
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    List<Address> addresses = geocoder.getFromLocationName(searchText, 1);
//                    if (addresses.size() > 0) {
//                        final LatLng location = new LatLng(addresses.get(0).getLatitude(),
//                                addresses.get(0).getLongitude());
//                        currentPosition = location;
//                        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                mMap.clear();
//                                addMarker(currentPosition);
//                                getAddress(currentPosition);
//                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, (float) 15));
//                            }
//                        });
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }
//            @Override
//            public void onClick(View v) {
//                final Geocoder geocoder = new Geocoder(getActivity());
//                final String searchText = searchView.getQuery().toString();
//
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            List<Address> addresses = geocoder.getFromLocationName(searchText, 1);
//                            if (addresses.size() > 0) {
//                                final LatLng location = new LatLng(addresses.get(0).getLatitude(),
//                                        addresses.get(0).getLongitude());
//                                currentPosition = location;
//                                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        mMap.clear();
//                                        addMarker(currentPosition);
//                                        getAddress(currentPosition);
//                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, (float) 15));
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

    private void getAddress(final LatLng location) {
        final Geocoder geocoder = new Geocoder(getContext());
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    displayAddress(location, geocoder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private synchronized void displayAddress(LatLng location, Geocoder geocoder) throws IOException {
        final List<Address> addresses = geocoder.getFromLocation(location.latitude, location.longitude, 15);
        address.post(new Runnable() {
            @Override
            public void run() {
                if (addresses.get(0).getLocality() != null) {
                    address.setText(addresses.get(0).getLocality());
                } else {
                    address.setText(R.string.no_locality);
                    return;
                }
                savePreferences();
            }
        });
    }

    private synchronized void addMarker(LatLng location) {
        currentPosition = location;
        mMap.clear();
        mMap.addMarker(new MarkerOptions()
                .position(location)
                .title(currentCity.city)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}