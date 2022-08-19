package com.example.project483.broadcastReciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import com.example.project483.service.GPSTracker;

public class SchedulerBroadCastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        //Toast.makeText(context, "called", Toast.LENGTH_SHORT).show();

        GPSTracker.mContext=context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, GPSTracker.class));
        } else {
            context.startService(new Intent(context, GPSTracker.class));
        }

    }
}
