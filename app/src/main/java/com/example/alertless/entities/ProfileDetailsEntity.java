package com.example.alertless.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.alertless.models.ProfileDetailsModel;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import static com.example.alertless.utils.Constants.ACTIVE_COL;
import static com.example.alertless.utils.Constants.PROFILE_DETAILS_TABLE;
import static com.example.alertless.utils.Constants.PROFILE_NAME_COL;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
@Builder
@EqualsAndHashCode (callSuper = false)
@Entity(tableName = PROFILE_DETAILS_TABLE,
        indices = {@Index(value = {PROFILE_NAME_COL}, unique = true)})
public class ProfileDetailsEntity implements BaseEntity, Serializable {

    @NonNull
    @PrimaryKey
    private String id;

    @NonNull
    @ColumnInfo(name = PROFILE_NAME_COL)
    private String name;

    @ColumnInfo(name = ACTIVE_COL)
    private boolean active;

    public ProfileDetailsEntity(String name, boolean active) {
        this.name = name;
        this.active = active;
    }

    @Override
    public ProfileDetailsModel getModel() {
        return ProfileDetailsModel.builder()
                    .name(this.name)
                    .active(this.active)
                .build();
    }
}
