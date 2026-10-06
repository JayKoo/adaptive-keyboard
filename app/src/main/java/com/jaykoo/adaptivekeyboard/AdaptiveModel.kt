package com.jaykoo.adaptivekeyboard

import android.content.Context
import kotlin.math.pow

data class KeyStat(var dx: Float = 0f, var dy: Float = 0f, var n: Int = 0)

class AdaptiveModel(context: Context) {
    private val prefs = context.getSharedPreferences("adaptive_model", Context.MODE_PRIVATE)
    private val stats = mutableMapOf<Char, KeyStat>()
    private val alpha = 0.15f

    init {
        ('a'..'z').forEach { c ->
            stats[c] = KeyStat(
                prefs.getFloat("${c}_dx", 0f),
                prefs.getFloat("${c}_dy", 0f),
                prefs.getInt("${c}_n", 0)
            )
        }
    }

    fun score(key: Char, touchX: Float, touchY: Float, centerX: Float, centerY: Float): Float {
        val s = stats[key] ?: KeyStat()
        return (touchX - (centerX + s.dx)).pow(2) + (touchY - (centerY + s.dy)).pow(2)
    }

    /** Learn that a touch at (x,y) was intended for [key]. */
    fun learn(key: Char, x: Float, y: Float, centerX: Float, centerY: Float) {
        val s = stats.getOrPut(key) { KeyStat() }
        val targetDx = x - centerX
        val targetDy = y - centerY
        s.dx = (1f - alpha) * s.dx + alpha * targetDx
        s.dy = (1f - alpha) * s.dy + alpha * targetDy
        s.n++
        prefs.edit().putFloat("${key}_dx", s.dx).putFloat("${key}_dy", s.dy)
            .putInt("${key}_n", s.n).apply()
    }

    fun reset() {
        stats.values.forEach { it.dx = 0f; it.dy = 0f; it.n = 0 }
        prefs.edit().clear().apply()
    }
}
