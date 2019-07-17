package com.android.locationshareapp.db_room.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.android.locationshareapp.db_room.entity.LocationEntity;
import com.android.locationshareapp.db_room.entity.TripEntity;

import java.util.List;

@Dao
public interface AppDbDAO {

    @Insert
    void insertRouteItem(TripEntity item);

    @Insert
    void insertLocationItem(LocationEntity item);

    @Query("SELECT MAX(id) AS id FROM trip_entity")
    long getMaxId();

    @Query("SELECT * FROM trip_entity ORDER BY id DESC")
    List<TripEntity> getTripList();

    @Query("SELECT * FROM location_entity WHERE route_id =:routeId  ORDER BY id ASC")
    List<LocationEntity> getLocationList(long routeId);

    @Query("DELETE FROM trip_entity WHERE id=:tripId")
    void removeTrip(int tripId);

    @Query("DELETE FROM location_entity WHERE id=:tripId")
    void removeTripLocations(int tripId);
}
