package com.example.tripplanner.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.widget.TextView;

import com.example.tripplanner.R;
import com.example.tripplanner.models.Attraction;
import com.example.tripplanner.models.Trip;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

public class PastTripAdapter extends RecyclerView.Adapter<PastTripAdapter.ViewHolder> {
    private static final String TAG = "PastTripAdapter";
    Context context;
    List<Trip> tripList;

    public PastTripAdapter(Context context, List<Trip> tripList) {
        this.context = context;
        this.tripList = tripList;
    }

    @NonNull
    @Override
    public PastTripAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_past_trip, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Trip trip = tripList.get(position);
        Log.i(TAG, "Binding " + trip.getTripName() + " position " + position);
        holder.bind(trip);
    }


    @Override
    public int getItemCount() {
        return tripList.size();
    }

    public void clear() {
        tripList.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Trip> list) {
        tripList.addAll(list);
        notifyDataSetChanged();
    }

    public void add(Trip trip) {
        tripList.add(trip);
        Log.i(TAG, trip.getTripName());
        notifyItemInserted(tripList.size() - 1);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageButton imBtnArrow;
        ImageButton imBtnViewRoute;
        LinearLayout hiddenLayout;
        CardView cvPastTrip;
        TextView tvStat;
        TextView tvTripName;

        private RecyclerView rvTripAttractions;
        private TripAttractionsAdapter tripAttractionsAdapter;
        private List<Attraction> tripAttractionsList;
        private final DecimalFormat decimalFormat = new DecimalFormat("0.00");

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cvPastTrip = itemView.findViewById(R.id.cvPastTrip);
            hiddenLayout = itemView.findViewById(R.id.hiddenLayout);
            imBtnArrow = itemView.findViewById(R.id.imBtnArrow);
            imBtnArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (hiddenLayout.getVisibility() == View.VISIBLE) {
                        // Transition of the hiddenView
                        TransitionManager.beginDelayedTransition(cvPastTrip,
                                new AutoTransition());
                        hiddenLayout.setVisibility(View.GONE);
                        imBtnArrow.setImageResource(R.drawable.ic_angle_down_solid);
                    } else {
                        // The CardView is not expanded
                        TransitionManager.beginDelayedTransition(cvPastTrip,
                                new AutoTransition());
                        hiddenLayout.setVisibility(View.VISIBLE);
                        imBtnArrow.setImageResource(R.drawable.ic_angle_up_solid);
                    }
                }
            });
            imBtnViewRoute = itemView.findViewById(R.id.imBtnViewRoute);
            imBtnViewRoute.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO go to Route Activity
                    // Pass the list of attractions in right order
                }
            });
            tvStat = itemView.findViewById(R.id.tvStat);
            tvTripName = itemView.findViewById(R.id.tvTripName);

            rvTripAttractions = (RecyclerView) itemView.findViewById(R.id.rvTripAttractions);
            tripAttractionsList = new LinkedList<>();
            tripAttractionsAdapter = new TripAttractionsAdapter(context, tripAttractionsList);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            rvTripAttractions.setLayoutManager(linearLayoutManager);
            rvTripAttractions.setAdapter(tripAttractionsAdapter);
        }

        public void bind(Trip trip) {
            tvTripName.setText(trip.getTripName());
            tvStat.setText(trip.getAttractionsInTrip().size() - 1 + " Attractions  •  " + decimalFormat.format(trip.getActualTotalTime()) + "hrs");
            List<Attraction> atrInTrip = trip.getAttractionsInTrip();
            for (int i = 1; i < atrInTrip.size(); i++) {
                Log.i(TAG, "adding to adapter " + atrInTrip.get(i).getPhoto());
                Attraction curAtr = atrInTrip.get(i);
                tripAttractionsAdapter.add(atrInTrip.get(i));
            }
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();

        }
    }
}