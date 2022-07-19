package com.example.tripplanner.adapters;
import com.bumptech.glide.Glide;
import com.example.tripplanner.R;
import android.content.Context;
import android.content.Intent;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripplanner.activities.AtrDetailsActivity;
import com.example.tripplanner.models.Attraction;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import org.parceler.Parcels;

import java.text.DecimalFormat;
import java.util.List;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.ViewHolder> {
    private static final String TAG = "PlacesAdapter";
    private static final String noPhotoAvailableUrl = "https://archive.org/download/no-photo-available/no-photo-available.png";
    Context context;
    List<Attraction> attractionsList;

    public PlacesAdapter(Context context, List<Attraction> attractionsList) {
        this.context = context;
        this.attractionsList = attractionsList;
    }

    @NonNull
    @Override
    public PlacesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_attraction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlacesAdapter.ViewHolder holder, int position) {
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
        private final DecimalFormat decimalFormat = new DecimalFormat("0.00");
        MaterialCardView cdAttraction;
        ImageView ivAtrPicture;
        TextView tvName;
        TextView tvAddress;
        TextView tvDescription;
        TextView tvRating;
        MaterialButton btnLearnMore;


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
        }

        public void bind(Attraction attraction) {
            if (attraction.photo != null) {
                ivAtrPicture.setImageBitmap(attraction.photo);
            }
            if (ivAtrPicture.getDrawable() == null) {
                Glide.with(context)
                        .load(noPhotoAvailableUrl).into(ivAtrPicture);
            }

            tvName.setText(attraction.name);
            tvRating.setText(decimalFormat.format(attraction.rating));
            tvAddress.setText(attraction.formatted_address);
           // tvDescription.setText(attraction.website);
            cdAttraction.setOnClickListener(this);
            btnLearnMore.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Attraction atr = null;
            if (position != RecyclerView.NO_POSITION && v.getId() != R.id.btnLearnMore) {
                atr = attractionsList.get(position);
                Log.i(TAG, "clicked " + atr.name);
                atr.picked = !atr.picked;
                cdAttraction.toggle();
            }
            if (position != RecyclerView.NO_POSITION && v.getId() == R.id.btnLearnMore) {
                atr = attractionsList.get(position);
                Log.i("adapter", "Clicked Learn More about " + atr.name);
                Intent intent = new Intent(context, AtrDetailsActivity.class);
                intent.putExtra(Attraction.class.getSimpleName(), Parcels.wrap(atr));
                context.startActivity(intent);
            }

//                if (atr.picked) {
//                    cdAttraction.setBackgroundColor(Color.BLUE);
//                } else {
//                    cdAttraction.setBackgroundColor(Color.WHITE);
//                }
               // notifyItemChanged(position);
        }
    }
}
