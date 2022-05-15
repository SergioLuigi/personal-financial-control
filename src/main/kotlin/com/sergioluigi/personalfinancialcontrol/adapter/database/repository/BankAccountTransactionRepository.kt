package com.sergioluigi.personalfinancialcontrol.adapter.database.repository

import com.sergioluigi.personalfinancialcontrol.adapter.database.entity.BankAccountTransaction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.util.*

interface BankAccountTransactionRepository: JpaRepository<BankAccountTransaction, UUID>, JpaSpecificationExecutor<BankAccountTransaction> {
}