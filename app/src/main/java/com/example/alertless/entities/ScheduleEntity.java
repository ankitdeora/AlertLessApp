package com.example.alertless.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.alertless.commons.ScheduleType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import static com.example.alertless.utils.Constants.ID;
import static com.example.alertless.utils.Constants.SCHEDULE_TABLE;
import static com.example.alertless.utils.Constants.SCHEDULE_TYPE_COL;
import static com.example.alertless.utils.Constants.TIME_RANGE_FK;

@Getter
@Setter
@AllArgsConstructor
@ToString
@Builder
@EqualsAndHashCode
@Entity(tableName = SCHEDULE_TABLE,
        foreignKeys = @ForeignKey(entity = TimeRangeEntity.class,
                parentColumns = ID,
                childColumns = TIME_RANGE_FK),
        indices = {@Index(value = {TIME_RANGE_FK})})
public class ScheduleEntity {

    @NonNull
    @PrimaryKey
    private String id;

    @NonNull
    @ColumnInfo(name = SCHEDULE_TYPE_COL)
    private String scheduleType;

    @NonNull
    @ColumnInfo(name = TIME_RANGE_FK)
    private String timeRangeId;

    public ScheduleEntity() {
    }
}
