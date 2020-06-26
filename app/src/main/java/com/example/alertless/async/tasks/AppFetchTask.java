package com.example.alertless.async.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.example.alertless.models.AppDetailsModel;
import com.example.alertless.utils.AppUtils;
import com.example.alertless.view.adapters.AppListAdapter;

import java.util.List;
import java.util.Set;

public class AppFetchTask extends AsyncTask<Void, Void, List<AppDetailsModel>> {

    private Context mContext;
    private View view;
    private ProgressBar spinner;
    private AppListAdapter appListAdapter;
    private Set<AppDetailsModel> enabledApps;

    public AppFetchTask(Context mContext, View view, ProgressBar spinner, AppListAdapter appListAdapter, Set<AppDetailsModel> enabledApps) {
        this.mContext = mContext;
        this.view = view;
        this.spinner = spinner;
        this.appListAdapter = appListAdapter;
        this.enabledApps = enabledApps;
    }

    @Override
    protected void onPreExecute() {
        spinner.setVisibility(View.VISIBLE);
        view.setVisibility(View.GONE);
        super.onPreExecute();
    }

    @Override
    protected List<AppDetailsModel> doInBackground(Void... voids) {
        return AppUtils.getUserAppsModel(mContext.getPackageManager());
    }

    @Override
    protected void onPostExecute(List<AppDetailsModel> userApps) {
        spinner.setVisibility(View.GONE);
        view.setVisibility(View.VISIBLE);
        appListAdapter.setApps(userApps, this.enabledApps);
        super.onPostExecute(userApps);
    }
}
