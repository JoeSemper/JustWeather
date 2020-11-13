package com.joesemper.justweather.services;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import static com.joesemper.justweather.SettingsActivity.CITY;

public class SearchWorker extends Worker {

    public static final String WORK_MANAGER = "WorkManager";
    public static final String SEARCH_RESULT_LAT = "LAT";
    public static final String SEARCH_RESULT_LON = "LON";
    public static final String SEARCH_PARCEL = "PARCEL";

    List<Address> addresses = null;


    String query;

    public SearchWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        query = workerParams.getInputData().getString(CITY);
    }

    @NonNull
    @Override
    public ListenableWorker.Result doWork() {
        final Geocoder geocoder = new Geocoder(getApplicationContext());
        try {
            addresses = geocoder.getFromLocationName(query, 1);
            sendBroadcastSearchComplete();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ListenableWorker.Result.success();
    }

    private void sendBroadcastSearchComplete() {
        Intent broadcastIntent = new Intent(WORK_MANAGER);
        broadcastIntent.putExtra(SEARCH_RESULT_LAT, addresses.get(0).getLatitude());
        broadcastIntent.putExtra(SEARCH_RESULT_LON, addresses.get(0).getLongitude());
        broadcastIntent.putExtra(SEARCH_PARCEL, new AddressParcel(addresses));
        getApplicationContext().sendBroadcast(broadcastIntent);
    }

    public static class AddressParcel implements Parcelable {

        List<Address> addresses;

        public AddressParcel(List<Address> addresses) {
            this.addresses = addresses;
        }

        protected AddressParcel(Parcel in) {
            addresses = in.createTypedArrayList(Address.CREATOR);
        }

        public static final Creator<AddressParcel> CREATOR = new Creator<AddressParcel>() {
            @Override
            public AddressParcel createFromParcel(Parcel in) {
                return new AddressParcel(in);
            }

            @Override
            public AddressParcel[] newArray(int size) {
                return new AddressParcel[size];
            }
        };

        public List<Address> getAddresses() {
            return addresses;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeTypedList(addresses);
        }
    }
}
