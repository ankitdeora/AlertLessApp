package com.example.alertless.view.adapters;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alertless.R;
import com.example.alertless.activities.ProfileEditActivity;
import com.example.alertless.database.repositories.ProfileRepository;
import com.example.alertless.entities.ProfileDetailsEntity;
import com.example.alertless.exceptions.AlertlessException;
import com.example.alertless.models.Profile;
import com.example.alertless.models.ProfileDetailsModel;
import com.example.alertless.utils.Constants;
import com.example.alertless.utils.ToastUtils;
import com.example.alertless.view.callbacks.EntityDiffCallBack;

import java.util.ArrayList;
import java.util.List;

import static com.example.alertless.utils.Constants.DISABLED;
import static com.example.alertless.utils.Constants.ENABLED;

public class ProfileListAdapter extends RecyclerView.Adapter<ProfileListAdapter.ProfileViewHolder> {
    private static final String TAG = ProfileListAdapter.class.getName() + Constants.TAG_SUFFIX;

    class ProfileViewHolder extends RecyclerView.ViewHolder {
        private final TextView profileItemView;
        private final Switch profileItemSwitch;
        private final RelativeLayout itemLayout;
        private final ImageView editIcon;

        private ProfileViewHolder(View itemView) {
            super(itemView);
            profileItemView = itemView.findViewById(R.id.recycler_item_text_view);
            profileItemSwitch = itemView.findViewById(R.id.recycler_item_switch);
            itemLayout = itemView.findViewById(R.id.recycler_item_layout);
            editIcon = itemView.findViewById(R.id.edit_profile_btn);

            editIcon.setOnClickListener(getProfileOnClickListener());
            profileItemSwitch.setOnCheckedChangeListener(getSwitchChangeListener());
        }

        void selectItem(ProfileDetailsEntity item) {
            if (multiSelect) {
                if (selectedItems.contains(item)) {
                    selectedItems.remove(item);
                    itemLayout.setBackgroundColor(Color.WHITE);
                } else {
                    selectedItems.add(item);
                    itemLayout.setBackgroundColor(Color.LTGRAY);
                }
            }
        }

        void updateItem(ProfileDetailsEntity current) {
            profileItemView.setText(current.getName());
            profileItemSwitch.setChecked(current.isActive());

            if (selectedItems.contains(current)) {
                itemLayout.setBackgroundColor(Color.LTGRAY);
            } else {
                itemLayout.setBackgroundColor(Color.WHITE);
            }

            itemView.setOnLongClickListener(view -> {
                ((AppCompatActivity)view.getContext()).startSupportActionMode(actionModeCallbacks);
                selectItem(current);
                return true;
            });

            itemView.setOnClickListener(view -> selectItem(current));
        }

        private View.OnClickListener getProfileOnClickListener() {
            return v -> {
                int pos = getAdapterPosition();
                if(pos != RecyclerView.NO_POSITION){

                    ProfileDetailsEntity profileDetails = mProfileDetails.get(pos);
                    ProfileDetailsModel detailsModel = profileDetails.getModel();

                    Profile profile = Profile.builder()
                            .details(detailsModel)
                            .build();

                    Intent intent = new Intent(mContext, ProfileEditActivity.class);
                    intent.putExtra(Constants.CURRENT_PROFILE, profile);

                    Activity parentActivity = (Activity) mContext;
                    parentActivity.startActivity(intent);
                } else {
                    String msg = String.format("Clicked position : %s is invalid !!!", pos);
                    ToastUtils.showToast(mContext, msg);
                }
            };
        }

        private CompoundButton.OnCheckedChangeListener getSwitchChangeListener() {
            return (buttonView, isChecked) -> {

                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {

                    final CharSequence profileName = profileItemView.getText();
                    ProfileDetailsEntity clickedProfile = mProfileDetails.get(pos);

                    if (clickedProfile.isActive() == isChecked) {
                        return;
                    }

                    clickedProfile.setActive(isChecked);
                    String updateAction = isChecked ? ENABLED : DISABLED;
                    // Update in DB
                    try {

                        profileRepository.updateProfileDetails(profileName, isChecked);
                        String updateMsg = String.format("Profile : %s %s", profileName, updateAction);
                        ToastUtils.showToast(mContext, updateMsg);
                    } catch (AlertlessException e) {
                        Log.e(TAG, e.getMessage(), e);
                        ToastUtils.showToast(mContext, e.getMessage());
                    }

                } else {
                    String msg = String.format("Clicked position : %s is invalid !!!", pos);
                    ToastUtils.showToast(mContext, msg);
                }
            };
        }
    }

    private ActionMode.Callback actionModeCallbacks = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            multiSelect = true;
            mode.getMenuInflater().inflate(R.menu.long_click_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.delete_btn:
                    for (ProfileDetailsEntity profileDetailsEntity : selectedItems) {

                        try {
                            profileRepository.deleteProfile(profileDetailsEntity.getName());
                        } catch (AlertlessException e) {
                            Log.i(TAG, e.getMessage());
                            ToastUtils.showToast(mContext, e.getMessage());
                        }
                    }
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            multiSelect = false;
            selectedItems.clear();
            notifyDataSetChanged();
        }
    };

    private boolean multiSelect = false;
    private List<ProfileDetailsEntity> selectedItems = new ArrayList<>();

    private final Context mContext;
    private final LayoutInflater mInflater;
    private final ProfileRepository profileRepository;
    private List<ProfileDetailsEntity> mProfileDetails; // Cached copy of profiles

    public ProfileListAdapter(Context context, Application application) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        profileRepository = ProfileRepository.getInstance(application);
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new ProfileViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        if (mProfileDetails != null) {

            ProfileDetailsEntity current = mProfileDetails.get(position);
            holder.updateItem(current);

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

        final EntityDiffCallBack<ProfileDetailsEntity, String> diffCallBack =
                new EntityDiffCallBack<>(this.mProfileDetails, newProfileDetails, ProfileDetailsEntity::getId);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallBack);

        updateDataset(newProfileDetails);
        diffResult.dispatchUpdatesTo(this);

    }

    private void updateDataset(List<ProfileDetailsEntity> newProfiles) {
        this.mProfileDetails = newProfiles;
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
