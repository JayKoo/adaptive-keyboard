package com.jaykoo.adaptivekeyboard

import android.inputmethodservice.InputMethodService
import android.view.View

class AdaptiveImeService:InputMethodService(){
 private lateinit var model:AdaptiveModel; private lateinit var metrics:MetricsStore
 private val hangul=HangulComposer(); private var korean=true
 private var lastKey:Char?=null;private var lastTouch:Pair<Float,Float>?=null;private var lastCell:KeyCell?=null
 private var deletedAt=0L;private var correction:Triple<Pair<Float,Float>,KeyCell,Char>?=null
 private val ko=mapOf('q' to "ㅂ",'w' to "ㅈ",'e' to "ㄷ",'r' to "ㄱ",'t' to "ㅅ",'y' to "ㅛ",'u' to "ㅕ",'i' to "ㅑ",'o' to "ㅐ",'p' to "ㅔ",'a' to "ㅁ",'s' to "ㄴ",'d' to "ㅇ",'f' to "ㄹ",'g' to "ㅎ",'h' to "ㅗ",'j' to "ㅓ",'k' to "ㅏ",'l' to "ㅣ",'z' to "ㅋ",'x' to "ㅌ",'c' to "ㅊ",'v' to "ㅍ",'b' to "ㅠ",'n' to "ㅜ",'m' to "ㅡ")
 override fun onCreate(){super.onCreate();model=AdaptiveModel(this);metrics=MetricsStore(this)}
 private fun flush(){val s=hangul.flush();if(s.isNotEmpty()){currentInputConnection.finishComposingText();currentInputConnection.commitText(s,1)}}
 override fun onCreateInputView():View{
  lateinit var view:KeyboardView
  view=KeyboardView(this,model,{getSharedPreferences("settings",MODE_PRIVATE).getBoolean("adaptive",true)},{korean},
   {c,x,y,cell->
    metrics.key();metrics.sample(x,y,c);val now=System.currentTimeMillis()
    correction?.let{(touch,_,old)->if(now-deletedAt<1200&&old!=c){model.learn(c,touch.first,touch.second,cell.cx,cell.cy);metrics.correction()}}
    correction=null
    if(korean){val j=ko[c]?:c.toString();val (committed,composing)=hangul.input(j);if(committed.isNotEmpty())currentInputConnection.commitText(committed,1);currentInputConnection.setComposingText(composing,1)}
    else {flush();currentInputConnection.commitText(c.toString(),1)}
    lastKey=c;lastTouch=x to y;lastCell=cell
   },
   {
    if(lastKey!=null&&lastTouch!=null&&lastCell!=null){correction=Triple(lastTouch!!,lastCell!!,lastKey!!);deletedAt=System.currentTimeMillis()}
    if(korean&&hangul.buffer.isNotEmpty()){val s=hangul.backspace();if(s.isEmpty())currentInputConnection.finishComposingText() else currentInputConnection.setComposingText(s,1)}
    else currentInputConnection.deleteSurroundingText(1,0);lastKey=null
   },
   {flush();currentInputConnection.commitText(" ",1);correction=null},
   {flush();currentInputConnection.commitText("\n",1);correction=null},
   {flush();korean=!korean;view.invalidate()}
  );return view
 }
}
