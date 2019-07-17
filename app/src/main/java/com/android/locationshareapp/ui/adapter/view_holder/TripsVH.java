package com.android.locationshareapp.ui.adapter.view_holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.locationshareapp.R;
import com.android.locationshareapp.db_room.entity.TripEntity;
import com.android.locationshareapp.helper.AppHelper;
import com.android.locationshareapp.ui.adapter.TripsAdapter;

public class TripsVH extends RecyclerView.ViewHolder {

    private TripsAdapter.OnItemClickListener mListener;
    private TextView tvCreated;
    private TextView tvStart;
    private TextView tvStop;

    public TripsVH(@NonNull View itemView) {
        super(itemView);
        tvCreated = itemView.findViewById(R.id.tvCreated);
        tvStart = itemView.findViewById(R.id.tvStart);
        tvStop = itemView.findViewById(R.id.tvStop);
        ImageView ivDelete = itemView.findViewById(R.id.ivDelete);
        itemView.setOnClickListener(view -> {
            if (mListener != null)
                mListener.onItemClicked(getLayoutPosition());
        });
        ivDelete.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.onDeleteClicked(getLayoutPosition());
            }
        });

    }

    public void setData(TripEntity item, TripsAdapter.OnItemClickListener mListener) {
        this.mListener = mListener;
        tvStart.setText(item.getStartPoint());
        tvStop.setText(item.getEndPoint());
        tvCreated.setText(AppHelper.getFormattedDate(item.getTimestamp()));
    }

}
