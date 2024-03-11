package com.example.steptracker

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.steptracker.data.AppContainer
import com.example.steptracker.data.AppDataContainer
import com.example.steptracker.ui.navigation.NavHost

@Composable
fun StepTrackerApp(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController)
}

class StepTrackerApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}