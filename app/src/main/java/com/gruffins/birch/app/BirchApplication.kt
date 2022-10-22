package com.gruffins.birch.app

import android.app.Application
import com.gruffins.birch.Birch

class BirchApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        Birch.host = BuildConfig.BIRCH_HOST
        Birch.init(this, BuildConfig.BIRCH_API_KEY)
        Birch.debug = BuildConfig.DEBUG
    }
}