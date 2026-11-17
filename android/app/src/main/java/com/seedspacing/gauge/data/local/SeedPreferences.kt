package com.seedspacing.gauge.data.local

import android.content.Context
import android.content.SharedPreferences

class SeedPreferences(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE)

    fun text(resId: Int): String = context.getString(resId)

    fun readString(key: String, fallback: String): String =
        prefs.getString(key, fallback) ?: fallback

    fun writeString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun readBoolean(key: String, fallback: Boolean): Boolean = prefs.getBoolean(key, fallback)

    fun writeBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    companion object {
        const val STORE_NAME = "seed_spacing_prefs"
        const val KEY_BEDS = "beds_v1"
        const val KEY_SEEDED = "seeded_v1"
        const val KEY_UNITS = "units"
        const val KEY_THEME = "theme"
        const val KEY_HAPTICS = "haptics"
        const val KEY_OFFSET = "offset_default"
    }
}
