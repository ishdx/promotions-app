package com.example.project483;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ListCampaignActivity extends AppCompatActivity {

    DatabaseHelper mDatabaseHelper;
    private ListView campaignList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_campaign);

        campaignList = (ListView) findViewById(R.id.campList);
        mDatabaseHelper = new DatabaseHelper(this);

        populateListView();
    }

    private void populateListView() {
        Cursor campaigns = mDatabaseHelper.getCampaigns();
        ArrayList<String> campList = new ArrayList<>();
        while (campaigns.moveToNext()){
            campList.add(campaigns.getString(1));
        }

        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, campList);
        campaignList.setAdapter(adapter);
    }
}
