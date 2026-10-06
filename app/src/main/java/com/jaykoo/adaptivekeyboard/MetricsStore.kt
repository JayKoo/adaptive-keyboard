package com.jaykoo.adaptivekeyboard
import android.content.Context

class MetricsStore(context: Context) {
    private val p=context.getSharedPreferences("metrics",Context.MODE_PRIVATE)
    fun key(){p.edit().putLong("keys",p.getLong("keys",0)+1).apply()}
    fun correction(){p.edit().putLong("corrections",p.getLong("corrections",0)+1).apply()}
    fun sample(x:Float,y:Float,key:Char){
        val old=p.getString("samples","") ?: ""
        val lines=(old+"\n$key,$x,$y").lines().takeLast(300).joinToString("\n")
        p.edit().putString("samples",lines).apply()
    }
    fun summary():String {
        val k=p.getLong("keys",0); val c=p.getLong("corrections",0)
        val rate=if(k==0L)0.0 else 100.0*c/k
        return "Keystrokes: $k\nCorrections: $c\nCorrection rate: %.2f%%".format(rate)
    }
    fun samples()=p.getString("samples","") ?: ""
    fun reset(){p.edit().clear().apply()}
}
