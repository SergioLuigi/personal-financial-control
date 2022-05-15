package com.sergioluigi.personalfinancialcontrol.adapter.database.repository

import com.sergioluigi.personalfinancialcontrol.adapter.database.entity.CashBoxTransaction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.util.*

interface CashBoxTransactionRepository: JpaRepository<CashBoxTransaction, UUID>, JpaSpecificationExecutor<CashBoxTransaction> {
}