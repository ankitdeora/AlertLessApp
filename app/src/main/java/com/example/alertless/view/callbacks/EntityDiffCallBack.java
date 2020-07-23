package com.example.alertless.view.callbacks;

import androidx.recyclerview.widget.DiffUtil;
import com.example.alertless.entities.BaseEntity;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class EntityDiffCallBack<T, S> extends DiffUtil.Callback {

    private final List<T> oldEntities;
    private final List<T> newEntities;
    private final Function<T, S> idFunc;

    public EntityDiffCallBack(List<T> oldEntities, List<T> newEntities, Function<T, S> idFunc) {
        this.oldEntities = oldEntities;
        this.newEntities = newEntities;
        this.idFunc = idFunc;
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
        return idFunc.apply(oldEntities.get(oldItemPosition)).equals(idFunc.apply(newEntities.get(newItemPosition)));
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldEntities.get(oldItemPosition).equals(newEntities.get(newItemPosition));
    }
}
