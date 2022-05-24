package com.athentech.attendence;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

public class GeofenceFunctions {
    private Context context;

    GeofenceFunctions(Context context){
        this.context=context;
    }
    private Geofence creategeofence(String geofenceID, LatLng latLng,float radius){
        Geofence geofence=new Geofence.Builder()
                .setRequestId(geofenceID)
                .setCircularRegion(latLng.latitude,latLng.longitude,radius)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |  Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();
        return geofence;
    }
    public GeofencingRequest requestGeofence(String geofenceID, LatLng latLng,float radius){
        GeofencingRequest request=new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER |  GeofencingRequest.INITIAL_TRIGGER_EXIT)
                .addGeofence(creategeofence(geofenceID, latLng, radius)).build();
        return request;
    }
    public PendingIntent geofencingPendingIntent(){
        PendingIntent pendingIntent=null;
        final Intent intent=new Intent(context,GeofencingBroadCastReceiver.class);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
             pendingIntent=PendingIntent.getBroadcast(context,0,intent,PendingIntent.FLAG_MUTABLE);
        }else{
             pendingIntent=PendingIntent.getBroadcast(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        }
        return pendingIntent;
    }

}

























