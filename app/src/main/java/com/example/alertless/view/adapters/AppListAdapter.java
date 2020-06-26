package com.example.alertless.view.adapters;

import android.app.Application;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alertless.R;
import com.example.alertless.models.AppDetailsModel;
import com.example.alertless.utils.Constants;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.AppViewHolder> {
    private static final String TAG = AppListAdapter.class.getName() + Constants.TAG_SUFFIX;

    class AppViewHolder extends RecyclerView.ViewHolder {

        private String packageName;
        private final TextView appTextView;
        private final Switch appItemSwitch;

        // TODO : add icon for Apps

        private AppViewHolder(View itemView) {
            super(itemView);
            appTextView = itemView.findViewById(R.id.recycler_item_text_view);
            appItemSwitch = itemView.findViewById(R.id.recycler_item_switch);

            appTextView.setClickable(true);
            appItemSwitch.setOnCheckedChangeListener(getSwitchChangeListener());
        }

        private CompoundButton.OnCheckedChangeListener getSwitchChangeListener() {
            return (buttonView, isChecked) -> {

                AppDetailsModel clickedModel = modelMap.get(packageName);

                if (isChecked && clickedModel != null) {
                    enabledApps.add(clickedModel);
                } else {
                    enabledApps.remove(clickedModel);
                }
            };
        }
    }

    private final Context mContext;
    private final LayoutInflater mInflater;
    private List<AppDetailsModel> mApps; // Cached copy of apps
    private Set<AppDetailsModel> enabledApps;
    private Map<String, AppDetailsModel> modelMap;

    public AppListAdapter(Context context, Application application) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.enabledApps = new HashSet<>();
    }

    public Set<AppDetailsModel> getEnabledApps() {
        return enabledApps;
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new AppViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        if (mApps != null) {

            AppDetailsModel current = mApps.get(position);
            holder.appTextView.setText(current.getAppName());

            boolean switchState = this.getEnabledApps().contains(current);

            holder.appItemSwitch.setChecked(switchState);
            holder.packageName = current.getPackageName();
        } else {
            // Covers the case of data not being ready yet.
            holder.appTextView.setText("No Apps found");
        }
    }

    public void setApps(List<AppDetailsModel> newApps, Set<AppDetailsModel> enabledApps){
            updateDataset(newApps);

            if (enabledApps != null) {
                this.enabledApps = enabledApps;
            }

            notifyDataSetChanged();
    }

    public void setApps(List<AppDetailsModel> newApps) {
        setApps(newApps, null);
    }

    private void updateDataset(List<AppDetailsModel> newApps) {
        this.mApps = newApps;
        populateModelMap();
    }

    private void populateModelMap() {
        if (this.mApps != null) {

            if (this.modelMap == null) {
                this.modelMap = new HashMap<>();
            }

            for (AppDetailsModel appModel : this.mApps) {
                this.modelMap.put(appModel.getPackageName(), appModel);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mApps != null) {
            return mApps.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}