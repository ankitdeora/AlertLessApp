package com.example.alertless.entities.relations;

import com.example.alertless.commons.ScheduleType;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class ProfileScheduleRelation {
    private String profileId;
    private ScheduleType scheduleType;
    private String scheduleId;
}
