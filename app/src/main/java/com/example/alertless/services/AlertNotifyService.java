package com.example.alertless.services;

import android.content.Context;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.example.alertless.database.repositories.ProfileRepository;
import com.example.alertless.entities.ProfileDetailsEntity;
import com.example.alertless.exceptions.AlertlessDatabaseException;
import com.example.alertless.models.AppDetailsModel;
import com.example.alertless.models.Profile;
import com.example.alertless.models.ScheduleModel;
import com.example.alertless.utils.Constants;
import com.example.alertless.utils.ToastUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class AlertNotifyService extends NotificationListenerService {
    private static final String TAG = AlertNotifyService.class.getName() + Constants.TAG_SUFFIX;

    Context context;
    ProfileRepository profileRepository;
    Map<String, Integer> silentedMap;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        silentedMap = new HashMap<>();

        profileRepository = ProfileRepository.getInstance(getApplication());
        profileRepository.getActiveProfiles().observeForever(activeProfiles -> Optional.ofNullable(activeProfiles).ifPresent(activeProfilesList -> {
            String toastMsg = "Active Profiles : " + activeProfiles.size();
            Log.i(TAG, toastMsg);
            Log.i(TAG, silentedMap.toString());
            ToastUtils.showToast(getApplicationContext(), silentedMap.toString());
        }));
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String packageName = sbn.getPackageName();

        Map<String, Set<Profile>> packageProfilesMap = profileRepository.getPackageProfilesMap();
        Set<Profile> profiles = null;
        List<ScheduleModel> schedules;

        if (packageProfilesMap != null) {
            profiles = packageProfilesMap.get(packageName);
        }

        if (profiles != null) {

            schedules = profiles.stream()
                    .map(Profile::getSchedule)
                    .collect(Collectors.toList());
        } else {
            List<ProfileDetailsEntity> activeProfiles = profileRepository.getActiveProfiles().getValue();

            if (activeProfiles == null) {
                return;
            }

            schedules = activeProfiles.stream()
                    .map(profileDetails -> {
                        try {
                            String profileName = profileDetails.getName();
                            List<AppDetailsModel> profileApps = profileRepository.getProfileApps(profileName);

                            for (AppDetailsModel app : profileApps) {
                                if (packageName.equalsIgnoreCase(app.getPackageName())) {
                                    return profileRepository.getSchedule(profileName);
                                }
                            }

                            return null;
                        } catch (AlertlessDatabaseException e) {
                            Log.i(TAG, e.getMessage());
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

        }

        checkScheduleAndCancelNotification(schedules, sbn);

        /*
        int id = sbn.getId();
        String key = sbn.getKey();

        Bundle extras = sbn.getNotification().extras;
        text = extras.getCharSequence("android.text");
        title = extras.getString("android.title");

         */
    }

    private void checkScheduleAndCancelNotification(Collection<ScheduleModel> schedules, StatusBarNotification sbn) {
        for (ScheduleModel schedule : schedules) {

            if (schedule.isActive()) {
                String key = sbn.getKey();
                String packageName = sbn.getPackageName();

                // cancel notification
                this.cancelNotification(key);

                updateSilentMap(packageName);
                ToastUtils.showToast(context, "Cancelled notification for app : " + packageName);
                return;
            }
        }
    }

    private void updateSilentMap(String packageName) {
        Integer count = silentedMap.get(packageName);

        if (count == null) {
            silentedMap.put(packageName, 1);
        } else {
            silentedMap.put(packageName, count + 1);
        }
    }

}
