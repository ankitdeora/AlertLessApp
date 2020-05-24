package com.example.alertless.models;

import com.example.alertless.entities.AppDetailsEntity;
import com.example.alertless.entities.BaseEntity;
import com.example.alertless.exceptions.AlertlessIllegalArgumentException;
import com.example.alertless.utils.ValidationUtils;

import java.io.Serializable;

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
@ToString
@EqualsAndHashCode
@Builder
public class AppDetailsModel implements BaseModel, Serializable {
    private String appName;
    private String packageName;

    @Override
    public AppDetailsEntity getEntity(String id) throws AlertlessIllegalArgumentException {
        ValidationUtils.validateInput(id);

        return AppDetailsEntity.builder()
                    .id(id)
                    .appName(this.appName)
                    .packageName(this.packageName)
                .build();
    }
}
