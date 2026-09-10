package com.appworks.portal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * Separate from CustomerMetricRequest/CustomerMetricEnabledRequest so that
 * editing the SOAP config doesn't touch the enabled flag, and toggling enabled
 * doesn't require re-sending the SOAP config.
 */
@Getter
@Setter
public class CustomerMetricSoapConfigRequest {

    @NotBlank(message = "gatewayEndpointUrl is required")
    private String gatewayEndpointUrl;

    @NotBlank(message = "serviceName is required")
    private String serviceName;

    @NotBlank(message = "namespace is required")
    private String namespace;
}
