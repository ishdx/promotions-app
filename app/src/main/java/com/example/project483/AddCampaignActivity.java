package com.example.project483;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.project483.modals.LocationModal;
import com.example.project483.service.GPSTracker;

public class AddCampaignActivity extends Activity {

    DatabaseHelper mDatabaseHelper;
    private EditText editTitle, editDescription;
    private Button sendBtn, cancelBtn;

    private LocationModal locationModal;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_campaign);

        //Initiate views
        editTitle = (EditText) findViewById(R.id.editTitle);
        editDescription = (EditText) findViewById(R.id.editDisc);
        sendBtn = (Button) findViewById(R.id.sendButton);
        cancelBtn = (Button) findViewById(R.id.cancelButton);
        mDatabaseHelper = new DatabaseHelper(this);

        locationService();

        //Set dimensions
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width*.8), (int) (height*.6));

        //Send campaign
        sendCampaign();

        //Cancel sending
        canelation();

    }


    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void sendCampaign() {
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = editTitle.getText().toString().trim();
                String description = editDescription.getText().toString().trim();

                if (editTitle.length()!=0 && editDescription.length()!=0){
                    mDatabaseHelper.addCampaign(title, description, locationModal);
                    toastMessage("The campaign was sent to subscribers!");
                    finish();
                } else {
                    toastMessage("Please fill both fields!");
                }
            }
        });
    }

    private void canelation() {
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void locationService() {

        // check if GPS enabled
        GPSTracker.mContext=this;
        GPSTracker gpsTracker = new GPSTracker();
        locationModal=new LocationModal();

        if (gpsTracker.getIsGPSTrackingEnabled())
        {
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

            Log.d("LocationService",stringLatitude+ "," + stringLongitude + "," + country + "," + city +
                    "," + postalCode + "," + addressLine);

        }
        else
        {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gpsTracker.showSettingsAlert();
        }

    }

}
