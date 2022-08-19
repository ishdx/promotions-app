package com.example.project483;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.project483.broadcastReciever.SchedulerBroadCastReceiver;
import com.example.project483.modals.CampinModal;
import com.example.project483.modals.LocationModal;
import com.example.project483.service.GPSTracker;

import java.util.ArrayList;

public class UserActivity extends AppCompatActivity {
    private Cursor cursorOutput;
    private DatabaseHelper DBhelper;
    private RecyclerView recyclerView;
    private SwitchCompat switchCompat;
    ArrayList<CampinModal> camps = new ArrayList<>();

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        DBhelper = new DatabaseHelper(this);
        cursorOutput = DBhelper.getCampaigns();

        if (cursorOutput != null && cursorOutput.getCount() != 0) {
            cursorOutput.moveToFirst();


            do {
                String stringcamps = cursorOutput.getString(cursorOutput.getColumnIndexOrThrow("campaign_title"));

                CampinModal campinModal = new CampinModal();
                campinModal.setId(cursorOutput.getInt(0));
                campinModal.setTitle(cursorOutput.getString(1));
                campinModal.setDescription(cursorOutput.getString(2));
                campinModal.setLat(cursorOutput.getString(3));
                campinModal.setLon(cursorOutput.getString(4));

                camps.add(campinModal);

            } while (cursorOutput.moveToNext());
        }

        cursorOutput.close();
        cursorOutput = null;

        recyclerView = findViewById(R.id.camps_rv);
        recyclerView.setAdapter(new Adapter(this, camps, DBhelper));
        recyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        switchCompat = findViewById(R.id.sw);
        checkLocationPermission();
        switchCompat.performClick();

        switchCompat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!switchCompat.isChecked()) {

                    switchCompat.setText("Turn On Notification");
                    switchCompat.setChecked(false);
                    GPSTracker.shouldCancelAlarm = true;
                    cancelAlarmMeneger();
                } else {

                    switchCompat.setText("Turn Off Notification");
                    switchCompat.setChecked(true);
                    GPSTracker.shouldCancelAlarm = false;
                    checkLocationPermission();

                }

            }
        });


        //checkLocationPermission();
    }

    private void locationService() {

        // check if GPS enabled
        GPSTracker gpsTracker = new GPSTracker();
        LocationModal locationModal = new LocationModal();

        if (gpsTracker.getIsGPSTrackingEnabled()) {
            String stringLatitude = String.valueOf(gpsTracker.latitude);
            locationModal.setLatitude(stringLatitude);

            String stringLongitude = String.valueOf(gpsTracker.longitude);
            locationModal.setLongitude(stringLongitude);

            String country = gpsTracker.getCountryName(this);
            locationModal.setCity(country);

            String city = gpsTracker.getLocality(this);
            locationModal.setCity(city);

            String postalCode = gpsTracker.getPostalCode(this);
            locationModal.setPostalCode(postalCode);

            String addressLine = gpsTracker.getAddressLine(this);
            locationModal.setAddress(addressLine);

            Log.d("LocationService", stringLatitude + "," + stringLongitude + "," + country + "," + city +
                    "," + postalCode + "," + addressLine);

        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gpsTracker.showSettingsAlert();
        }

    }

    private void backGroundLocation() {

        Intent myIntent = new Intent(getApplicationContext(), SchedulerBroadCastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(), 1, myIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() +
                3000, pendingIntent);


    }

    private void cancelAlarmMeneger() {


        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(getApplicationContext(), SchedulerBroadCastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(), 1, myIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(pendingIntent);

    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Permission Alert")
                        .setMessage("Need Location Permission")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(UserActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            backGroundLocation();
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        backGroundLocation();

                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }

}