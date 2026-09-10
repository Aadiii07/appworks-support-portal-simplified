package com.appworks.portal.repository;

import com.appworks.portal.entity.AlertRecipient;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

/** Data access for the email recipients attached to an AlertConfiguration. */
public interface AlertRecipientRepository extends JpaRepository<AlertRecipient, Long> {
    List<AlertRecipient> findByAlertConfigurationId(Long alertConfigurationId);

    // Added for the alert-configuration API: lets the service reject a duplicate
    // email cleanly (409) instead of relying on the DB's unique constraint to
    // throw a raw data-integrity exception.
    boolean existsByAlertConfigurationIdAndEmail(Long alertConfigurationId, String email);

    // Added for the alert-configuration API: scopes a recipient lookup to its
    // parent configuration, so deleting recipient X under the wrong customer's
    // configuration 404s instead of succeeding.
    Optional<AlertRecipient> findByIdAndAlertConfigurationId(Long id, Long alertConfigurationId);
}
