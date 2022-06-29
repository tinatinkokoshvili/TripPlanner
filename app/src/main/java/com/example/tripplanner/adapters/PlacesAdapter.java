package com.example.tripplanner.adapters;
import com.example.tripplanner.R;
import android.content.Context;
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


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvVicinity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvVicinity = itemView.findViewById(R.id.tvVicinity);
        }

        public void bind(Attraction attraction) {
            tvName.setText(attraction.name);
            tvVicinity.setText(attraction.vicinity);
        }
    }
}
