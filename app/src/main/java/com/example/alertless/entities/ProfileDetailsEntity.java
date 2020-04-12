package com.example.alertless.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.alertless.models.ProfileDetailsModel;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
@Builder
@EqualsAndHashCode
@Entity(tableName = "profile_details", indices = {@Index(value = {"profile_name"}, unique = true)})
public class ProfileDetailsEntity implements Serializable {

    @NonNull
    @PrimaryKey
    private String id;

    @NonNull
    @ColumnInfo(name = "profile_name")
    private String name;

    @ColumnInfo(name = "active")
    private boolean active;

    public ProfileDetailsEntity(String name, boolean active) {
        this.name = name;
        this.active = active;
    }

    public static ProfileDetailsEntity getEntity(ProfileDetailsModel profileDetailsModel, String profileId) {

        if (profileDetailsModel == null) {
            return null;
        }

        return ProfileDetailsEntity.builder()
                    .id(profileId)
                    .name(profileDetailsModel.getName())
                    .active(profileDetailsModel.isActive())
                .build();
    }
}
