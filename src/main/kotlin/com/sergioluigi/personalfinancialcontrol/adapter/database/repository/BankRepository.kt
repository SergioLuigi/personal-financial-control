package com.sergioluigi.personalfinancialcontrol.adapter.database.repository

import com.sergioluigi.personalfinancialcontrol.adapter.database.entity.Bank
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.util.*

interface BankRepository: JpaRepository<Bank, UUID>, JpaSpecificationExecutor<Bank> {
}