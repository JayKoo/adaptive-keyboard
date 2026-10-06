package com.jaykoo.adaptivekeyboard

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

data class KeyCell(val char: Char, val rect: RectF) { val cx get()=rect.centerX(); val cy get()=rect.centerY() }

class KeyboardView(context:Context, private val model:AdaptiveModel,
 private val adaptiveEnabled:()->Boolean, private val koreanEnabled:()->Boolean,
 private val onKey:(Char,Float,Float,KeyCell)->Unit, private val onBackspace:()->Unit,
 private val onSpace:()->Unit, private val onEnter:()->Unit, private val onLanguage:()->Unit
):View(context){
 private val rows=listOf("qwertyuiop","asdfghjkl","zxcvbnm")
 private val ko=mapOf('q' to "ㅂ",'w' to "ㅈ",'e' to "ㄷ",'r' to "ㄱ",'t' to "ㅅ",'y' to "ㅛ",'u' to "ㅕ",'i' to "ㅑ",'o' to "ㅐ",'p' to "ㅔ",
  'a' to "ㅁ",'s' to "ㄴ",'d' to "ㅇ",'f' to "ㄹ",'g' to "ㅎ",'h' to "ㅗ",'j' to "ㅓ",'k' to "ㅏ",'l' to "ㅣ",
  'z' to "ㅋ",'x' to "ㅌ",'c' to "ㅊ",'v' to "ㅍ",'b' to "ㅠ",'n' to "ㅜ",'m' to "ㅡ")
 private val keys=mutableListOf<KeyCell>()
 private val line=Paint(Paint.ANTI_ALIAS_FLAG).apply{style=Paint.Style.STROKE;strokeWidth=2f}
 private val text=Paint(Paint.ANTI_ALIAS_FLAG).apply{textAlign=Paint.Align.CENTER;textSize=42f}
 private val heat=Paint(Paint.ANTI_ALIAS_FLAG).apply{style=Paint.Style.FILL;alpha=45}
 private var downX=0f; private var downY=0f
 private val recent=ArrayDeque<Pair<Float,Float>>()

 override fun onMeasure(w:Int,h:Int)=setMeasuredDimension(MeasureSpec.getSize(w),720)
 private fun build(){
  keys.clear(); val rh=height/4f
  rows.forEachIndexed{ri,row-> val off=when(ri){1->width*.05f;2->width*.15f;else->0f}; val usable=when(ri){1->width*.9f;2->width*.7f;else->width.toFloat()}; val kw=usable/row.length
   row.forEachIndexed{i,c->keys+=KeyCell(c,RectF(off+i*kw,ri*rh,off+(i+1)*kw,(ri+1)*rh))}
  }
 }
 override fun onDraw(c:Canvas){
  super.onDraw(c);build()
  if(context.getSharedPreferences("settings",Context.MODE_PRIVATE).getBoolean("heatmap",false))
   recent.forEach{c.drawCircle(it.first,it.second,18f,heat)}
  keys.forEach{k->c.drawRect(k.rect,line); val label=if(koreanEnabled()) ko[k.char]?:k.char.toString() else k.char.toString();c.drawText(label,k.cx,k.cy+14,text)}
  val h=height/4f;c.drawText(if(koreanEnabled())"한/영" else "EN/한",width*.08f,h*3.5f+14,text);c.drawText("⌫",width*.22f,h*3.5f+14,text);c.drawText("space",width*.52f,h*3.5f+14,text);c.drawText("↵",width*.88f,h*3.5f+14,text)
 }
 override fun onTouchEvent(e:MotionEvent):Boolean{
  when(e.actionMasked){
   MotionEvent.ACTION_DOWN->{downX=e.x;downY=e.y;return true}
   MotionEvent.ACTION_UP->{val h=height/4f
    if(e.y>=3*h){when{e.x<width*.15f->onLanguage();e.x<width*.32f->onBackspace();e.x>width*.78f->onEnter();else->onSpace()};invalidate();return true}
    if(keys.isEmpty())build();val cand=keys.filter{abs(it.cy-e.y)<h*.75f}
    val key=if(adaptiveEnabled())cand.minByOrNull{model.score(it.char,downX,downY,it.cx,it.cy)}else cand.minByOrNull{(downX-it.cx)*(downX-it.cx)+(downY-it.cy)*(downY-it.cy)}
    key?.let{recent.addLast(downX to downY);while(recent.size>80)recent.removeFirst();onKey(it.char,downX,downY,it);invalidate()};return true}
  };return super.onTouchEvent(e)
 }
}
