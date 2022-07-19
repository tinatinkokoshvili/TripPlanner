package com.example.tripplanner.adapters;
import com.bumptech.glide.Glide;
import com.example.tripplanner.R;
import android.content.Context;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripplanner.models.Attraction;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

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
        RatingBar rbAtrRating;

        ImageView ivDetailsPicture;
        ImageView ivDetailsIcon;
        TextView tvDetailsName;
        RatingBar rbDetailsRating;
        TextView tvDetailsRatingNum;
        TextView tvDetailsAddress;
        TextView tvDetailsPhoneNumber;
        TextView tvDetailsUrl;
        TextView tvDetailsWebsite;


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
            rbAtrRating = itemView.findViewById(R.id.rbAtrRating);


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
            float voteAverage = (float) attraction.rating;
            Log.i(TAG, attraction.name + voteAverage);
            rbAtrRating.setRating(voteAverage);
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
                // inflate the layout of the popup window
                LayoutInflater inflater = (LayoutInflater)
                        context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.details_popup, null);

                // create the popup window
                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true; // lets taps outside the popup also dismiss it
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                ivDetailsPicture = popupView.findViewById(R.id.ivDetailsPicture);
                ivDetailsIcon = popupView.findViewById(R.id.ivDetailsIcon);
                tvDetailsName = popupView.findViewById(R.id.tvDetailsName);

                rbDetailsRating = popupView.findViewById(R.id.rbDetailsRating);
                tvDetailsRatingNum = popupView.findViewById(R.id.tvDetailsRatingNum);
                tvDetailsAddress = popupView.findViewById(R.id.tvDetailsAddress);

                tvDetailsPhoneNumber = popupView.findViewById(R.id.tvDetailsPhoneNumber);
                tvDetailsUrl = popupView.findViewById(R.id.tvDetailsUrl);
                tvDetailsWebsite = popupView.findViewById(R.id.tvDetailsWebsite);

                if (atr.getPhoto() != null) {
                    ivDetailsPicture.setImageBitmap(atr.getPhoto());
                } else {
                    Glide.with(context)
                            .load(noPhotoAvailableUrl).into(ivAtrPicture);
                }
                if (atr.getIcon() != null && !atr.getIcon().isEmpty()) {
                    Glide.with(context).load(atr.getIcon()).into(ivDetailsIcon);
                }
                if (atr.getName() != null && !atr.getName().isEmpty()) {
                    tvDetailsName.setText(atr.getName());
                }
                float voteAverage = (float) atr.getRating();
                Log.i(TAG, atr.getName() + voteAverage);
                rbDetailsRating.setRating(voteAverage);
                tvDetailsRatingNum.setText(decimalFormat.format(atr.getRating()));

                if (atr.getFormattedAddress() != null && !atr.getFormattedAddress().isEmpty())
                    tvDetailsAddress.setText(atr.getFormattedAddress());
                if (atr.getFormattedPhoneNumber() != null && !atr.getFormattedPhoneNumber().isEmpty())
                    tvDetailsPhoneNumber.setText(atr.getFormattedPhoneNumber());
                if (atr.getUrl() != null && !atr.getUrl().isEmpty())
                    tvDetailsUrl.setText(atr.getUrl());
                if (atr.getWebsite() != null && !atr.getWebsite().isEmpty())
                    tvDetailsWebsite.setText(atr.getWebsite());

                // show the popup window
                // which view you pass in doesn't matter, it is only used for the window tolken
                popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

                // dismiss the popup window when touched
                popupView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        popupWindow.dismiss();
                        return true;
                    }
                });
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
