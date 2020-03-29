package com.example.alertless.activities;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.alertless.R;
import com.example.alertless.utils.ToastUtils;

import java.util.List;
import java.util.stream.Collectors;

public class AppSelectorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_selector);
    }

    public void listApps(View view) {
        final PackageManager pm = getPackageManager();

        // Flags: See below
        int flags = PackageManager.GET_META_DATA |
                PackageManager.GET_SHARED_LIBRARY_FILES |
                PackageManager.MATCH_UNINSTALLED_PACKAGES;

        //get a list of installed apps.
        List<ApplicationInfo> userApps = pm.getInstalledApplications(flags).stream()
                                            .filter(appInfo -> (appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 1)
                                            .collect(Collectors.toList());

        ApplicationInfo firstApp = userApps.get(0);
        String packageLabel = String.valueOf(pm.getApplicationLabel(firstApp)); //Package label(app name)
        int size = userApps.size();
//        firstApp.metaData.


        String msg = String.format("First App name : %s Size : %s", packageLabel, size);
        ToastUtils.showToast(getApplicationContext(), msg);

//        for (ApplicationInfo packageInfo : packages) {

//            Log.d(TAG, "Installed package :" + packageInfo.packageName);
//            Log.d(TAG, "Source dir : " + packageInfo.sourceDir);
//            Log.d(TAG, "Launch Activity :" + pm.getLaunchIntentForPackage(packageInfo.packageName));
//        }
    }
}
