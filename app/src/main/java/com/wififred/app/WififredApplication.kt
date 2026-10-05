package com.wififred.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * كلاس التطبيق الرئيسي
 * @HiltAndroidApp تقوم بتفعيل Dagger Hilt في كامل التطبيق
 */
@HiltAndroidApp
class WififredApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // تهيئة الإعدادات العامة هنا إن وجدت
    }
}
