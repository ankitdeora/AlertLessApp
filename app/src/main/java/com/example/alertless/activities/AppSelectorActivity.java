package com.example.alertless.activities;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alertless.R;
import com.example.alertless.async.tasks.AppFetchTask;
import com.example.alertless.models.AppDetailsModel;
import com.example.alertless.utils.Constants;
import com.example.alertless.utils.ToastUtils;
import com.example.alertless.view.adapters.AppListAdapter;

import java.util.Set;

public class AppSelectorActivity extends AppCompatActivity {

    private static final String TAG = AppSelectorActivity.class.getName() + Constants.TAG_SUFFIX;

    private PackageManager packageManager;
    private AppListAdapter appListAdapter;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_selector);

        initStates();
        new AppFetchTask(this, recyclerView, progressBar, appListAdapter).execute();
    }

    private void initStates() {
        progressBar = findViewById(R.id.progressBarCyclic);
        appListAdapter = new AppListAdapter(this, getApplication());
        setRecyclerView();

        packageManager = getPackageManager();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.save_app_btn) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setRecyclerView() {
        recyclerView = findViewById(R.id.appRecyclerview);
        recyclerView.setAdapter(appListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void listApps(View view) {

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

