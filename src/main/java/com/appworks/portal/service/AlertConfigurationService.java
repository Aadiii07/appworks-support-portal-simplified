package com.appworks.portal.service;

import com.appworks.portal.dto.AlertConfigurationRequest;
import com.appworks.portal.dto.AlertConfigurationResponse;
import com.appworks.portal.dto.AlertRecipientRequest;
import com.appworks.portal.dto.AlertRecipientResponse;
import com.appworks.portal.entity.AlertConfiguration;
import com.appworks.portal.entity.AlertRecipient;
import com.appworks.portal.entity.Customer;
import com.appworks.portal.exception.DuplicateResourceException;
import com.appworks.portal.exception.ResourceNotFoundException;
import com.appworks.portal.repository.AlertConfigurationRepository;
import com.appworks.portal.repository.AlertRecipientRepository;
import com.appworks.portal.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Lets a customer's alert frequency/enabled flag and recipient list actually be
 * configured through the API. AlertConfiguration, AlertRecipient and the email-
 * sending logic in AlertService already existed and were already wired into
 * MonitoringService — this service is the missing piece that lets a user (not
 * just a test) create and edit those rows.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AlertConfigurationService {

    private final AlertConfigurationRepository alertConfigurationRepository;
    private final AlertRecipientRepository alertRecipientRepository;
    private final CustomerRepository customerRepository;

    @Transactional(readOnly = true)
    public AlertConfigurationResponse getForCustomer(Long customerId) {
        return AlertConfigurationResponse.fromEntity(getConfigOrThrow(customerId));
    }

    /**
     * Creates the customer's alert configuration if it doesn't exist yet, or
     * updates frequency/enabled if it does. There's exactly one configuration
     * per customer (enforced by a unique constraint on customer_id), so an
     * idempotent upsert is a simpler contract here than separate create/update
     * endpoints.
     */
    public AlertConfigurationResponse upsert(Long customerId, AlertConfigurationRequest request) {
        Customer customer = getCustomerOrThrow(customerId);

        AlertConfiguration config = alertConfigurationRepository.findByCustomerId(customerId)
                .orElseGet(() -> AlertConfiguration.builder().customer(customer).build());

        config.setFrequency(request.getFrequency());
        config.setEnabled(request.getEnabled() == null || request.getEnabled());

        AlertConfiguration saved = alertConfigurationRepository.save(config);
        return AlertConfigurationResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<AlertRecipientResponse> listRecipients(Long customerId) {
        AlertConfiguration config = getConfigOrThrow(customerId);
        return alertRecipientRepository.findByAlertConfigurationId(config.getId()).stream()
                .map(AlertRecipientResponse::fromEntity)
                .toList();
    }

    public AlertRecipientResponse addRecipient(Long customerId, AlertRecipientRequest request) {
        AlertConfiguration config = getConfigOrThrow(customerId);

        if (alertRecipientRepository.existsByAlertConfigurationIdAndEmail(config.getId(), request.getEmail())) {
            throw new DuplicateResourceException(
                    "Recipient '" + request.getEmail() + "' is already configured for this customer");
        }

        AlertRecipient recipient = AlertRecipient.builder()
                .alertConfiguration(config)
                .email(request.getEmail())
                .build();

        AlertRecipient saved = alertRecipientRepository.save(recipient);
        return AlertRecipientResponse.fromEntity(saved);
    }

    public void removeRecipient(Long customerId, Long recipientId) {
        AlertConfiguration config = getConfigOrThrow(customerId);
        AlertRecipient recipient = alertRecipientRepository
                .findByIdAndAlertConfigurationId(recipientId, config.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Recipient not found with id " + recipientId + " for customer " + customerId));
        alertRecipientRepository.delete(recipient);
    }

    private Customer getCustomerOrThrow(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id " + customerId));
    }

    private AlertConfiguration getConfigOrThrow(Long customerId) {
        getCustomerOrThrow(customerId);
        return alertConfigurationRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No alert configuration found for customer " + customerId));
    }
}
