package ru.skillbranch.skillarticles;

import android.app.Application;
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import ru.skillbranch.skillarticles.data.PrefManager

class App : Application() {

    companion object {
        private var instance: App? = null

        fun aplicationContext() = instance!!.applicationContext
    }

    init {
        instance = this
    }


    override fun onCreate() {
        super.onCreate()


        AppCompatDelegate.setDefaultNightMode(if (PrefManager().isDarkMode) MODE_NIGHT_YES else MODE_NIGHT_NO)


    }

}
