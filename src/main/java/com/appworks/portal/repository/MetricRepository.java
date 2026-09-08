package com.appworks.portal.repository;

import com.appworks.portal.entity.Metric;
import org.springframework.data.jpa.repository.JpaRepository;

/** Data access for the global Metric catalog, plus the serviceKey-uniqueness checks used by MetricService. */
public interface MetricRepository extends JpaRepository<Metric, Long> {

    boolean existsByServiceKeyIgnoreCase(String serviceKey);

    boolean existsByServiceKeyIgnoreCaseAndIdNot(String serviceKey, Long id);
}
