package com.example.steptracker.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.steptracker.StepTrackerApplication
import com.example.steptracker.ui.agenda.AgendaViewModel
import com.example.steptracker.ui.home.HomeViewModel

object ViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            HomeViewModel(
                stepTrackerApplication().container.stepsData
            )
        }
        initializer {
            AgendaViewModel(
                stepTrackerApplication().container.stepsData
            )
        }
    }
}

fun CreationExtras.stepTrackerApplication(): StepTrackerApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as StepTrackerApplication)