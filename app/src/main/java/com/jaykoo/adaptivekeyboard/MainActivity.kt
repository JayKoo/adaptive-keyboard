package com.jaykoo.adaptivekeyboard
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import android.widget.*

class MainActivity:Activity(){
 override fun onCreate(b:Bundle?){super.onCreate(b);val prefs=getSharedPreferences("settings",MODE_PRIVATE);val metrics=MetricsStore(this)
  val l=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(48,64,48,48)}
  val stats=TextView(this).apply{textSize=18f;text=metrics.summary()}
  l.addView(TextView(this).apply{text="Adaptive Keyboard\n\n한/영 adaptive hitbox PoC";textSize=22f})
  l.addView(Switch(this).apply{text="Adaptive hitboxes";isChecked=prefs.getBoolean("adaptive",true);setOnCheckedChangeListener{_,v->prefs.edit().putBoolean("adaptive",v).apply()}})
  l.addView(Switch(this).apply{text="Touch heatmap overlay";isChecked=prefs.getBoolean("heatmap",false);setOnCheckedChangeListener{_,v->prefs.edit().putBoolean("heatmap",v).apply()}})
  l.addView(stats)
  l.addView(Button(this).apply{text="Refresh metrics";setOnClickListener{stats.text=metrics.summary()}})
  l.addView(Button(this).apply{text="Open keyboard settings";setOnClickListener{startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))}})
  l.addView(Button(this).apply{text="Choose keyboard";setOnClickListener{(getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).showInputMethodPicker()}})
  l.addView(Button(this).apply{text="Reset learning + metrics";setOnClickListener{AdaptiveModel(this@MainActivity).reset();metrics.reset();stats.text=metrics.summary()}})
  setContentView(l)
 }
}
