package com.aca.people

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import io.paperdb.Paper

@HiltAndroidApp
class PeopleApp : Application(){
override fun onCreate() {
    super.onCreate()
    Paper.init(this)
}
}