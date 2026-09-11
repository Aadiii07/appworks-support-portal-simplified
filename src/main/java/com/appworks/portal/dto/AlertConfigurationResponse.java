package com.appworks.portal.dto;

import com.appworks.portal.entity.AlertConfiguration;
import com.appworks.portal.entity.AlertFrequency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertConfigurationResponse {

    private Long id;
    private Long customerId;
    private AlertFrequency frequency;
    private boolean enabled;
    private LocalDateTime lastSentAt;

    public static AlertConfigurationResponse fromEntity(AlertConfiguration config) {
        return AlertConfigurationResponse.builder()
                .id(config.getId())
                .customerId(config.getCustomer().getId())
                .frequency(config.getFrequency())
                .enabled(config.isEnabled())
                .lastSentAt(config.getLastSentAt())
                .build();
    }
}
