package com.example.adminordering

import android.content.Context
import com.cloudinary.android.MediaManager

object CloudinaryManager {
    private var isInitialized = false

    fun init(context: Context) {
        if (isInitialized) return

        val config = mapOf(
            "cloud_name" to "dzhsr5fb2",
            "api_key" to "154591567259533",
            "api_secret" to "1QQ1Ikwa9JTbFd55D5NP05pyBxw",
            "secure" to true
        )

        try {
            MediaManager.init(context, config)
            isInitialized = true
        } catch (e: IllegalStateException) {

            isInitialized = true
        }
    }
}