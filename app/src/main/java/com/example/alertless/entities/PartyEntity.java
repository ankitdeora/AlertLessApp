package com.example.alertless.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import static com.example.alertless.utils.Constants.PARTY_TABLE;
import static com.example.alertless.utils.Constants.SCHEDULE_TYPE_COL;

@Getter
@Setter
@AllArgsConstructor
@ToString
@Builder
@EqualsAndHashCode
@Entity(tableName = PARTY_TABLE)
public class PartyEntity implements Identity{
    @NonNull
    @PrimaryKey
    private String id;

    @NonNull
    @ColumnInfo(name = SCHEDULE_TYPE_COL)
    private String scheduleType;

    public PartyEntity() {
    }
}
