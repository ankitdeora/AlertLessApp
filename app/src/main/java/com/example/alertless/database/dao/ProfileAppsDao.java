package com.example.alertless.database.dao;

import android.util.Log;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.alertless.database.AppDatabase;
import com.example.alertless.entities.AppDetailsEntity;
import com.example.alertless.entities.relations.ProfileAppRelation;
import com.example.alertless.models.AppDetailsModel;
import com.example.alertless.models.BaseModel;
import com.example.alertless.utils.StringUtils;
import com.example.alertless.utils.ValidationUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Dao
public abstract class ProfileAppsDao extends BaseDao<ProfileAppRelation, BaseModel> {

    private final AppDetailsDao appDetailsDao;

    public ProfileAppsDao(AppDatabase appDatabase) {
        this.appDetailsDao = appDatabase.getAppDetailsDao();
    }

    @Override
    @Query("SELECT * FROM profile_apps")
    public abstract List<ProfileAppRelation> findAllEntities();

    @Override
    @Query("SELECT * FROM profile_apps WHERE id = :id")
    public abstract ProfileAppRelation findEntity(String id);

    @Query("SELECT * FROM profile_apps WHERE profile_id = :profileId AND app_id = :appId")
    public abstract ProfileAppRelation findProfileAppRelation(String profileId, String appId);

    @Query("SELECT * FROM profile_apps WHERE profile_id = :profileId")
    public abstract List<ProfileAppRelation> findProfileApps(String profileId);

    @Query("SELECT * FROM profile_apps WHERE app_id = :appId")
    public abstract List<ProfileAppRelation> findRelationForAppId(String appId);

    @Transaction
    public List<ProfileAppRelation> createOrUpdateProfileApps(String profileId, List<AppDetailsModel> apps) {
        ValidationUtils.validateInput(profileId);
        ValidationUtils.validateInput(apps);

        List<ProfileAppRelation> existingRelations = findProfileApps(profileId);

        if (existingRelations != null) {

            if (isChangeNotRequired(existingRelations, apps)) {
                Log.i("ProfileAppsDao-TAG", "No change in apps required for : " + profileId);
                return existingRelations;
            }

            this.cascadeDelete(profileId);
        }

        List<ProfileAppRelation> result = new ArrayList<>();

        for (AppDetailsModel appModel : apps) {
            AppDetailsEntity appEntity = this.appDetailsDao.findOrCreateEntity(appModel);

            ProfileAppRelation relation = ProfileAppRelation.builder()
                    .id(StringUtils.getUniqueId())
                    .profileId(profileId)
                    .appId(appEntity.getId())
                    .build();

            this.insert(relation);
            result.add(relation);
        }

        return result;
    }

    private boolean isChangeNotRequired(List<ProfileAppRelation> existingRelations, List<AppDetailsModel> requestedApps) {
        List<AppDetailsModel> existingApps = getAppModelsFromRelations(existingRelations);

        Set<AppDetailsModel> existingAppsSet = new HashSet<>(existingApps);
        Set<AppDetailsModel> requestedAppsSet = new HashSet<>(requestedApps);

        return existingAppsSet.equals(requestedAppsSet);
    }

    @Transaction
    public List<AppDetailsModel> getProfileSilentApps(String profileId) {
        ValidationUtils.validateInput(profileId);

        List<ProfileAppRelation> relations = findProfileApps(profileId);

        List<AppDetailsModel> appModels = null;

        if (relations != null) {
            appModels = getAppModelsFromRelations(relations);
        }

        return appModels;
    }

    private List<AppDetailsModel> getAppModelsFromRelations(List<ProfileAppRelation> relations) {
        ValidationUtils.validateInput(relations);

        return relations.stream()
                .map(ProfileAppRelation::getAppId)
                .map(this.appDetailsDao::findEntity)
                .map(AppDetailsEntity::getModel)
                .collect(Collectors.toList());
    }

    @Transaction
    public void cascadeDelete(String profileId) {
        ValidationUtils.validateInput(profileId);

        List<ProfileAppRelation> relations = findProfileApps(profileId);

        relations.forEach(relation -> {
                    this.delete(relation);

                    if (!isAppReferredByOther(relation)) {
                        this.appDetailsDao.delete(relation.getAppId());
                    }
        });

    }

    private boolean isAppReferredByOther(ProfileAppRelation relation) {
        ValidationUtils.validateInput(relation);

        String appId = relation.getAppId();
        String profileId = relation.getProfileId();

        List<ProfileAppRelation> relationsForAppId = findRelationForAppId(appId);
        return relationsForAppId != null && !relationsForAppId.isEmpty() && (relationsForAppId.size() > 1 || !relationsForAppId.get(0).getProfileId().equals(profileId));
    }
}
