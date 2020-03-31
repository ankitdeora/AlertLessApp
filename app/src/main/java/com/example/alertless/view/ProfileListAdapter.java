package com.example.alertless.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alertless.R;
import com.example.alertless.entities.ProfileDetails;

import java.util.List;

public class ProfileListAdapter extends RecyclerView.Adapter<ProfileListAdapter.ProfileViewHolder> {


    class ProfileViewHolder extends RecyclerView.ViewHolder {
        private final TextView profileItemView;

        private ProfileViewHolder(View itemView) {
            super(itemView);
            profileItemView = itemView.findViewById(R.id.profileTextView);
        }
    }

    private final LayoutInflater mInflater;
    private List<ProfileDetails> mProfileDetails; // Cached copy of profiles

    public ProfileListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.profile_recyclerview_item, parent, false);
        return new ProfileViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        if (mProfileDetails != null) {
            ProfileDetails current = mProfileDetails.get(position);
            holder.profileItemView.setText(current.getName());
        } else {
            // Covers the case of data not being ready yet.
            holder.profileItemView.setText("No Profiles found");
        }
    }

    public void setProfileDetails(List<ProfileDetails> profileDetails){
        mProfileDetails = profileDetails;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mProfileDetails != null) {
            return mProfileDetails.size();
        } else {
            return 0;
        }
    }


}
