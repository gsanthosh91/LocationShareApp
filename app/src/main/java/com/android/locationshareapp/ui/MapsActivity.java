package com.android.locationshareapp.ui;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.android.locationshareapp.R;
import com.android.locationshareapp.db_room.entity.LocationEntity;
import com.android.locationshareapp.db_room.room.AppDatabase;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private int tripId = 0;
    private List<LocationEntity> mLocations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        Objects.requireNonNull(mapFragment).getMapAsync(this);
        tripId = getIntent().getIntExtra(TripsActivity.TRIP_ID, 0);
        mLocations = AppDatabase.getDatabase(getApplicationContext()).appDao().getLocationList(tripId);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        List<LatLng> mTripRoute = new ArrayList<>();
        if (mLocations.size() > 1) {
            for (LocationEntity item : mLocations) {
                mTripRoute.add(new LatLng(item.getLatitude(), item.getLongitude()));
            }
            LatLng from = new LatLng(mLocations.get(0).getLatitude(), mLocations.get(0).getLongitude());
            mMap.addMarker(new MarkerOptions().position(from)
                    .title("Start Point"));
            mMap.addMarker(new MarkerOptions().position(new LatLng(mLocations.
                    get(mLocations.size() - 1).getLatitude(), mLocations.get(mLocations.size() - 1).getLongitude()))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                    .title("End Point"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(from, 15));
            mMap.addPolyline(new PolylineOptions().addAll(mTripRoute));
        } else {
            Toast.makeText(this, "Location entry below two points.", Toast.LENGTH_SHORT).show();
        }
    }
}
