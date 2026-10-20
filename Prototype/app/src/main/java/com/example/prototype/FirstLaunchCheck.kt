package com.example.prototype

import android.content.Context
import androidx.core.content.edit

class FirstLaunchCheck(context: Context) {

    private val prefs = context.getSharedPreferences(
        "modeSettings",
        Context.MODE_PRIVATE
    )

    // 初回起動かどうか
    fun isFirstLaunch(): Boolean {
        return prefs.getBoolean(
            KEY_FIRST_LAUNCH,
            true
        )
    }

    // 初回起動が終わったことを記録する
    fun setFirstLaunchFinished() {
        prefs.edit {
            putBoolean(
                KEY_FIRST_LAUNCH,
                false
            )
        }
    }

    // 初回起動状態に戻す
    fun resetFirstLaunch() {
        prefs.edit {
            putBoolean(
                KEY_FIRST_LAUNCH,
                true
            )
        }
    }

    // 通知モードを保存する
    fun saveAlertMode(mode: String) {
        prefs.edit {
            putString(
                KEY_ALERT_MODE,
                mode
            )
        }
    }

    // 保存されている通知モードを取得する
    fun getAlertMode(): String {
        return prefs.getString(
            KEY_ALERT_MODE,
            MODE_SOUND
        ) ?: MODE_SOUND
    }

    fun saveAlertDistance(distance: Int) {
        prefs.edit {
            putInt(
                KEY_ALERT_DISTANCE,
                distance
            )
                .apply()
        }
    }

    fun getAlertDistance(): Int {
        return try {
            prefs.getInt(KEY_ALERT_DISTANCE, 100)
        } catch (e: ClassCastException) {
            // 過去に文字列として保存された値を削除
            prefs.edit()
                .remove(KEY_ALERT_DISTANCE)
                .apply()

            // 初期値100mを返す
            100
        }
    }

    companion object {
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val KEY_ALERT_MODE = "alert_mode"
        private const val KEY_ALERT_DISTANCE = "distance_mode"

        const val MODE_SOUND = "sound"
        const val MODE_VIBRATION = "vibration"
        const val MODE_BOTH = "both"
        const val DEFAULT_DISTANCE = 100
    }
}