package com.example.steptracker.ui.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.health.connect.client.records.StepsRecord
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.steptracker.data.StepsData
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId

class HomeViewModel(private val stepsData: StepsData) : ViewModel() {
    val dayData: MutableState<DayData> = mutableStateOf(DayData())

    init {
        stepsData.initClient()
    }

    fun fetchDayData(){
        viewModelScope.launch {
            stepsData.getDayData(
                LocalDateTime.now().toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant(),
                LocalDateTime.now().toLocalDate().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant(),
            )?.let {
                dayData.value = DayData(it)
            }
        }
    }

    fun addData(){
        viewModelScope.launch {
            stepsData.insertSteps(1, LocalDateTime.now().toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant(), LocalDateTime.now().toLocalDate().plusDays(2).atStartOfDay(ZoneId.systemDefault()).toInstant(),)
        }
    }

    data class DayData(
        val stepsRecords: List<StepsRecord> = listOf()
    )
}