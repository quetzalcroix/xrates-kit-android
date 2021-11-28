package io.vextabit.xrateskit.demo

import android.app.Application
import com.facebook.stetho.Stetho
import io.vextabit.xrateskit.demo.chartdemo.NumberFormatter

class App : Application() {

    companion object {
        lateinit var ratesManager: RatesManager
        lateinit var baseCurrency: String
        lateinit var numberFormatter: NumberFormatter
    }

    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)

        baseCurrency = "USD"
        ratesManager = RatesManager(this, baseCurrency)
        numberFormatter = NumberFormatter()
    }
}
