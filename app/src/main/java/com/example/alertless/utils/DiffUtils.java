package com.example.alertless.utils;

import com.example.alertless.entities.Identity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DiffUtils<T extends Identity> {

    private Map<String, T> oldItemsMap;
    private Map<String, T> newItemsMap;

    private List<T> added;
    private List<T> updated;
    private List<T> removed;

    public DiffUtils(List<T> oldList, List<T> newList) {
        Objects.requireNonNull(oldList);
        Objects.requireNonNull(newList);

        oldItemsMap = new HashMap<>();
        newItemsMap = new HashMap<>();

        added = new ArrayList<>();
        updated = new ArrayList<>();
        removed = new ArrayList<>();

        populateMap(oldList, oldItemsMap);
        populateMap(newList, newItemsMap);
    }

    private void populateMap(List<T> list, Map<String, T> map) {
        for (T item : list) {
            map.put(item.getId(), item);
        }
    }

    public void findDiff() {
        Iterator<Map.Entry<String, T>> iterator = oldItemsMap.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, T> entry = iterator.next();

            String key = entry.getKey();
            T oldValue = entry.getValue();
            T newValue = newItemsMap.get(key);

            if (newValue == null) {
                removed.add(oldValue);
            } else if (!newValue.equals(oldValue)) {
                updated.add(newValue);
            }

            iterator.remove();
            newItemsMap.remove(key);
        }

        added.addAll(newItemsMap.values());
    }

    public List<T> added() {
        return added;
    }

    public List<T> updated() {
        return updated;
    }

    public List<T> removed() {
        return removed;
    }
}
