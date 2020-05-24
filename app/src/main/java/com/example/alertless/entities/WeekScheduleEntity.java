package com.example.alertless.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.alertless.models.BaseModel;
import com.example.alertless.models.WeekScheduleDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import static com.example.alertless.utils.Constants.DATE_RANGE_FK;
import static com.example.alertless.utils.Constants.ID;
import static com.example.alertless.utils.Constants.WEEKDAYS_COL;
import static com.example.alertless.utils.Constants.WEEK_SCHEDULE_ID;
import static com.example.alertless.utils.Constants.WEEK_SCHEDULE_TABLE;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
@Builder
@EqualsAndHashCode
@Entity(tableName = WEEK_SCHEDULE_TABLE,
        foreignKeys = {
                        @ForeignKey(entity = PartyEntity.class,
                                    parentColumns = ID,
                                    childColumns = WEEK_SCHEDULE_ID,
                                    onUpdate = ForeignKey.CASCADE),
                        @ForeignKey(entity = DateRangeEntity.class,
                                    parentColumns = ID,
                                    childColumns = DATE_RANGE_FK,
                                    onUpdate = ForeignKey.CASCADE)
                    },
        indices = {@Index(value = {DATE_RANGE_FK})})
public class WeekScheduleEntity implements BaseEntity {
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = WEEK_SCHEDULE_ID)
    private String weekScheduleId;

    @ColumnInfo(name = WEEKDAYS_COL)
    private byte weekdays;

    @NonNull
    @ColumnInfo(name = DATE_RANGE_FK)
    private String dateRangeId;

    public WeekScheduleEntity() {
    }

    @Override
    public String getId() {
        return weekScheduleId;
    }

    @Override
    public WeekScheduleDTO getModel() {

        return WeekScheduleDTO.builder()
                    .weekdays(this.weekdays)
                    .dateRangeId(this.dateRangeId)
                .build();
    }
}
