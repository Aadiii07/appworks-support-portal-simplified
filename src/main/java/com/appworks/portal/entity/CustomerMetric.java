package com.appworks.portal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Join entity: assigns a Metric to a Customer. Not every customer runs every metric
 * (reference shows customers with 6-10 metrics assigned out of a shared catalog).
 * Uses its own @Id (not a composite key) to keep repository/service code simple and
 * consistent with the rest of the project — a unique constraint enforces one row
 * per (customer, metric) pair.
 */
@Entity
@Table(name = "customer_metric",
        uniqueConstraints = @UniqueConstraint(columnNames = {"customer_id", "metric_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "metric_id", nullable = false)
    private Metric metric;

    @Column(nullable = false)
    @Builder.Default
    private boolean enabled = true;

    // The SOAP API configuration for this customer's use of this metric
    // (requirement: "configure SOAP APIs for each customer, including Gateway
    // Endpoint URL, Service Name, and Namespace"). Kept here rather than on the
    // shared Metric catalog entry because these values are specific to this
    // customer's assignment, not shared across customers.
    @Column(name = "gateway_endpoint_url")
    private String gatewayEndpointUrl;

    @Column(name = "service_name")
    private String serviceName;

    @Column(name = "namespace")
    private String namespace;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
