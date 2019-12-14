package com.example.contacts

import android.app.Application
import io.realm.Realm

class app : Application() {

    override fun onCreate() {
        super.onCreate()

        Realm.init(this)
    }
}