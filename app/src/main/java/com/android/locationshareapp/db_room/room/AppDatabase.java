package com.android.locationshareapp.db_room.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.android.locationshareapp.db_room.dao.AppDbDAO;
import com.android.locationshareapp.db_room.entity.LocationEntity;
import com.android.locationshareapp.db_room.entity.TripEntity;

@Database(entities = {LocationEntity.class, TripEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract AppDbDAO appDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    // Create database here
                    INSTANCE = Room.databaseBuilder(context,
                            AppDatabase.class, "location_app")
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
