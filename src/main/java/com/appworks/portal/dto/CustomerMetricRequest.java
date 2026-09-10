package com.appworks.portal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerMetricRequest {

    @NotNull(message = "metricId is required")
    private Long metricId;

    /** Optional. Defaults to true on create if not supplied. */
    private Boolean enabled;

    @NotBlank(message = "gatewayEndpointUrl is required")
    private String gatewayEndpointUrl;

    @NotBlank(message = "serviceName is required")
    private String serviceName;

    @NotBlank(message = "namespace is required")
    private String namespace;
}
