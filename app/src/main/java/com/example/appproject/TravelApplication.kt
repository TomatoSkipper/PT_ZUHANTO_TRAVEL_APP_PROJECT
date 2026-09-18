package com.example.appproject

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp

class TravelApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Firebase once for the entire application
        try {
            val app = FirebaseApp.initializeApp(this)
            Log.d("TravelApp", "Firebase initialized: ${app?.name}. Apps count: ${FirebaseApp.getApps(this).size}")
        } catch (e: Exception) {
            Log.e("TravelApp", "Firebase init failed", e)
        }
    }
}
