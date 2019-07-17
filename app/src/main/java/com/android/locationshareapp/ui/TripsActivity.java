package com.android.locationshareapp.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.android.locationshareapp.R;
import com.android.locationshareapp.db_room.dao.AppDbDAO;
import com.android.locationshareapp.db_room.room.AppDatabase;
import com.android.locationshareapp.ui.adapter.TripsAdapter;


public class TripsActivity extends AppCompatActivity implements TripsAdapter.OnItemClickListener {

    private TripsAdapter mTripsAdapter;
    public static String TRIP_ID = "trip_id";
    private AppDbDAO appDbDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            RecyclerView recyclerView = findViewById(R.id.recyclerView);
            appDbDAO = AppDatabase.getDatabase(getApplicationContext()).appDao();
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            mTripsAdapter = new TripsAdapter(this);
            recyclerView.setAdapter(mTripsAdapter);
            mTripsAdapter.setItems(appDbDAO.getTripList());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClicked(int index) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra(TRIP_ID, mTripsAdapter.getItem(index).getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteClicked(int index) {
        new AlertDialog.Builder(this)
                .setIcon(R.mipmap.ic_launcher_round)
                .setTitle("Confirmation")
                .setMessage("Are you sure want to remove?")
                .setPositiveButton("Confirm", (dialogInterface, i) -> {
                    appDbDAO.removeTrip(mTripsAdapter.getItem(index).getId());
                    appDbDAO.removeTripLocations(mTripsAdapter.getItem(index).getId());
                    mTripsAdapter.removeItem(index);
                }).setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
