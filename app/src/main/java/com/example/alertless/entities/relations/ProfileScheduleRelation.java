package com.example.alertless.entities.relations;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.alertless.entities.ProfileDetailsEntity;
import com.example.alertless.entities.ScheduleEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import static com.example.alertless.utils.Constants.ID;
import static com.example.alertless.utils.Constants.PROFILE_ID_FK;
import static com.example.alertless.utils.Constants.PROFILE_SCHEDULE_RELATION_TABLE;
import static com.example.alertless.utils.Constants.SCHEDULE_ID_FK;

@Getter
@Setter
@AllArgsConstructor
@ToString
@Builder
@EqualsAndHashCode
@Entity(tableName = PROFILE_SCHEDULE_RELATION_TABLE,
        foreignKeys = {
                @ForeignKey(entity = ScheduleEntity.class,
                        parentColumns = ID,
                        childColumns = SCHEDULE_ID_FK),
                @ForeignKey(entity = ProfileDetailsEntity.class,
                        parentColumns = ID,
                        childColumns = PROFILE_ID_FK)
        },
        indices = {@Index(value = {SCHEDULE_ID_FK})})
public class ProfileScheduleRelation {
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = PROFILE_ID_FK)
    private String profileId;

    @NonNull
    @ColumnInfo(name = SCHEDULE_ID_FK)
    private String scheduleId;

    public ProfileScheduleRelation() {
    }
}
