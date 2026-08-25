package com.example.domain

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import java.util.concurrent.Executors

class SoundHapticHelper(private val context: Context) {

    private val asyncExecutor = Executors.newSingleThreadExecutor()

    private var toneGenerator: ToneGenerator? = try {
        ToneGenerator(AudioManager.STREAM_MUSIC, 40)
    } catch (e: Exception) {
        null
    }

    private val vibrator: Vibrator? = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    } catch (e: Exception) {
        null
    }

    fun playClick(isSoundEnabled: Boolean, isEquals: Boolean = false, isClear: Boolean = false) {
        if (!isSoundEnabled) return
        asyncExecutor.execute {
            try {
                val tone = when {
                    isEquals -> ToneGenerator.TONE_PROP_ACK
                    isClear -> ToneGenerator.TONE_PROP_BEEP2
                    else -> ToneGenerator.TONE_PROP_BEEP
                }
                toneGenerator?.startTone(tone, 20)
            } catch (e: Exception) {
                // Ignore sound errors
            }
        }
    }

    fun triggerHaptic(composeHaptics: HapticFeedback?, isHapticsEnabled: Boolean, isHeavy: Boolean = false) {
        if (!isHapticsEnabled) return
        // Trigger Compose haptics immediately
        try {
            if (composeHaptics != null) {
                if (isHeavy) {
                    composeHaptics.performHapticFeedback(HapticFeedbackType.LongPress)
                } else {
                    composeHaptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }
            }
        } catch (e: Exception) {
            // Ignore
        }

        // Hardware vibrator off-loaded to async executor to prevent UI thread lock
        asyncExecutor.execute {
            try {
                if (vibrator != null && vibrator.hasVibrator()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        val effect = if (isHeavy) {
                            VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK)
                        } else {
                            VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                        }
                        vibrator.vibrate(effect)
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val duration = if (isHeavy) 30L else 14L
                        val amplitude = if (isHeavy) 180 else 100
                        vibrator.vibrate(VibrationEffect.createOneShot(duration, amplitude))
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator.vibrate(if (isHeavy) 30L else 14L)
                    }
                }
            } catch (e: Exception) {
                // Ignore vibration errors
            }
        }
    }

    fun release() {
        asyncExecutor.execute {
            try {
                toneGenerator?.release()
                toneGenerator = null
            } catch (e: Exception) {
                // Ignore
            }
        }
        try {
            asyncExecutor.shutdown()
        } catch (e: Exception) {
            // Ignore
        }
    }
}

