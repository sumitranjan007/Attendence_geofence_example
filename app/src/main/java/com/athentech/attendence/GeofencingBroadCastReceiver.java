package com.athentech.attendence;

import static com.athentech.attendence.BaseApplication.CHANNEL_ID;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

public class GeofencingBroadCastReceiver extends BroadcastReceiver {
    private static final String TAG="geofence_broadcast";

    @Override
    public void onReceive(Context context, Intent intent) {
        GeofenceService.enqueueWork(context,intent);
    }

}






























