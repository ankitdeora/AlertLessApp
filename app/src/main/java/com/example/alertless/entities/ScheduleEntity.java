package com.example.alertless.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.alertless.models.BaseModel;
import com.example.alertless.models.ScheduleDTO;

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
                        childColumns = TIME_RANGE_FK,
                        onUpdate = ForeignKey.CASCADE),
                @ForeignKey(entity = PartyEntity.class,
                        parentColumns = ID,
                        childColumns = PARTY_ID_FK,
                        onUpdate = ForeignKey.CASCADE)
        },
        indices = {
                @Index(value = {TIME_RANGE_FK}),
                @Index(value = {PARTY_ID_FK})
        })
public class ScheduleEntity implements BaseEntity {

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

    @Override
    public ScheduleDTO getModel() {
        return ScheduleDTO.builder()
                    .partyId(this.partyId)
                    .timeRangeId(this.timeRangeId)
                .build();

    }
}
