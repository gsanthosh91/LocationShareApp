package com.android.locationshareapp.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.locationshareapp.R;
import com.android.locationshareapp.db_room.entity.TripEntity;
import com.android.locationshareapp.ui.adapter.view_holder.TripsVH;

import java.util.ArrayList;
import java.util.List;

public class TripsAdapter extends RecyclerView.Adapter {
    private List<TripEntity> tripList;
    private OnItemClickListener onItemClickListener;

    public TripsAdapter(OnItemClickListener listener) {
        this.tripList = new ArrayList<>();
        onItemClickListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_item, parent, false);
        return new TripsVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TripsVH) {
            ((TripsVH) holder).setData(getItem(position), onItemClickListener);
        }
    }

    public TripEntity getItem(int index) {
        return tripList.get(index);
    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }

    public void setItems(List<TripEntity> tripList) {
        this.tripList = tripList;
        notifyDataSetChanged();
    }

    public void removeItem(int index) {
        tripList.remove(index);
        notifyItemRemoved(index);
    }

    public interface OnItemClickListener {
        void onItemClicked(int index);

        void onDeleteClicked(int index);
    }
}
