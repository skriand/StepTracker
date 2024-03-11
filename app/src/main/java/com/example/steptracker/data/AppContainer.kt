package com.example.steptracker.data

import android.content.Context

interface AppContainer {
    val stepsData: StepsData
}

class AppDataContainer(private val context: Context) : AppContainer {

    override val stepsData: StepsData by lazy {
        StepsDataImp(context)
    }
}