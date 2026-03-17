package com.sky.chessplay

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        require(
            !(BuildConfig.FLAVOR == "prod" &&
                    BuildConfig.BASE_URL.contains("10.0.2.2"))
        ) {
            "❌ Prod -> localhost!"
        }

        Log.d(
            "ENV",
            "flavor=${BuildConfig.FLAVOR}, type=${BuildConfig.BUILD_TYPE}"
        )
    }
}
