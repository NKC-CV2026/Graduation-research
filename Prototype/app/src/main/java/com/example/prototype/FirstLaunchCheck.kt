package com.example.prototype

import android.content.Context
import androidx.core.content.edit

class FirstLaunchCheck(private val context: Context) {
    private val prefs = context.getSharedPreferences(
        "modeSettings",
        Context.MODE_PRIVATE
    )
    //初回起動かどうか
    fun isFirstLaunch(): Boolean {
        return  prefs.getBoolean(
            "first_launch",
            true
        )
    }

    //初回起動が終わったことを記録する
    fun setFirstLaunchFinished() {
        prefs.edit {
            putBoolean(
                "first_launch",
                false
            )
        }
    }

    // 追加：初回起動状態に戻す
    fun resetFirstLaunch() {
        prefs.edit {
            putBoolean(
                "first_launch",
                true
            )
        }
    }
}