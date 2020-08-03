package com.example.alertless.utils;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.example.alertless.models.AppDetailsModel;
import com.example.alertless.view.caches.AppIconCache;

import java.util.List;
import java.util.stream.Collectors;

public class AppUtils {

    private static final String TAG = AppUtils.class.getName() + Constants.TAG_SUFFIX;

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

    public static List<AppDetailsModel> getUserAppsModel(PackageManager packageManager, AppIconCache appIconCache) {
        List<ApplicationInfo> userAppsInfo = getUserAppsInfo(packageManager);

        return userAppsInfo.stream()
                .map(appInfo -> {
                    String appName = String.valueOf(packageManager.getApplicationLabel(appInfo));
                    String packageName = appInfo.packageName;

                    // add icon to cache
                    addIconToCache(appIconCache, packageManager, packageName);

                    return AppDetailsModel.builder()
                            .appName(appName)
                            .packageName(packageName)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private static void addIconToCache(AppIconCache appIconCache, PackageManager packageManager, String packageName) {

        Drawable icon = appIconCache.getDrawableFromMemCache(packageName);

        if (icon == null) {
            try {

                icon = packageManager.getApplicationIcon(packageName);
                appIconCache.addDrawableToMemoryCache(packageName, icon);

            } catch (PackageManager.NameNotFoundException e) {
                Log.i(TAG, e.getMessage());
            }
        }
    }
}
