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

import static com.example.alertless.utils.Constants.ID;
import static com.example.alertless.utils.Constants.PARTY_ID_FK;
import static com.example.alertless.utils.Constants.SCHEDULE_TABLE;
import static com.example.alertless.utils.Constants.TIME_RANGE_FK;

@Getter
@Setter
@AllArgsConstructor
@ToString
@Builder
@EqualsAndHashCode
@Entity(tableName = SCHEDULE_TABLE,
        foreignKeys = {
                @ForeignKey(entity = TimeRangeEntity.class,
                        parentColumns = ID,
                        childColumns = TIME_RANGE_FK),
                @ForeignKey(entity = PartyEntity.class,
                        parentColumns = ID,
                        childColumns = PARTY_ID_FK)
        },
        indices = {
                @Index(value = {TIME_RANGE_FK}),
                @Index(value = {PARTY_ID_FK})
        })
public class ScheduleEntity {

    @NonNull
    @PrimaryKey
    private String id;

    @NonNull
    @ColumnInfo(name = PARTY_ID_FK)
    private String partyId;

    @NonNull
    @ColumnInfo(name = TIME_RANGE_FK)
    private String timeRangeId;

    public ScheduleEntity() {
    }
}
