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

import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.widget.SearchView;

import com.joesemper.justweather.services.SearchWorker;

import java.util.Objects;

import static com.joesemper.justweather.SettingsActivity.CITY;

public class MapsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        initToolbar();

        initFragment(new MapFragment());
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
        if (id == R.id.search) {
            SearchView searchView = findViewById(R.id.search);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                searchView.setFocusedByDefault(true);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private synchronized void initFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_frame, fragment);
        fragmentTransaction.commit();
    }
}