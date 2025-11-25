package com.beta.safalya_v2

import android.app.Application
import com.google.firebase.FirebaseApp

class SafalyaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}

