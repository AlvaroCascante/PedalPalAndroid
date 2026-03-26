package com.quetoquenana.and

import android.app.Application
import android.util.Log
import android.os.Build
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class PedalPalApp : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())

            // Only connect to the local Firebase emulators when running inside an Android emulator.
            // Physical devices cannot reach 10.0.2.2 on the host without port forwarding or using the
            // machine IP. This check prevents "Failed to connect to /10.0.2.2:9099" when running on a phone.
            if (isProbablyAnEmulator()) {
                try {
                    FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099)
                    Timber.d("Firebase Auth emulator enabled at 10.0.2.2:9099")
                } catch (e: Exception) {
                    Timber.w(e, "Unable to connect to Firebase Auth emulator")
                }
            } else {
                Timber.d(
                    "Build props: FINGERPRINT=%s MODEL=%s PRODUCT=%s BRAND=%s DEVICE=%s MANUFACTURER=%s HARDWARE=%s",
                    Build.FINGERPRINT, Build.MODEL, Build.PRODUCT, Build.BRAND, Build.DEVICE, Build.MANUFACTURER, Build.HARDWARE
                )
                Timber.w("Detected physical device; skipping Firebase Auth emulator configuration")
            }
        } else {
            Timber.plant(ReleaseTree())
        }
    }

    private fun isProbablyAnEmulator(): Boolean {
        return (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("sdk_gphone")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("google") && Build.DEVICE.startsWith("emu"))
                || "google_sdk" == Build.PRODUCT)
    }
}

class ReleaseTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG) {
            return
        }
    }
}