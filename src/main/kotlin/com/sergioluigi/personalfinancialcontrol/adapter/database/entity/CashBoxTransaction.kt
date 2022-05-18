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
@Table(name = "cash_box_transaction")
@EntityListeners(AuditingEntityListener::class)
class CashBoxTransaction(

    @Id
    @GeneratedValue
    val id: UUID,
    
    @Column(name = "operation")
    @Enumerated(EnumType.STRING)
    val operation: CashBoxTransactionOperaion,

    @ManyToOne
    @JoinColumn(name="cash_box_id", nullable = false)
    val cashBox: CashBox,

    @Column(name="description", length = 150, nullable = false)
    val description: String,

    @Column(name="value", nullable = false)
    val value: BigDecimal,
    

): Serializable {
    
    @ManyToOne
    @CreatedBy
    @JoinColumn(name = "user_id", nullable = false)
    lateinit var user: User

    @CreatedDate
    lateinit var createDate: LocalDateTime

    @LastModifiedDate
    lateinit var updateDate: LocalDateTime
}