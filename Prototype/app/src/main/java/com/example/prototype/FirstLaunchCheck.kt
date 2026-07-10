package com.example.prototype

import android.content.Context

class FirstLaunchCheck(private val context: Context) {
    private val prefs = context.getSharedPreferences(
        "modeSettings",
        Context.MODE_PRIVATE
    )
    //初回起動かどうか
    fun isFirstLaunch(): Boolean {
        return  prefs.getBoolean(
            KEY_FIRST_LAUNCH,
            true
        )
    }

    //初回起動が終わったことを記録する
    fun setFirstLaunchFinished() {
        prefs.edit()
            .putBoolean(
                KEY_FIRST_LAUNCH,
                false
            )
            .apply()
    }

    // 追加：初回起動状態に戻す
    fun resetFirstLaunch() {
        prefs.edit()
            .putBoolean(
                KEY_FIRST_LAUNCH,
                true
            )
            .apply()
    }

    fun saveAlertMode(mode: String) {
        prefs.edit()
            .putString(
                KEY_ALERT_MODE,
                mode
            )
            .apply()
    }

    fun getAlertMode(): String {
        return prefs.getString(
            KEY_ALERT_MODE,
            MODE_SOUND
        ) ?: MODE_SOUND
    }

    companion object {
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val KEY_ALERT_MODE = "alert_mode"

        const val MODE_SOUND = "sound"
        const val MODE_VIBRATION = "vibration"
        const val MODE_BOTH = "both"
    }
}