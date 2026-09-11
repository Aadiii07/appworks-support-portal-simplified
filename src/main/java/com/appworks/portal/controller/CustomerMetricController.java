package com.appworks.portal.controller;

import com.appworks.portal.dto.CustomerMetricEnabledRequest;
import com.appworks.portal.dto.CustomerMetricRequest;
import com.appworks.portal.dto.CustomerMetricResponse;
import com.appworks.portal.dto.CustomerMetricSoapConfigRequest;
import com.appworks.portal.service.CustomerMetricService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Manages which metrics from the global catalog are assigned to a given
 * customer, and whether each assignment is currently enabled. A metric must
 * be assigned here before it can be scheduled for that customer.
 */
@RestController
@RequestMapping("/api/v1/customers/{customerId}/metrics")
@RequiredArgsConstructor
public class CustomerMetricController {

    private final CustomerMetricService customerMetricService;

    @PostMapping
    public ResponseEntity<CustomerMetricResponse> assign(@PathVariable Long customerId,
                                                           @Valid @RequestBody CustomerMetricRequest request) {
        CustomerMetricResponse created = customerMetricService.assign(customerId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<CustomerMetricResponse>> findAll(@PathVariable Long customerId) {
        return ResponseEntity.ok(customerMetricService.findAllForCustomer(customerId));
    }

    @PatchMapping("/{customerMetricId}")
    public ResponseEntity<CustomerMetricResponse> updateEnabled(@PathVariable Long customerId,
                                                                  @PathVariable Long customerMetricId,
                                                                  @Valid @RequestBody CustomerMetricEnabledRequest request) {
        return ResponseEntity.ok(customerMetricService.updateEnabled(
                customerId, customerMetricId, request.getEnabled()));
    }

    @PatchMapping("/{customerMetricId}/soap-config")
    public ResponseEntity<CustomerMetricResponse> updateSoapConfig(@PathVariable Long customerId,
                                                                     @PathVariable Long customerMetricId,
                                                                     @Valid @RequestBody CustomerMetricSoapConfigRequest request) {
        return ResponseEntity.ok(customerMetricService.updateSoapConfig(customerId, customerMetricId, request));
    }

    @DeleteMapping("/{customerMetricId}")
    public ResponseEntity<Void> unassign(@PathVariable Long customerId,
                                          @PathVariable Long customerMetricId) {
        customerMetricService.unassign(customerId, customerMetricId);
        return ResponseEntity.noContent().build();
    }
}
