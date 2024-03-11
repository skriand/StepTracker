package com.example.steptracker.data

import androidx.health.connect.client.records.StepsRecord
import java.time.Instant
import java.time.LocalDateTime

interface StepsData {
    fun initClient()
    suspend fun checkPermissions(): Boolean

    suspend fun getAggregatedMonthData(
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): Map<LocalDateTime, Long?>?
    suspend fun getDayData(startTime: Instant, endTime: Instant): List<StepsRecord>?
    suspend fun insertSteps(count: Long, startTime: Instant, endTime: Instant): Boolean
    suspend fun deleteStepsRecord(id: String): Boolean
}