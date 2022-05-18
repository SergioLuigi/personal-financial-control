package com.sergioluigi.personalfinancialcontrol.adapter.database.repository

import com.sergioluigi.personalfinancialcontrol.adapter.database.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.util.*

interface UserRepository: JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {
	fun findByCpf(cpf: String): User?
}