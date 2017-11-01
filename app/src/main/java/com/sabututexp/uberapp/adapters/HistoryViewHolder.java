package com.sabututexp.uberapp.adapters;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.sabututexp.uberapp.R;
import com.sabututexp.uberapp.activities.HistorySingleActivity;

/**
 * Created by s on 11/1/17.
 */

public class HistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView rideId,time;
    public HistoryViewHolder(View itemView) {
        super(itemView);
        rideId = (TextView) itemView.findViewById(R.id.rideId);
        time = (TextView) itemView.findViewById(R.id.time);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        Intent intent = new Intent(view.getContext().getApplicationContext(), HistorySingleActivity.class);
        Bundle b = new Bundle();
        b.putString("rideId", rideId.getText().toString());
        intent.putExtras(b);
        view.getContext().startActivity(intent);
    }
}
