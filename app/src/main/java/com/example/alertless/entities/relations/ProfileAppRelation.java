package com.example.alertless.entities.relations;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.alertless.entities.AppDetailsEntity;
import com.example.alertless.entities.Identity;
import com.example.alertless.entities.ProfileDetailsEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import static com.example.alertless.utils.Constants.APP_ID_FK;
import static com.example.alertless.utils.Constants.ID;
import static com.example.alertless.utils.Constants.PROFILE_APPS_RELATION_TABLE;
import static com.example.alertless.utils.Constants.PROFILE_ID_FK;

@Getter
@Setter
@AllArgsConstructor
@ToString
@Builder
@EqualsAndHashCode
@Entity(tableName = PROFILE_APPS_RELATION_TABLE,
        foreignKeys = {
                @ForeignKey(entity = ProfileDetailsEntity.class,
                        parentColumns = ID,
                        childColumns = PROFILE_ID_FK,
                        onUpdate = ForeignKey.CASCADE),
                @ForeignKey(entity = AppDetailsEntity.class,
                        parentColumns = ID,
                        childColumns = APP_ID_FK,
                        onUpdate = ForeignKey.CASCADE)
        },
        indices = {
                @Index(value = {PROFILE_ID_FK}),
                @Index(value = {APP_ID_FK})
        })
public class ProfileAppRelation implements Identity {

    @NonNull
    @PrimaryKey
    private String id;

    @NonNull
    @ColumnInfo(name = PROFILE_ID_FK)
    private String profileId;

    @NonNull
    @ColumnInfo(name = APP_ID_FK)
    private String appId;

    public ProfileAppRelation() {
    }
}
