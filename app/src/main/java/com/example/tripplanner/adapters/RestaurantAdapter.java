package com.example.tripplanner.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tripplanner.R;
import com.example.tripplanner.activities.AtrDetailsActivity;
import com.example.tripplanner.models.Attraction;
import com.example.tripplanner.models.Restaurant;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import org.parceler.Parcels;
import org.w3c.dom.Attr;

import java.text.DecimalFormat;
import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {
    private static final String TAG = "RestaurantAdapter";
    private static final String noPhotoAvailableUrl = "https://archive.org/download/no-photo-available/no-photo-available.png";
    Context context;
    List<Restaurant> restaurantList;
    List<Attraction> atrList;

    public RestaurantAdapter(Context context, List<Restaurant> restaurantList, List<Attraction> resAtrList) {
        this.context = context;
        this.restaurantList = restaurantList;
        this.atrList = resAtrList;
    }

    @NonNull
    @Override
    public RestaurantAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_restaurant, parent, false);
        return new RestaurantAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantAdapter.ViewHolder holder, int position) {
        Restaurant restaurant = restaurantList.get(position);
        Attraction attraction = atrList.get(position);
        Log.i(TAG, "Binding " + restaurant.name + " position " + position + " atrResName " + attraction.name);
        holder.bind(restaurant, attraction);
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    public void clear() {
        restaurantList.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Restaurant> list) {
        restaurantList.addAll(list);
        notifyDataSetChanged();
    }

    public void add(Restaurant restaurant, Attraction resAttraction) {
        restaurantList.add(restaurant);
        atrList.add(resAttraction);
        notifyItemInserted(restaurantList.size() - 1);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final DecimalFormat decimalFormat = new DecimalFormat("0.0");
        MaterialCardView cdAttraction;
        ImageView ivAtrPicture;
        TextView tvName;
        TextView tvAddress;
        TextView tvDescription;
        TextView tvRating;
        MaterialButton btnLearnMore;
        RatingBar rbResRating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAtrPicture = itemView.findViewById(R.id.ivAtrPicture);
            tvName = itemView.findViewById(R.id.tvName);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            //tvDescription = itemView.findViewById(R.id.tvDescription);
            btnLearnMore = itemView.findViewById(R.id.btnLearnMore);
            cdAttraction = itemView.findViewById(R.id.cdAttraction);
            itemView.setOnClickListener(this);
            rbResRating = itemView.findViewById(R.id.rbResRating);
        }

        public void bind(Restaurant restaurant, Attraction atr) {
            Log.i(TAG, "in bind  " + restaurant.name + " photo " + atr.name);
            if (atr.photo != null) {
                Log.i(TAG, "loading restaurant photo " + atr.name + " photo " + atr.photo);
                ivAtrPicture.setImageBitmap(atr.photo);
            }
            if (ivAtrPicture.getDrawable() == null) {
                Glide.with(context)
                        .load(noPhotoAvailableUrl).into(ivAtrPicture);
            }

            tvName.setText(restaurant.name);
            tvRating.setText(decimalFormat.format(restaurant.googleYelpRating));
            tvAddress.setText(restaurant.address);
            // tvDescription.setText(attraction.website);
            cdAttraction.setOnClickListener(this);
            btnLearnMore.setOnClickListener(this);
            float voteAverage = (float) Double.parseDouble(decimalFormat.format(restaurant.googleYelpRating));
            Log.i(TAG, restaurant.name + voteAverage);
            rbResRating.setRating(voteAverage);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Restaurant restaurant = null;
            if (position != RecyclerView.NO_POSITION && v.getId() != R.id.btnLearnMore) {
                restaurant = restaurantList.get(position);
                Log.i("adapter", "clicked " + restaurant.name);
                restaurant.picked = !restaurant.picked;
                cdAttraction.toggle();
            }
//            if (position != RecyclerView.NO_POSITION && v.getId() == R.id.btnLearnMore) {
//                restaurant = restaurantList.get(position);
//                Log.i("adapter", "Clicked Learn More about " + restaurant.name);
//                Intent intent = new Intent(context, AtrDetailsActivity.class);
//                intent.putExtra(Attraction.class.getSimpleName(), Parcels.wrap(restaurant));
//                context.startActivity(intent);
//            }

//                if (atr.picked) {
//                    cdAttraction.setBackgroundColor(Color.BLUE);
//                } else {
//                    cdAttraction.setBackgroundColor(Color.WHITE);
//                }
            // notifyItemChanged(position);
        }
    }

}
