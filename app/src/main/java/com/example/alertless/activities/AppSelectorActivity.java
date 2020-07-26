package com.example.alertless.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alertless.R;
import com.example.alertless.async.tasks.AppFetchTask;
import com.example.alertless.database.repositories.ProfileRepository;
import com.example.alertless.exceptions.AlertlessDatabaseException;
import com.example.alertless.models.AppDetailsModel;
import com.example.alertless.models.Profile;
import com.example.alertless.utils.ActivityUtils;
import com.example.alertless.utils.Constants;
import com.example.alertless.utils.StringUtils;
import com.example.alertless.utils.ToastUtils;
import com.example.alertless.view.adapters.AppListAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class AppSelectorActivity extends AppCompatActivity {

    private static final String TAG = AppSelectorActivity.class.getName() + Constants.TAG_SUFFIX;
    private ProfileRepository profileRepository = ProfileRepository.getInstance(getApplication());

    private AppListAdapter appListAdapter;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private Profile currentProfile;
    private List<AppDetailsModel> allUserApps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_selector);

        try {
            initStates();
            Set<AppDetailsModel> enabledAppsSet = this.currentProfile.getApps() != null ?
                                                        new HashSet<>(this.currentProfile.getApps()) :
                                                        null;

            AppFetchTask appFetchTask = new AppFetchTask(this, recyclerView, progressBar, appListAdapter, enabledAppsSet);
            allUserApps = appFetchTask.execute().get();

            String msg = String.format("Got user Apps : %s", allUserApps.size());
            ToastUtils.showToast(getApplication(), msg);
            Log.i(TAG, msg);

        } catch (AlertlessDatabaseException | InterruptedException | ExecutionException e) {
            Log.i(TAG, e.getMessage(), e);
            ActivityUtils.finishActivityWithErr(Constants.APP_SELECTOR_ERROR, e.getMessage(), this);
        }
    }

    private void initStates() throws AlertlessDatabaseException {
        progressBar = findViewById(R.id.progressBarCyclic);
        appListAdapter = new AppListAdapter(this, getApplication());
        setRecyclerView();

        this.currentProfile = (Profile) getIntent().getSerializableExtra(Constants.CURRENT_PROFILE);

        if (this.currentProfile == null) {
            String errMsg = "App Selector Activity cancelled as no Profile configured !!!";
            ActivityUtils.finishActivityWithErr(Constants.APP_SELECTOR_ERROR, errMsg, this);
        }

        List<AppDetailsModel> profileEnabledApps = this.profileRepository.getProfileApps(this.currentProfile.getDetails().getName());
        this.currentProfile.setApps(profileEnabledApps);

    }

    private void setRecyclerView() {
        recyclerView = findViewById(R.id.appRecyclerview);
        recyclerView.setAdapter(appListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search_app);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                if (allUserApps != null && !allUserApps.isEmpty()) {
                    final List<AppDetailsModel> filteredApps = filter(allUserApps, query);
                    appListAdapter.setApps(filteredApps);
                    return true;
                }
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private List<AppDetailsModel> filter(List<AppDetailsModel> allUserApps, String query) {
        return allUserApps.stream()
                .parallel()
                .filter(app -> app.getAppName().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.save_app_btn) {
            saveApps();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveApps() {
        listApps();
        Set<AppDetailsModel> enabledApps = this.appListAdapter.getEnabledApps();
        this.currentProfile.setApps(new ArrayList<>(enabledApps));

        try {
            profileRepository.createOrUpdateProfileApps(this.currentProfile);
        } catch (AlertlessDatabaseException e) {
            Log.i(TAG, e.getMessage(), e);
            ActivityUtils.finishActivityWithErr(Constants.APP_SELECTOR_ERROR, e.getMessage(), this);
        }

        Intent returnIntent = new Intent();
        returnIntent.putExtra(Constants.APP_SELECTOR_RESULT, (ArrayList) this.currentProfile.getApps());
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public void listApps() {

        Set<AppDetailsModel> enabledApps = appListAdapter.getEnabledApps();
        String msg = "";

        for (AppDetailsModel enabledApp : enabledApps) {
            msg = msg + " | " + enabledApp.getAppName() + " | ";
        }
        ToastUtils.showToast(getApplication(), msg);

//            Log.d(TAG, "Installed package :" + packageInfo.packageName);
//            Log.d(TAG, "Source dir : " + packageInfo.sourceDir);
//            Log.d(TAG, "Launch Activity :" + pm.getLaunchIntentForPackage(packageInfo.packageName));

    }
}

