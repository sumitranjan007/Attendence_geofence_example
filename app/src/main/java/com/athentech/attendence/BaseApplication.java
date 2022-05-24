package com.athentech.attendence;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

public class BaseApplication extends Application {
   public static final String CHANNEL_ID="practice_geofence";
   public static final String CHANNEL_NAME="geofence";
    @Override
    public void onCreate() {
        super.onCreate();
     creteNotificationChannel();
    }
    private void creteNotificationChannel(){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            String channelName=CHANNEL_NAME;
            String description="Athentech practiceing geofence";
            int importenace= NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel=new NotificationChannel(CHANNEL_ID,channelName,importenace);
            notificationChannel.setDescription(description);
            NotificationManager notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}



























