package com.example.alertless.view.adapters;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alertless.R;
import com.example.alertless.database.repositories.ProfileRepository;
import com.example.alertless.exceptions.AlertlessDatabaseException;
import com.example.alertless.models.AppDetailsModel;
import com.example.alertless.utils.Constants;
import com.example.alertless.utils.ToastUtils;
import com.example.alertless.view.caches.AppIconCache;
import com.example.alertless.view.callbacks.EntityDiffCallBack;

import java.util.List;

public class SilentAppListAdapter extends RecyclerView.Adapter<SilentAppListAdapter.SilentAppViewHolder> {
    private static final String TAG = SilentAppListAdapter.class.getName() + Constants.TAG_SUFFIX;

    class SilentAppViewHolder extends RecyclerView.ViewHolder {

        private String packageName;
        private final ImageView appIcon;
        private final TextView appTextView;
        private final ImageView removeIcon;

        // TODO : add icon for Apps
        private SilentAppViewHolder(View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.silent_app_image);
            appTextView = itemView.findViewById(R.id.recycler_item_text_view);
            removeIcon = itemView.findViewById(R.id.silent_app_remove);

            appTextView.setClickable(true);
            removeIcon.setOnClickListener(getRemoveIconClickListener());
        }

        private View.OnClickListener getRemoveIconClickListener() {
            return view -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    AppDetailsModel clickedModel = mSilentApps.get(pos);

                    try {
                        profileRepository.removeProfileApps(mProfileName, clickedModel);
                    } catch (AlertlessDatabaseException e) {
                        Log.i(TAG, e.getMessage());
                        ToastUtils.showToast(mContext, e.getMessage());
                    }

                } else {
                    String msg = String.format("Clicked position : %s is invalid !!!", pos);
                    ToastUtils.showToast(mContext, msg);
                }
            };
        }
    }

    private String mProfileName;
    private final Context mContext;
    private final LayoutInflater mInflater;
    private List<AppDetailsModel> mSilentApps;
    private final ProfileRepository profileRepository;
    private final AppIconCache appIconCache;

    public SilentAppListAdapter(Context context, Application application, String profileName) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        profileRepository = ProfileRepository.getInstance(application);
        this.mProfileName = profileName;
        this.appIconCache = AppIconCache.getInstance();
    }

    @NonNull
    @Override
    public SilentAppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.silent_apps_recyclerview_item, parent, false);
        return new SilentAppViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SilentAppViewHolder holder, int position) {
        if (mSilentApps != null) {

            AppDetailsModel current = mSilentApps.get(position);
            String packageName = current.getPackageName();

            try {
                Drawable icon = appIconCache.getDrawableFromMemCache(packageName);

                if (icon == null) {
                    // add to cache
                    icon = this.mContext.getPackageManager().getApplicationIcon(packageName);
                    appIconCache.addDrawableToMemoryCache(packageName, icon);
                }

                holder.appIcon.setImageDrawable(icon);
            } catch (PackageManager.NameNotFoundException e) {
                Log.i(TAG, e.getMessage());
            }

            holder.appTextView.setText(current.getAppName());
            holder.packageName = packageName;
        } else {
            // Covers the case of data not being ready yet.
            holder.appTextView.setText("No Apps found");
        }
    }

    public void setSilentApps(List<AppDetailsModel> newApps) {
        if (this.mSilentApps == null) {
            updateDataset(newApps);
            notifyDataSetChanged();
            return;
        }

        final EntityDiffCallBack<AppDetailsModel, String> diffCallBack =
                new EntityDiffCallBack<>(this.mSilentApps, newApps, AppDetailsModel::getPackageName);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallBack);

        updateDataset(newApps);
        diffResult.dispatchUpdatesTo(this);
    }

    private void updateDataset(List<AppDetailsModel> newSilentApps) {
        this.mSilentApps = newSilentApps;
    }

    @Override
    public int getItemCount() {
        if (mSilentApps != null) {
            return mSilentApps.size();
        } else {
            return 0;
        }
    }

//    @Override
//    public int getItemViewType(int position) {
//        return position;
//    }

}