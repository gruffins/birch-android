package com.gruffins.birch.app

import android.app.Application
import com.gruffins.birch.Birch
import com.gruffins.birch.Options

class BirchApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        Birch.init(
            this,
            BuildConfig.BIRCH_API_KEY,
            null,
            Options().also {
                it.host = BuildConfig.BIRCH_HOST
            }
        )
    }
}