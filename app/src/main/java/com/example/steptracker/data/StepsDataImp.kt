package com.example.steptracker.data

import android.content.Context
import android.util.Log
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.AggregateGroupByPeriodRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Instant
import java.time.LocalDateTime
import java.time.Period
import java.time.ZoneOffset

class StepsDataImp(private val applicationContext: Context): StepsData {
    private lateinit var healthConnectClient: HealthConnectClient

    private val requiredPermissions = setOf(
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getWritePermission(StepsRecord::class)
    )

    override fun initClient() {
        healthConnectClient = HealthConnectClient.getOrCreate(applicationContext)
    }

    override suspend fun checkPermissions(): Boolean {
        return healthConnectClient.permissionController.getGrantedPermissions()
            .containsAll(requiredPermissions.toList())
    }

    override suspend fun getDayData(
        startTime: Instant,
        endTime: Instant
    ): List<StepsRecord>?{
        return try {
            val response = healthConnectClient.readRecords(
                ReadRecordsRequest(
                    StepsRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )
            response.records
        } catch (e: Exception) {
            Log.e("StepsData", "Day fetch failed: $e")
            null
        }
    }

    override suspend fun getAggregatedMonthData(
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): Map<LocalDateTime, Long?>? {
        return try {
            val response =
                healthConnectClient.aggregateGroupByPeriod(
                    AggregateGroupByPeriodRequest(
                        metrics = setOf(StepsRecord.COUNT_TOTAL),
                        timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
                        timeRangeSlicer = Period.ofDays(1)
                    )
                )
            response.associateBy({it.startTime}, {it.result[StepsRecord.COUNT_TOTAL]})
        } catch (e: Exception) {
            Log.e("StepsData", "Aggregation failed: $e")
            null
        }
    }

    override suspend fun insertSteps(
        count: Long,
        startTime: Instant,
        endTime: Instant,
    ): Boolean{
        return try {
            val stepsRecord = StepsRecord(
                count = count,
                startTime = startTime,
                endTime = endTime,
                startZoneOffset = ZoneOffset.UTC,
                endZoneOffset = ZoneOffset.UTC
            )
            return healthConnectClient.insertRecords(listOf(stepsRecord)).recordIdsList.isNotEmpty()
        } catch (e: Exception) {
            Log.e("StepsData", "Steps save failed: $e")
            false
        }
    }

    override suspend fun deleteStepsRecord(
        id: String
    ): Boolean{
        return try {
            healthConnectClient.deleteRecords(
                StepsRecord::class,
                recordIdsList = listOf(id),
                clientRecordIdsList = listOf()
            )
            true
        } catch (e: Exception) {
            Log.e("StepsData", "Steps record deletion failed: $e")
            false
        }
    }
}