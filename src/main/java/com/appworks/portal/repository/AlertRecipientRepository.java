package com.appworks.portal.repository;

import com.appworks.portal.entity.AlertRecipient;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/** Data access for the email recipients attached to an AlertConfiguration. */
public interface AlertRecipientRepository extends JpaRepository<AlertRecipient, Long> {
    List<AlertRecipient> findByAlertConfigurationId(Long alertConfigurationId);
}
