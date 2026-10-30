package com.rsalanga.roadmap.shared.domain.repository

import com.rsalanga.roadmap.shared.data.local.entity.PlanEntity
import kotlinx.coroutines.flow.Flow

interface PlanRepository {

    fun observePlans(): Flow<List<PlanEntity>>

    fun savePlan()

    fun deletePlan()
}
