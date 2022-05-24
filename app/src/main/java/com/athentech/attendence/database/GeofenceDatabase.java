package com.athentech.attendence.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {GeofenceList.class},version = 1,exportSchema = false)
public abstract class GeofenceDatabase extends RoomDatabase {
    public abstract GeofenceDao geofenceDao();
    private static volatile GeofenceDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS=4;
    static final ExecutorService databaseWriterExecutor=
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    static GeofenceDatabase getDatabase(final Context context){
        if (INSTANCE==null){
            synchronized (GeofenceDatabase.class){
                if (INSTANCE==null){
                    INSTANCE= Room.databaseBuilder(context.getApplicationContext(),
                            GeofenceDatabase.class,"geofence_database").build();
                }
            }
        }
        return INSTANCE;
    }
}
































