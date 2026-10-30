package com.rsalanga.roadmap.shared.data.repository

import com.rsalanga.roadmap.shared.data.local.dao.PlanDao
import com.rsalanga.roadmap.shared.data.local.entity.PlanEntity
import com.rsalanga.roadmap.shared.domain.repository.PlanRepository
import kotlinx.coroutines.flow.Flow

class PlanRepositoryImpl(
    private val planDao: PlanDao
) : PlanRepository {

    override fun observePlans(): Flow<List<PlanEntity>> = planDao.getPlans()

    override fun savePlan() {
        TODO("Not yet implemented")
    }

    override fun deletePlan() {
        TODO("Not yet implemented")
    }
}
