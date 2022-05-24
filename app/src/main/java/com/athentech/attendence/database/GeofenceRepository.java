package com.athentech.attendence.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class GeofenceRepository {

    private GeofenceDao mGeofenceDao;
    private LiveData<List<GeofenceList>> mAllGeofenceList;

    GeofenceRepository(Application application){
        GeofenceDatabase db=GeofenceDatabase.getDatabase(application);
        mGeofenceDao=db.geofenceDao();
        mAllGeofenceList=mGeofenceDao.getAll();
    }

    LiveData<List<GeofenceList>> getAllGeofenceList(){
        return mAllGeofenceList;
    }
    void insert(GeofenceList geofenceList){
        GeofenceDatabase.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mGeofenceDao.insertAll(geofenceList);
            }
        });
    }
}






























