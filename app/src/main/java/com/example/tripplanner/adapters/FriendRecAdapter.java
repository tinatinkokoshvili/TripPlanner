package com.example.tripplanner.adapters;

import android.content.Context;
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
import com.example.tripplanner.models.User;

import java.util.List;

public class FriendRecAdapter extends RecyclerView.Adapter<FriendRecAdapter.ViewHolder>{
    private static final String TAG = "FriendRecAdapter";
    Context context;
    List<User> recFriendList;

    public FriendRecAdapter(Context context, List<User> recFriendList) {
        this.context = context;
        this.recFriendList = recFriendList;
    }

    @NonNull
    @Override
    public FriendRecAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_friend_recommendation, parent, false);
        return new FriendRecAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendRecAdapter.ViewHolder holder, int position) {
        User friendRec = recFriendList.get(position);
        Log.i(TAG, "Binding " + friendRec.fullName + " position " + position);
        holder.bind(friendRec);
    }

    @Override
    public int getItemCount() {
        return recFriendList.size();
    }

    public void clear() {
        recFriendList.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<User> list) {
        recFriendList.addAll(list);
        notifyDataSetChanged();
    }

    public void add(User friendRec) {
        recFriendList.add(friendRec);
        notifyItemInserted(recFriendList.size() - 1);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFriendPic;
        TextView tvFriendName;
        TextView tvFriendUserName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFriendPic = itemView.findViewById(R.id.ivFriendPic);
            tvFriendName = itemView.findViewById(R.id.tvFriendName);
            tvFriendUserName = itemView.findViewById(R.id.tvFriendName);
        }

        public void bind(User friendRec) {
            if (friendRec.getPicUrl() != null && !friendRec.getPicUrl().isEmpty()) {
                Glide.with(context).load(friendRec.getPicUrl()).into(ivFriendPic);
            }
            Log.i(TAG, "name " + friendRec.getFullName());
            tvFriendName.setText(friendRec.getFullName());
            tvFriendUserName.setText("@" + friendRec.getUsername());
        }
    }
}
