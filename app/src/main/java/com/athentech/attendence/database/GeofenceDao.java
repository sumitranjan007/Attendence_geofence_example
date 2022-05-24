package com.athentech.attendence.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.google.android.gms.location.Geofence;

import java.util.List;

@Dao
public interface GeofenceDao {
    @Query("SELECT * FROM geofence_list_table")
    LiveData<List<GeofenceList>> getAll();

    @Insert
    void insertAll(GeofenceList geofenceList);

    @Delete
    void delete(GeofenceList geofenceList);
}
