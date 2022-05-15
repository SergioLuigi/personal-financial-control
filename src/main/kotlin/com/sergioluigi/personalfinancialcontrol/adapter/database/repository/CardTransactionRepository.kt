package com.sergioluigi.personalfinancialcontrol.adapter.database.repository

import com.sergioluigi.personalfinancialcontrol.adapter.database.entity.CardTransaction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.util.*

interface CardTransactionRepository: JpaRepository<CardTransaction, UUID>, JpaSpecificationExecutor<CardTransaction> {
}