package com.example.alertless.view.adapters;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alertless.R;
import com.example.alertless.activities.ProfileEditActivity;
import com.example.alertless.database.repositories.ProfileDetailsRepository;
import com.example.alertless.entities.ProfileDetailsEntity;
import com.example.alertless.exceptions.AlertlessException;
import com.example.alertless.models.Profile;
import com.example.alertless.models.ProfileDetailsModel;
import com.example.alertless.utils.Constants;
import com.example.alertless.utils.ToastUtils;
import com.example.alertless.view.callbacks.ProfileDiffCallBack;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ProfileListAdapter extends RecyclerView.Adapter<ProfileListAdapter.ProfileViewHolder> {
    private static final String TAG = ProfileListAdapter.class.getName() + Constants.TAG_SUFFIX;
    private static final String ENABLED = "Enabled";
    private static final String DISABLED = "Disabled";

    class ProfileViewHolder extends RecyclerView.ViewHolder {
        private final TextView profileItemView;
        private final Switch profileItemSwitch;

        private ProfileViewHolder(View itemView) {
            super(itemView);
            profileItemView = itemView.findViewById(R.id.profileTextView);
            profileItemSwitch = itemView.findViewById(R.id.profileSwitch);

            profileItemView.setClickable(true);
            profileItemView.setOnClickListener(getProfileOnClickListener());
            profileItemSwitch.setOnCheckedChangeListener(getSwitchChangeListener());
        }

        private View.OnClickListener getProfileOnClickListener() {
            return v -> {
                final CharSequence profileName = ((TextView) v).getText();
                ProfileDetailsEntity profileDetails = mProfileMap.get(profileName);
                ProfileDetailsModel detailsModel = profileDetails.getModel();

                Profile profile = Profile.builder()
                                    .details(detailsModel)
                                    .build();

                Intent intent = new Intent(mContext, ProfileEditActivity.class);
                intent.putExtra(Constants.CURRENT_PROFILE, profile);

                Activity parentActivity = (Activity) mContext;
                parentActivity.startActivity(intent);
            };
        }

        private CompoundButton.OnCheckedChangeListener getSwitchChangeListener() {
            return (buttonView, isChecked) -> {

                final CharSequence profileName = profileItemView.getText();
                ProfileDetailsEntity clickedProfile = mProfileMap.get(profileName);

                if (clickedProfile.isActive() == isChecked) {
                    return;
                }

                clickedProfile.setActive(isChecked);
                String updateAction = isChecked ? ENABLED : DISABLED;
                // Update in DB
                try {

                    profileDetailsRepository.updateProfileDetails(profileName, isChecked);
                    String updateMsg = String.format("Profile : %s %s", profileName, updateAction);
                    ToastUtils.showToast(mContext, updateMsg);
                } catch (AlertlessException e) {
                    Log.e(TAG, e.getMessage(), e);
                    ToastUtils.showToast(mContext, e.getMessage());
                }

            };
        }
    }

    private ProfileDetailsRepository profileDetailsRepository;
    private final LayoutInflater mInflater;
    private List<ProfileDetailsEntity> mProfileDetails; // Cached copy of profiles
    private Map<CharSequence, ProfileDetailsEntity> mProfileMap;
    private Context mContext;

    public ProfileListAdapter(Context context, Application application) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        // Init userRepository
        profileDetailsRepository = ProfileDetailsRepository.getInstance(application);
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

            ProfileDetailsEntity current = mProfileDetails.get(position);
            holder.profileItemView.setText(current.getName());
            holder.profileItemSwitch.setChecked(current.isActive());

        } else {
            // Covers the case of data not being ready yet.
            holder.profileItemView.setText("No Profiles found");
        }
    }

    public void setProfileDetails(List<ProfileDetailsEntity> newProfileDetails){

        if (this.mProfileDetails == null) {
            updateDataset(newProfileDetails);
            notifyDataSetChanged();
            return;
        }

        final ProfileDiffCallBack diffCallBack = new ProfileDiffCallBack(this.mProfileDetails, newProfileDetails);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallBack);

        updateDataset(newProfileDetails);
        diffResult.dispatchUpdatesTo(this);

    }

    private void updateDataset(List<ProfileDetailsEntity> newProfiles) {
        this.mProfileDetails = newProfiles;
        populateProfileMap();
    }

    private void populateProfileMap() {
        if (mProfileDetails != null) {

            if (mProfileMap == null) {
                mProfileMap = new HashMap<>();
            }

            for (ProfileDetailsEntity profileDetails : mProfileDetails) {
                mProfileMap.put(profileDetails.getName(), profileDetails);
            }
        }
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
