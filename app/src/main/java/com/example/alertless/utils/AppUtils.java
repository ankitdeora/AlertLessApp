package com.example.alertless.utils;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.example.alertless.models.AppDetailsModel;

import java.util.List;
import java.util.stream.Collectors;

public class AppUtils {

    private static List<ApplicationInfo> getUserAppsInfo(PackageManager packageManager) {

        int flags = PackageManager.GET_META_DATA |
                PackageManager.GET_SHARED_LIBRARY_FILES |
                PackageManager.MATCH_UNINSTALLED_PACKAGES;

        //get a list of installed apps.
        List<ApplicationInfo> userApps = packageManager.getInstalledApplications(flags).stream()
                .filter(appInfo -> (appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 1)
                .collect(Collectors.toList());

        return userApps;
    }

    public static List<AppDetailsModel> getUserAppsModel(PackageManager packageManager) {
        List<ApplicationInfo> userAppsInfo = getUserAppsInfo(packageManager);

        return userAppsInfo.stream()
                .map(appInfo -> {
                    String appName = String.valueOf(packageManager.getApplicationLabel(appInfo));
                    String packageName = appInfo.packageName;

                    return AppDetailsModel.builder()
                            .appName(appName)
                            .packageName(packageName)
                            .build();
                })
                .collect(Collectors.toList());

    }
}
