package com.example.alertless.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import static com.example.alertless.utils.Constants.APP_DETAILS_TABLE;
import static com.example.alertless.utils.Constants.APP_NAME_COL;
import static com.example.alertless.utils.Constants.PACKAGE_NAME_COL;

@Getter
@Setter
@AllArgsConstructor
@ToString
@Builder
@EqualsAndHashCode
@Entity(tableName = APP_DETAILS_TABLE,
        indices = {@Index(value = {PACKAGE_NAME_COL}, unique = true)})
public class AppDetailsEntity implements Serializable {
    @NonNull
    @PrimaryKey
    private String id;

    @NonNull
    @ColumnInfo(name = APP_NAME_COL)
    private String appName;

    @NonNull
    @ColumnInfo(name = PACKAGE_NAME_COL)
    private String packageName;

    public AppDetailsEntity() {
    }
}
