package br.com.sergioluigi.personal_financial_control.commons.audit;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

/**
 * Audit columns embedded in every JPA entity, so each table records who owns a row and when it was written.
 */
@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class AuditEntity {

    /**
     * The owner of the record, set from the domain model rather than by auditing, so that a record created on
     * a user's behalf still belongs to that user. It never changes after creation.
     */
    @Column(updatable = false)
    private String createdBy;

    /** The user who last changed the record, filled in by Spring Data auditing. */
    @LastModifiedBy
    private String updatedBy;

    /** The moment the record was created, filled in by auditing. It never changes after creation. */
    @CreatedDate
    @Column(updatable = false)
    private Instant createdAt;

    /** The moment the record was last changed, filled in by auditing. */
    @LastModifiedDate
    private Instant updatedAt;
}
