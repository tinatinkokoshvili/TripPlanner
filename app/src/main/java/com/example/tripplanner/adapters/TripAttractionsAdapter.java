package com.example.tripplanner.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tripplanner.R;
import com.example.tripplanner.activities.AtrDetailsActivity;
import com.example.tripplanner.models.Attraction;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;


import java.util.Collections;
import java.util.List;

public class TripAttractionsAdapter extends RecyclerView.Adapter<TripAttractionsAdapter.ViewHolder> {
    private static final String TAG = "TripAttractionsAdapter";
    private static final String API_KEY = "AIzaSyCe2kjKuINrKzh9bvmGa-ToZiEvluGRzwU";

    private static final String noPhotoAvailableUrl = "https://archive.org/download/no-photo-available/no-photo-available.png";
    Context context;
    List<Attraction> attractionsList;
    PlacesClient placesClient;

    public TripAttractionsAdapter(Context context, List<Attraction> attractionsList) {
        this.context = context;
        this.attractionsList = attractionsList;
    }

    @NonNull
    @Override
    public TripAttractionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_trip_attraction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripAttractionsAdapter.ViewHolder holder, int position) {
        Attraction attraction = attractionsList.get(position);
        Log.i(TAG, "Binding " + attraction.name + " position " + position);
        holder.bind(attraction);
    }

    @Override
    public int getItemCount() {
        return attractionsList.size();
    }

    public void clear() {
        attractionsList.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Attraction> list) {
        attractionsList.addAll(list);
        notifyDataSetChanged();
    }

    public void add(Attraction attraction) {
        attractionsList.add(attraction);
        notifyItemInserted(attractionsList.size() - 1);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView ivTripAtrPic;
        TextView tvTripAtrName;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivTripAtrPic = itemView.findViewById(R.id.ivTripAtrPic);
            tvTripAtrName = itemView.findViewById(R.id.tvTripAtrName);
        }

        public void bind(Attraction attraction) {
            loadPhotoBitmap(attraction);
            Log.i(TAG, "setting name for " + attraction.name);
            tvTripAtrName.setText(attraction.name);
        }

        public Attraction loadPhotoBitmap(Attraction atr) {
            Places.initialize(context, API_KEY);
            placesClient = Places.createClient(context);
            //API calls to get photo of the place
            //PlacesClient placesClient = Places.createClient(context);
            final String placeId = atr.getPlaceId();
            Log.i(TAG, "place id " + placeId);
            final List<Place.Field> fields = Collections.singletonList(Place.Field.PHOTO_METADATAS);
            final FetchPlaceRequest placeRequest = FetchPlaceRequest.newInstance(placeId, fields);
            placesClient.fetchPlace(placeRequest).addOnSuccessListener((response) -> {
                final Place place = response.getPlace();
                final List<PhotoMetadata> metadata = place.getPhotoMetadatas();
                if (metadata == null || metadata.isEmpty()) {
                    Log.w(TAG, "No photo metadata.");
                    return;
                }
                final PhotoMetadata photoMetadata = metadata.get(0);
                final String attributions = photoMetadata.getAttributions();
                // Create a FetchPhotoRequest.
                final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
//                    .setMaxWidth(500) // Optional.
//                    .setMaxHeight(300) // Optional.
                        .build();
                placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                    Bitmap bitmap = fetchPhotoResponse.getBitmap();
                    Log.i(TAG, "bitmap fetched " + bitmap);
                    atr.photo = bitmap;
                    if (atr.getPhoto() != null) {
                        Log.i(TAG, "photo bitmap " + atr.getPhoto());
                        ivTripAtrPic.setImageBitmap(atr.getPhoto());
                    }
                    if (ivTripAtrPic.getDrawable() == null) {
                        Glide.with(context)
                                .load(R.drawable.ic_image_solid).into(ivTripAtrPic);
                    }
                }).addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        final ApiException apiException = (ApiException) exception;
                        Log.e(TAG, "Place not found: " + exception.getMessage());
                    }
                });
            });
            return atr;
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();

        }
    }

}
