package com.example.victory_group_test_task.app

import android.app.Application
import com.yandex.mapkit.MapKitFactory

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey("f649864e-27a6-47a3-886b-e9f615f6f8bc")
    }
}