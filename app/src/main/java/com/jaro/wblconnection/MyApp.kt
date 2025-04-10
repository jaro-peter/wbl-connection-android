package com.jaro.wblconnection
import android.app.Application
import android.util.Log
import com.parse.Parse

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        val appId = "7oP3tukRXeZMTK5pL6eeeencS6tJQ3TQZGm6jLh2".trim()
        val clientKey = "73wVMQmQnM4ueRKKmk5tJOv57INbCTuUqhVFYDGi".trim()
        Log.d("ParseDebug", "📏 App ID hossza: ${appId.length}")


        Parse.initialize(
            Parse.Configuration.Builder(this)
                .applicationId(appId)
                .clientKey(clientKey)
                .server("https://parseapi.back4app.com")
                .build()
        )
    }
}
