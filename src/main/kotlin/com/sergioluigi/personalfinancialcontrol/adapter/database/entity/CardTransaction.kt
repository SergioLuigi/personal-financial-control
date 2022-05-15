package com.sergioluigi.personalfinancialcontrol.adapter.database.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "card_transaction")
@EntityListeners(AuditingEntityListener::class)
class CardTransaction(

    @Id
    @GeneratedValue
    val id: UUID,

    @ManyToOne
    @JoinColumn(name="card_id", nullable = false)
    val card: Card,

    @Column(name="description", length = 150, nullable = false)
    val description: String,

    @Column(name="recurrent", nullable = false)
    val recurrent: Boolean = false,

    @Column(name="installments", nullable = false)
    val installments: BigDecimal = BigDecimal.ZERO,

    @Column(name="value", nullable = false)
    val value: BigDecimal,

    @Column(name="limit", nullable = false)
    val limit: BigDecimal = BigDecimal.ZERO,

): Serializable{

    @CreatedDate
    lateinit var createDate: LocalDateTime

    @LastModifiedDate
    lateinit var updateDate: LocalDateTime
}