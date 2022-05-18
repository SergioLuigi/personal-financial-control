package com.sergioluigi.personalfinancialcontrol.adapter.database.entity

import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "cash_box")
@EntityListeners(AuditingEntityListener::class)
class CashBox(

    @Id
    @GeneratedValue
    val id: UUID,

    @Column(name="description", length = 150, nullable = false)
    val description: String,

    @Column(name="value", nullable = false)
    val value: BigDecimal,

    @Column(name="balance", nullable = false)
    val balance: BigDecimal = BigDecimal.ZERO,

): Serializable {
    
    @ManyToOne
    @CreatedBy
    @JoinColumn(name = "user_id", nullable = false)
    lateinit var user: User

    @OneToMany(mappedBy = "cashBox")
    var transactions: Set<CashBoxTransaction> = emptySet()

    @CreatedDate
    lateinit var createDate: LocalDateTime

    @LastModifiedDate
    lateinit var updateDate: LocalDateTime
}