package com.example.alertless.models;

import com.example.alertless.entities.ProfileDetailsEntity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
@Builder
@EqualsAndHashCode
public class ProfileDetailsModel implements Serializable {
    private String name;
    private boolean active;

    public static ProfileDetailsModel getModel(ProfileDetailsEntity profileDetailsEntity) {

        if (profileDetailsEntity == null) {
            return null;
        }

        return ProfileDetailsModel.builder()
                    .name(profileDetailsEntity.getName())
                    .active(profileDetailsEntity.isActive())
                .build();
    }
}
