package com.appworks.portal.dto;

import com.appworks.portal.entity.AlertFrequency;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlertConfigurationRequest {

    @NotNull(message = "frequency is required")
    private AlertFrequency frequency;

    /** Optional. Defaults to true if not supplied. */
    private Boolean enabled;
}
