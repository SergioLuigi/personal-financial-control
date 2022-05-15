package com.sergioluigi.personalfinancialcontrol.adapter.database.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.io.Serializable
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "proj_user")
@EntityListeners(AuditingEntityListener::class)
data class User(

    @Id
    @GeneratedValue
    val id: UUID,

    @Column(name="username", length = 20, nullable = false, unique = true)
    val username: String,

    @Column(name="email", length = 120, nullable = false, unique = true)
    val email: String,

    @Column(name="name", length = 100, nullable = false)
    val name: String,

    @Column(name="last_name", length = 120, nullable = false)
    val lastName: String,

): Serializable {

    @OneToMany(mappedBy = "user")
    var bankAccounts: Set<BankAccount> = emptySet()

    @OneToOne(mappedBy = "user")
    var cashBox: CashBox? = null

    @CreatedDate
    lateinit var createDate: LocalDateTime

    @LastModifiedDate
    lateinit var updateDate: LocalDateTime
}