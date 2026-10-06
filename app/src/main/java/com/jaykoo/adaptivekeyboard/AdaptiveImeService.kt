package com.jaykoo.adaptivekeyboard

import android.inputmethodservice.InputMethodService
import android.view.View

class AdaptiveImeService : InputMethodService() {
    private lateinit var model: AdaptiveModel
    private var lastKey: Char? = null
    private var lastTouch: Pair<Float,Float>? = null
    private var lastCell: KeyCell? = null
    private var deletedAt = 0L
    private var correction: Triple<Pair<Float,Float>, KeyCell, Char>? = null

    override fun onCreate() {
        super.onCreate()
        model = AdaptiveModel(this)
    }

    override fun onCreateInputView(): View {
        return KeyboardView(
            this, model,
            adaptiveEnabled = { getSharedPreferences("settings", MODE_PRIVATE).getBoolean("adaptive", true) },
            onKey = { c,x,y,cell ->
                val now = System.currentTimeMillis()
                correction?.let { (touch, oldCell, _) ->
                    if (now - deletedAt < 1200 && oldCell.char != c) {
                        model.learn(c, touch.first, touch.second, cell.cx, cell.cy)
                    }
                }
                correction = null
                currentInputConnection.commitText(c.toString(), 1)
                lastKey=c; lastTouch=x to y; lastCell=cell
            },
            onBackspace = {
                if (lastKey != null && lastTouch != null && lastCell != null) {
                    correction = Triple(lastTouch!!, lastCell!!, lastKey!!)
                    deletedAt = System.currentTimeMillis()
                }
                currentInputConnection.deleteSurroundingText(1,0)
                lastKey=null
            },
            onSpace = { currentInputConnection.commitText(" ",1); correction=null },
            onEnter = { currentInputConnection.commitText("\n",1); correction=null }
        )
    }
}
