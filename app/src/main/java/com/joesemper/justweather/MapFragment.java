package com.joesemper.justweather;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.joesemper.justweather.SettingsActivity.CITY;
import static com.joesemper.justweather.SettingsActivity.LAT;
import static com.joesemper.justweather.SettingsActivity.LON;
import static com.joesemper.justweather.SettingsActivity.SETTINGS;

public class MapFragment extends Fragment {

    private TextView address;
    private LatLng currentPosition;
    private Location currentCity;
    private MapView mapView;

    private List<Marker> markers = new ArrayList<Marker>();

    private Marker currentMarker;

    private static final int PERMISSION_REQUEST_CODE = 10;

    private GoogleMap mMap;

    public MapFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadPreferences();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        initViews(view);
        initMap(savedInstanceState);
        return view;
    }

    private void loadPreferences() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SETTINGS, MODE_PRIVATE);
        currentCity = new Location(
                sharedPreferences.getString(CITY, "Moscow"),
                sharedPreferences.getFloat(LAT, 55),
                sharedPreferences.getFloat(LON, 37));
        currentPosition = new LatLng(currentCity.lat, currentCity.lon);
    }

    private void savePreferences() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SETTINGS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CITY, address.getText().toString());
        editor.putFloat(LAT, (float) currentPosition.latitude);
        editor.putFloat(LON, (float) currentPosition.longitude);
        editor.apply();
    }

    private void initViews(View view){
        mapView = (MapView) view.findViewById(R.id.mapView);
        address = (TextView) view.findViewById(R.id.current_location);
        address.setText(currentCity.city);
    }

    private void initMap(Bundle savedInstanceState){
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
            mMap.setMyLocationEnabled(true);

            addMarker(currentPosition);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, (float) 10));
            mMap.setOnMapLongClickListener(new MyOnMapLongClickListener());
        }
    }

    private class MyOnMapLongClickListener implements GoogleMap.OnMapLongClickListener {
        @Override
        public void onMapLongClick(LatLng latLng) {
            getAddress(latLng);
            addMarker(latLng);
            savePreferences();
        }
    }


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

    private void displayAddress(LatLng location, Geocoder geocoder) throws IOException {
        final List<Address> addresses = geocoder.getFromLocation(location.latitude, location.longitude, 15);
        address.post(new Runnable() {
            @Override
            public void run() {
                if(addresses.get(0).getLocality() != null){
                    address.setText(addresses.get(0).getLocality());
                } else {
                    address.setText(addresses.get(0).getSubAdminArea());
                }
            }
        });
    }

    private void addMarker(LatLng location) {
        currentPosition = location;
        mMap.clear();
        mMap.addMarker(new MarkerOptions()
                .position(location)
                .title(currentCity.city)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
//        markers.add(marker);
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