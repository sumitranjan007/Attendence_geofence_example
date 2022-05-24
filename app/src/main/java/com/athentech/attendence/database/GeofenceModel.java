package com.athentech.attendence.database;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class GeofenceModel extends AndroidViewModel {
    private GeofenceRepository mRepository;
    private final LiveData<List<GeofenceList>> mAllGeofence;
    public GeofenceModel(@NonNull Application application) {
        super(application);
        mRepository=new GeofenceRepository(application);
        mAllGeofence=mRepository.getAllGeofenceList();
    }
   public LiveData<List<GeofenceList>> getAllGeofenceList(){
        return mAllGeofence;
    }
    public void insert(GeofenceList geofenceList){
        mRepository.insert(geofenceList);
    }
}





























