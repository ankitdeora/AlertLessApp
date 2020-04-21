package com.example.alertless.models;


import java.io.Serializable;
import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class Profile implements Serializable {
    private ProfileDetailsModel details;
    private ArrayList<AppDetailsModel> apps;
    private ScheduleModel schedule;
}
