package com.example.alertless.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.alertless.models.TimeRangeModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import static com.example.alertless.utils.Constants.END_MIN_COL;
import static com.example.alertless.utils.Constants.START_MIN_COL;
import static com.example.alertless.utils.Constants.TIME_RANGE_TABLE;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
@Builder
@EqualsAndHashCode
@Entity(tableName = TIME_RANGE_TABLE,
        indices = {@Index(value = {START_MIN_COL, END_MIN_COL}, unique = true)})
public class TimeRangeEntity extends BaseEntity {
    @NonNull
    @PrimaryKey
    private String id;

    @ColumnInfo(name = START_MIN_COL)
    private int startMin;

    @ColumnInfo(name = END_MIN_COL)
    private int endMin;

    public TimeRangeEntity() {
    }

    @Override
    public TimeRangeModel getModel() {
        return TimeRangeModel.builder()
                    .startMin(this.startMin)
                    .endMin(this.endMin)
                .build();
    }
}
