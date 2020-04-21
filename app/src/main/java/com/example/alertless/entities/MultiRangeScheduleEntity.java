package com.example.alertless.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import static com.example.alertless.utils.Constants.DATE_RANGE_FK;
import static com.example.alertless.utils.Constants.DATE_SCHEDULE_ID;
import static com.example.alertless.utils.Constants.ID;
import static com.example.alertless.utils.Constants.MULTI_RANGE_SCHEDULE_TABLE;

@Getter
@Setter
@AllArgsConstructor
@ToString
@Builder
@EqualsAndHashCode
@Entity(tableName = MULTI_RANGE_SCHEDULE_TABLE,
        foreignKeys = {
                @ForeignKey(entity = ScheduleEntity.class,
                        parentColumns = ID,
                        childColumns = DATE_SCHEDULE_ID),
                @ForeignKey(entity = DateRangeEntity.class,
                        parentColumns = ID,
                        childColumns = DATE_RANGE_FK)
        },
        indices = {
                @Index(value = {DATE_SCHEDULE_ID}),
                @Index(value = {DATE_RANGE_FK})
        })
public class MultiRangeScheduleEntity {
    @NonNull
    @PrimaryKey
    private String id;

    @NonNull
    @ColumnInfo(name = DATE_SCHEDULE_ID)
    private String dateScheduleId;

    @NonNull
    @ColumnInfo(name = DATE_RANGE_FK)
    private String dateRangeId;

    public MultiRangeScheduleEntity() {
    }
}
