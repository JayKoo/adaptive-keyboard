package com.jaykoo.adaptivekeyboard

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.view.MotionEvent
import android.view.View

data class KeyCell(val char: Char, val rect: RectF) {
    val cx get() = rect.centerX()
    val cy get() = rect.centerY()
}

class KeyboardView(
    context: Context,
    private val model: AdaptiveModel,
    private val adaptiveEnabled: () -> Boolean,
    private val onKey: (Char, Float, Float, KeyCell) -> Unit,
    private val onBackspace: () -> Unit,
    private val onSpace: () -> Unit,
    private val onEnter: () -> Unit
) : View(context) {
    private val rows = listOf("qwertyuiop", "asdfghjkl", "zxcvbnm")
    private val keys = mutableListOf<KeyCell>()
    private val line = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE; strokeWidth = 2f }
    private val text = Paint(Paint.ANTI_ALIAS_FLAG).apply { textAlign = Paint.Align.CENTER; textSize = 42f }
    private var downX = 0f
    private var downY = 0f

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), 720)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        buildKeys()
        keys.forEach { k ->
            canvas.drawRect(k.rect, line)
            canvas.drawText(k.char.toString(), k.cx, k.cy + 14f, text)
        }
        val h = height / 4f
        canvas.drawText("⌫", width * .15f, h * 3.5f + 14f, text)
        canvas.drawText("space", width * .5f, h * 3.5f + 14f, text)
        canvas.drawText("↵", width * .85f, h * 3.5f + 14f, text)
    }

    private fun buildKeys() {
        keys.clear()
        val h = height / 4f
        rows.forEachIndexed { rowIndex, row ->
            val offset = when(rowIndex) { 1 -> width * .05f; 2 -> width * .15f; else -> 0f }
            val usable = when(rowIndex) { 1 -> width * .9f; 2 -> width * .7f; else -> width.toFloat() }
            val w = usable / row.length
            row.forEachIndexed { i, c ->
                keys += KeyCell(c, RectF(offset + i*w, rowIndex*h, offset + (i+1)*w, (rowIndex+1)*h))
            }
        }
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        when(e.actionMasked) {
            MotionEvent.ACTION_DOWN -> { downX=e.x; downY=e.y; return true }
            MotionEvent.ACTION_UP -> {
                val h = height / 4f
                if (e.y >= 3*h) {
                    when {
                        e.x < width*.3f -> onBackspace()
                        e.x > width*.75f -> onEnter()
                        else -> onSpace()
                    }
                    return true
                }
                if (keys.isEmpty()) buildKeys()
                val candidates = keys.filter { kotlin.math.abs(it.cy-e.y) < h*.75f }
                val key = if (adaptiveEnabled())
                    candidates.minByOrNull { model.score(it.char, downX, downY, it.cx, it.cy) }
                else candidates.minByOrNull { (downX-it.cx)*(downX-it.cx)+(downY-it.cy)*(downY-it.cy) }
                key?.let { onKey(it.char, downX, downY, it) }
                return true
            }
        }
        return super.onTouchEvent(e)
    }
}
