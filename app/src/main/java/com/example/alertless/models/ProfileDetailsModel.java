package com.example.alertless.models;

import com.example.alertless.entities.ProfileDetailsEntity;
import com.example.alertless.exceptions.AlertlessIllegalArgumentException;
import com.example.alertless.utils.ValidationUtils;

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
public class ProfileDetailsModel extends BaseModel implements Serializable {
    private String name;
    private boolean active;

    @Override
    public ProfileDetailsEntity getEntity(String id) throws AlertlessIllegalArgumentException {
        ValidationUtils.validateInput(id);

        return ProfileDetailsEntity.builder()
                    .id(id)
                    .name(this.name)
                    .active(this.active)
                .build();
    }
}
