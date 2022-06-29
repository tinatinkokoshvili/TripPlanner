package com.example.tripplanner.adapters;
import com.example.tripplanner.R;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripplanner.models.Attraction;

import java.util.List;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.ViewHolder> {
    private static final String TAG = "PlacesAdapter";
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
        TextView tvName;
        TextView tvVicinity;
        CardView cdAttraction;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvVicinity = itemView.findViewById(R.id.tvVicinity);
            cdAttraction = itemView.findViewById(R.id.cdAttraction);
            itemView.setOnClickListener(this);
        }

        public void bind(Attraction attraction) {
            tvName.setText(attraction.name);
            tvVicinity.setText(attraction.vicinity);
            if (attraction.picked) {
                cdAttraction.setBackgroundColor(Color.BLUE);
            } else {
                cdAttraction.setBackgroundColor(Color.WHITE);
            }
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Attraction atr = attractionsList.get(position);
                atr.picked = !atr.picked;
                Log.i("adapter", "clicked " + atr.name);
                if (atr.picked) {
                    cdAttraction.setBackgroundColor(Color.BLUE);
                } else {
                    cdAttraction.setBackgroundColor(Color.WHITE);
                }
               // notifyItemChanged(position);
//                Intent intent = new Intent(context, AtrDetailsActivity.class);
//                intent.putExtra(Attraction.class.getSimpleName(), Parcels.wrap(atr));
//                context.startActivity(intent);
            }
        }
    }
}
