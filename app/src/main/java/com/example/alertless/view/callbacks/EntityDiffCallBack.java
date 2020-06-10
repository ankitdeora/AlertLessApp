package com.example.alertless.view.callbacks;

import androidx.recyclerview.widget.DiffUtil;
import com.example.alertless.entities.BaseEntity;

import java.util.List;

public class EntityDiffCallBack<T extends BaseEntity> extends DiffUtil.Callback {

    private final List<T> oldEntities;
    private final List<T> newEntities;

    public EntityDiffCallBack(List<T> oldEntities, List<T> newEntities) {
        this.oldEntities = oldEntities;
        this.newEntities = newEntities;
    }

    @Override
    public int getOldListSize() {
        return oldEntities.size();
    }

    @Override
    public int getNewListSize() {
        return newEntities.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldEntities.get(oldItemPosition).getId().equals(newEntities.get(newItemPosition).getId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldEntities.get(oldItemPosition).getModel().equals(newEntities.get(newItemPosition).getModel());
    }

}
