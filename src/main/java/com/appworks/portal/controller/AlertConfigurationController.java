package com.appworks.portal.controller;

import com.appworks.portal.dto.AlertConfigurationRequest;
import com.appworks.portal.dto.AlertConfigurationResponse;
import com.appworks.portal.dto.AlertRecipientRequest;
import com.appworks.portal.dto.AlertRecipientResponse;
import com.appworks.portal.service.AlertConfigurationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Lets a customer's alert frequency, enabled flag, and email recipient list be
 * configured. Once a configuration and at least one recipient exist here,
 * MonitoringService's existing call to AlertService.notify() picks them up
 * automatically on every run — no changes needed there.
 */
@RestController
@RequestMapping("/api/v1/customers/{customerId}/alert-configuration")
@RequiredArgsConstructor
public class AlertConfigurationController {

    private final AlertConfigurationService alertConfigurationService;

    @GetMapping
    public ResponseEntity<AlertConfigurationResponse> get(@PathVariable Long customerId) {
        return ResponseEntity.ok(alertConfigurationService.getForCustomer(customerId));
    }

    @PutMapping
    public ResponseEntity<AlertConfigurationResponse> upsert(@PathVariable Long customerId,
                                                               @Valid @RequestBody AlertConfigurationRequest request) {
        return ResponseEntity.ok(alertConfigurationService.upsert(customerId, request));
    }

    @GetMapping("/recipients")
    public ResponseEntity<List<AlertRecipientResponse>> listRecipients(@PathVariable Long customerId) {
        return ResponseEntity.ok(alertConfigurationService.listRecipients(customerId));
    }

    @PostMapping("/recipients")
    public ResponseEntity<AlertRecipientResponse> addRecipient(@PathVariable Long customerId,
                                                                 @Valid @RequestBody AlertRecipientRequest request) {
        AlertRecipientResponse created = alertConfigurationService.addRecipient(customerId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/recipients/{recipientId}")
    public ResponseEntity<Void> removeRecipient(@PathVariable Long customerId, @PathVariable Long recipientId) {
        alertConfigurationService.removeRecipient(customerId, recipientId);
        return ResponseEntity.noContent().build();
    }
}
