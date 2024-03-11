package com.example.steptracker.ui.agenda


import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.health.connect.client.records.StepsRecord
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.steptracker.data.StepsData
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class AgendaViewModel(private val stepsData: StepsData) : ViewModel() {

    val dailySteps: MutableState<List<Pair<LocalDateTime, Long?>>> = mutableStateOf(listOf())
    val dayRecords: MutableState<List<List<StepsRecord>>> = mutableStateOf(listOf())
    private val stepsDataPeriod: MutableState<Pair<LocalDateTime, LocalDateTime>> = mutableStateOf(
        Pair(
            LocalDateTime.now().toLocalDate().atStartOfDay().minusMonths(1),
            LocalDateTime.now().toLocalDate().atStartOfDay()
        )
    )

    private fun fetchDayData(dayInfo: Pair<LocalDate, Long?>) {
        viewModelScope.launch {
            stepsData.getDayData(
                dayInfo.first.atStartOfDay(ZoneId.systemDefault()).toInstant(),
                dayInfo.first.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant(),
            )?.let {
                if (dayRecords.value.isNotEmpty())
                    dayRecords.value += listOf(it)
                else
                    dayRecords.value = listOf(it)
            }
        }
    }

    fun updateAggregationData() {
        viewModelScope.launch {
            stepsData.getAggregatedMonthData(
                stepsDataPeriod.value.first,
                stepsDataPeriod.value.second.plusDays(1).minusSeconds(1)
            )?.let { data ->
                dailySteps.value = data.toList()
                dailySteps.value.forEach{
                    fetchDayData(Pair(it.first.toLocalDate(), it.second))
                }
            }
        }
    }

    fun addData() {
        viewModelScope.launch {
            stepsData.insertSteps(
                1,
                LocalDateTime.now().toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant(),
                LocalDateTime.now().toLocalDate().plusDays(2).atStartOfDay(ZoneId.systemDefault())
                    .toInstant(),
            )
        }
    }

    data class DailySteps(
        val installSdkDialogRequired: Boolean = false,
        val permissionDialogRequired: Boolean = false,
    )

    fun LocalDate.getAllDaysUntil(until: LocalDate): List<LocalDate> {
        val result = mutableListOf<LocalDate>()
        for (i in 0L..ChronoUnit.DAYS.between(this, until)) {
            result.add(this.plusDays(i))
        }
        return result
    }
}
