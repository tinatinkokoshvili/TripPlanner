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
    private static final String noPhotoAvailableUrl = "https://archive.org/download/no-photo-available/no-photo-available.png";
    private static final String viewRoutePicUrl = "data:image/jpeg;base64,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";

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
        private final DecimalFormat df = new DecimalFormat("0.00");

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
            tvStat.setText(trip.getAttractionsInTrip().size() - 1 + " Attractions  •  " + df.format(trip.getActualTotalTime()) + "hrs");
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