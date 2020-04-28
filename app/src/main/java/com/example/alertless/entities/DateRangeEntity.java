package com.example.alertless.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import static com.example.alertless.utils.Constants.DATE_RANGE_TABLE;
import static com.example.alertless.utils.Constants.END_DATE_MS_COL;
import static com.example.alertless.utils.Constants.START_DATE_MS_COL;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
@Builder
@EqualsAndHashCode
@Entity(tableName = DATE_RANGE_TABLE,
        indices = {@Index(value = {START_DATE_MS_COL, END_DATE_MS_COL}, unique = true)})
public class DateRangeEntity {
    @NonNull
    @PrimaryKey
    private String id;

    @ColumnInfo(name = START_DATE_MS_COL)
    private long startDateMs;

    @ColumnInfo(name = END_DATE_MS_COL)
    private long endDateMs;

    public DateRangeEntity() {
    }
}
