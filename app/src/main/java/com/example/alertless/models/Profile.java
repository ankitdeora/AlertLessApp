package com.example.alertless.models;

import com.example.alertless.entities.AppDetails;
import com.example.alertless.entities.ProfileDetails;
import com.example.alertless.entities.Schedule;
import java.util.List;
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
public class Profile {
    private ProfileDetails details;
    private List<AppDetails> silentApps;
    private List<Schedule> schedules;
}
