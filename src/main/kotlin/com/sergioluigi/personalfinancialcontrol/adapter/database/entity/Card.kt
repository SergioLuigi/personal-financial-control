package com.sergioluigi.personalfinancialcontrol.adapter.database.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "card")
@EntityListeners(AuditingEntityListener::class)
class Card(

    @Id
    @GeneratedValue
    val id: UUID,
    
    @ManyToOne
    @JoinColumn(name="owner_user_id", nullable = false)
    val owner: User,

    @ManyToOne
    @JoinColumn(name="bank_account_id", nullable = false)
    val bankAccount: BankAccount,

    @Column(name="number", length = 16, nullable = false)
    val number: String,

    @Column(name="valid_thru", nullable = false)
    val validThru: LocalDate,

    @Enumerated(EnumType.STRING)
    @Column(name="type", nullable = false)
    val type: CardType,

    @Column(name="limit", nullable = false)
    val limit: BigDecimal = BigDecimal.ZERO,

): Serializable {

    @OneToMany(mappedBy = "card")
    var transactions: Set<CardTransaction> = emptySet()

    @CreatedDate
    lateinit var createDate: LocalDateTime

    @LastModifiedDate
    lateinit var updateDate: LocalDateTime
}