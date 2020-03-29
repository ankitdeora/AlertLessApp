package com.example.alertless.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.AllArgsConstructor;
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
@EqualsAndHashCode
@Entity(tableName = "profile_details")
public class ProfileDetails {

    @NonNull
    @PrimaryKey
    private String id;

    @NonNull
    @ColumnInfo(name = "profile_name")
    private String name;

    @ColumnInfo(name = "active")
    private boolean active;

    public ProfileDetails(String name, boolean active) {
        this.name = name;
        this.active = active;
    }
}
