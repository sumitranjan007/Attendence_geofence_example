package com.athentech.attendence;

import static com.athentech.attendence.BaseApplication.CHANNEL_ID;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

public class GeofenceService extends JobIntentService {
    private static final String TAG="geofence_service";
    private static final int JOB_ID=527;
     public static void enqueueWork(Context context,Intent intent){
         enqueueWork(context,GeofenceService.class,JOB_ID,intent);
     }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceStatusCodes
                    .getStatusCodeString(geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
         return;
        }
        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            fireNotification(getTransition(triggeringGeofences), this, "Entered into geofence");
            Log.d(TAG, "Entered the location");
        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            Log.d(TAG, "Exited  the location");
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            fireNotification(getTransition(triggeringGeofences), this, "Exited from geofence");
        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {
            Log.d(TAG, "Staying in the location");
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            fireNotification(getTransition(triggeringGeofences), this, "Staying inside geofence");
        }
    }

    private String getTransition(List<Geofence> triggeringGeofences){
        ArrayList<String> idList=new ArrayList<>();
        for (Geofence geofence : triggeringGeofences){
            idList.add(geofence.getRequestId());
        }
        return TextUtils.join(",",idList);
    }
    private void fireNotification(String msg, Context context, String title){
     int rn = (int)(1 + (Math.random() * 21));
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(context,CHANNEL_ID)
                .setSmallIcon(R.drawable.client_icon)
                .setContentTitle(title)
                .setContentText(msg)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notificationManager.notify(rn,builder.build());

    }
}
