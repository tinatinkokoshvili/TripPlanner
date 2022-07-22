package com.example.tripplanner.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tripplanner.R;
import com.example.tripplanner.activities.RouteActivity;
import com.example.tripplanner.models.Attraction;
import com.example.tripplanner.models.Trip;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;
import java.util.ArrayList;
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
        private ImageButton imBtnArrow;
        private ImageButton imBtnViewRoute;
        private LinearLayout hiddenLayout;
        private CardView cvPastTrip;
        private TextView tvStat;
        private TextView tvTripName;
        private TextView tvTripAuthor;
        private ImageView ivHeart;
        private ImageView ivFilledHeart;
        private TextView tvLikes;

        private RecyclerView rvTripAttractions;
        private TripAttractionsAdapter tripAttractionsAdapter;
        private List<Attraction> tripAttractionsList;
        private final DecimalFormat decimalFormat = new DecimalFormat("0.00");

        private FirebaseFirestore firestore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            firestore  = FirebaseFirestore.getInstance();
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
                    int position = getAdapterPosition();
                    Trip trip = null;
                    if (v.getId() == R.id.imBtnViewRoute) {
                        trip = tripList.get(position);
                        Log.i(TAG, "clicked " + trip.getTripName());
                        // Pass the list of attractions in right order
                        ArrayList<Attraction> atrInTrip = (ArrayList<Attraction>) trip.getAttractionsInTrip();
                        //Moving userLocation to last one so that it si
                        Attraction userLocation = atrInTrip.get(0);
                        atrInTrip.remove(0);
                        atrInTrip.add(userLocation);

                        Intent routeIntent = new Intent(context, RouteActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList("data", atrInTrip);
                        routeIntent.putExtras(bundle);
                        routeIntent.putExtra("tripName", trip.getTripName().toString());
                        routeIntent.putExtra("radius", trip.getRadius()).toString();
                        routeIntent.putExtra("totalTime", Double.toString(trip.getTotalTripTime()));
                        routeIntent.putExtra("avgStayTime", Double.toString(trip.getavgStayTime()));
                        context.startActivity(routeIntent);
                    }
                }
            });

            tvStat = itemView.findViewById(R.id.tvStat);
            tvTripName = itemView.findViewById(R.id.tvTripName);
            tvTripAuthor = itemView.findViewById(R.id.tvTripAuthor);
            ivHeart = itemView.findViewById(R.id.ivHeart);
            ivFilledHeart = itemView.findViewById(R.id.ivFilledHeart);
            tvLikes = itemView.findViewById(R.id.tvLikes);

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
            Log.i(TAG, "authorId "+ trip.getAuthorId());
            if (trip.getAuthorId() != null && !trip.getAuthorId().isEmpty()) {
                fetchAuthorDetails(trip.getAuthorId());
            }
            Glide.with(context).load(R.drawable.ufi_heart).into(ivHeart);
            ivFilledHeart.setVisibility(View.GONE);
            Glide.with(context).load(R.drawable.ufi_heart_active).into(ivFilledHeart);
            tvLikes.setText(trip.getLikes() + " Likes");
            ivHeart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "Heart Clicked");
                    ivFilledHeart.setVisibility(View.VISIBLE);
                    trip.incrementLikes();
                    saveNewTripLikes(trip);
                    tvLikes.setText(trip.getLikes() + " Likes");
                }
            });
            ivFilledHeart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "Filled Heart Clicked");
                    ivHeart.setVisibility(View.VISIBLE);
                    ivFilledHeart.setVisibility(View.GONE);
                    trip.decrementLikes();
                    saveNewTripLikes(trip);
                    tvLikes.setText(trip.getLikes() + " Likes");
                }
            });
        }

        private void saveNewTripLikes(Trip trip) {
            firestore.collection("testUsers")
                    .document(trip.getAuthorId()).collection("trips").document(trip.getTripName())
                    .set(trip).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.i(TAG, "trip likes successfully updates");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "error when updating trip likes");
                        }
                    });
        }

        private void fetchAuthorDetails(String author) {
            firestore.collection("testUsers").document(author)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                String fullName_result = task.getResult().getString("fullName");
                                String username_result = task.getResult().getString("username");
                                String picUrl_result = task.getResult().getString("picUrl");

                                //Glide.with(getContext()).load(picUrl_result).into(ivProfPagePic);
                                tvTripAuthor.setText(fullName_result + " • @" + username_result);

                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Profile does not exist.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        @Override
        public void onClick(View v) {
            return;
        }
    }
}