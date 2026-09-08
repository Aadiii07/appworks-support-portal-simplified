package com.appworks.portal.repository;

import com.appworks.portal.entity.AlertConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/** Data access for a customer's single AlertConfiguration (alert on/off, frequency, throttling state). */
public interface AlertConfigurationRepository extends JpaRepository<AlertConfiguration, Long> {
    Optional<AlertConfiguration> findByCustomerId(Long customerId);
}
