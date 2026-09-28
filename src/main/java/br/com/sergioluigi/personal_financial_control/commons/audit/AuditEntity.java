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

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class AuditEntity {

    // The owner of the record, set from the domain model rather than by auditing, so
    // that a record created on a user's behalf still belongs to that user. It never
    // changes after creation.
    @Column(updatable = false)
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;

    @CreatedDate
    @Column(updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
