package com.example.alertless.view.callbacks;

import androidx.recyclerview.widget.DiffUtil;

import com.example.alertless.entities.ProfileDetailsEntity;

import java.util.List;

public class ProfileDiffCallBack extends DiffUtil.Callback {

    private final List<ProfileDetailsEntity> oldProfiles;
    private final List<ProfileDetailsEntity> newProfiles;

    public ProfileDiffCallBack(List<ProfileDetailsEntity> oldProfiles, List<ProfileDetailsEntity> newProfiles) {
        this.oldProfiles = oldProfiles;
        this.newProfiles = newProfiles;
    }

    @Override
    public int getOldListSize() {
        return oldProfiles.size();
    }

    @Override
    public int getNewListSize() {
        return newProfiles.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldProfiles.get(oldItemPosition).getId().equals(newProfiles.get(newItemPosition).getId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldProfiles.get(oldItemPosition).getModel().equals(newProfiles.get(newItemPosition).getModel());
    }

}
