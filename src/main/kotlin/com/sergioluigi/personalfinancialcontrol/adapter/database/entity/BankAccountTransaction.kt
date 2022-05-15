package com.sergioluigi.personalfinancialcontrol.adapter.database.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.io.Serializable
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "bank_account_transaction")
@EntityListeners(AuditingEntityListener::class)
class BankAccountTransaction(

    @Id
    @GeneratedValue
    val id: UUID,

    @ManyToOne
    @JoinColumn(name="bank_account_id", nullable = false)
    val bankAccount: BankAccount,

    @Column(name="description", length = 150, nullable = false)
    val description: String,

    @Column(name="recurrent", nullable = false)
    val recurrent: Boolean = false,

    @Column(name="frequency_in_days", nullable = false)
    val frequencyInDays: Int = 0,

    @Enumerated(EnumType.STRING)
    @Column(name="operation", nullable = false)
    val operation: BankAccountTransactionOperation

): Serializable {

    @CreatedDate
    lateinit var createDate: LocalDateTime

    @LastModifiedDate
    lateinit var updateDate: LocalDateTime
}