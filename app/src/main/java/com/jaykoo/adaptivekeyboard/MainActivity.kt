package com.jaykoo.adaptivekeyboard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48,64,48,48)
        }
        layout.addView(TextView(this).apply {
            text = "Adaptive Keyboard\n\nThe visible keys stay fixed while invisible touch centers adapt to your corrections."
            textSize = 20f
        })
        layout.addView(Switch(this).apply {
            text = "Adaptive hitboxes"
            isChecked = prefs.getBoolean("adaptive", true)
            setOnCheckedChangeListener { _, checked -> prefs.edit().putBoolean("adaptive",checked).apply() }
        })
        layout.addView(Button(this).apply {
            text = "Open keyboard settings"
            setOnClickListener { startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)) }
        })
        layout.addView(Button(this).apply {
            text = "Choose keyboard"
            setOnClickListener { (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).showInputMethodPicker() }
        })
        layout.addView(Button(this).apply {
            text = "Reset learned touch model"
            setOnClickListener { AdaptiveModel(this@MainActivity).reset() }
        })
        setContentView(layout)
    }
}
