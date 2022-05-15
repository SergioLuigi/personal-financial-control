package com.sergioluigi.personalfinancialcontrol.adapter.database.repository

import com.sergioluigi.personalfinancialcontrol.adapter.database.entity.Card
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.util.*

interface CardRepository: JpaRepository<Card, UUID>, JpaSpecificationExecutor<Card> {
}