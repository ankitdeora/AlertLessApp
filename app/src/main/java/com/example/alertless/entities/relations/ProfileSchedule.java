package com.example.alertless.entities.relations;

import com.example.alertless.entities.ScheduleType;
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
public class ProfileSchedule {
    private String profileId;
    private ScheduleType scheduleType;
    private String scheduleId;
}
