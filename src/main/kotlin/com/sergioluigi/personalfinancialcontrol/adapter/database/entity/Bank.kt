package com.sergioluigi.personalfinancialcontrol.adapter.database.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.io.Serializable
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "bank")
@EntityListeners(AuditingEntityListener::class)
class Bank(

    @Id
    @GeneratedValue
    val id: String,

    @Column(name="name", length = 120, nullable = false, unique = true)
    val name: String,

): Serializable {

    @CreatedDate
    lateinit var createDate: LocalDateTime

    @LastModifiedDate
    lateinit var updateDate: LocalDateTime
}