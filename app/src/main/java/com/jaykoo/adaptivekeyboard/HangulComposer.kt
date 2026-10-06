package com.jaykoo.adaptivekeyboard

class HangulComposer {
    private val choseong = "ㄱㄲㄴㄷㄸㄹㅁㅂㅃㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎ"
    private val jungseong = "ㅏㅐㅑㅒㅓㅔㅕㅖㅗㅘㅙㅚㅛㅜㅝㅞㅟㅠㅡㅢㅣ"
    private val jongseong = listOf("", "ㄱ","ㄲ","ㄳ","ㄴ","ㄵ","ㄶ","ㄷ","ㄹ","ㄺ","ㄻ","ㄼ","ㄽ","ㄾ","ㄿ","ㅀ","ㅁ","ㅂ","ㅄ","ㅅ","ㅆ","ㅇ","ㅈ","ㅊ","ㅋ","ㅌ","ㅍ","ㅎ")
    private val vowelMerge = mapOf("ㅗㅏ" to "ㅘ","ㅗㅐ" to "ㅙ","ㅗㅣ" to "ㅚ","ㅜㅓ" to "ㅝ","ㅜㅔ" to "ㅞ","ㅜㅣ" to "ㅟ","ㅡㅣ" to "ㅢ")
    private val finalMerge = mapOf("ㄱㅅ" to "ㄳ","ㄴㅈ" to "ㄵ","ㄴㅎ" to "ㄶ","ㄹㄱ" to "ㄺ","ㄹㅁ" to "ㄻ","ㄹㅂ" to "ㄼ","ㄹㅅ" to "ㄽ","ㄹㅌ" to "ㄾ","ㄹㅍ" to "ㄿ","ㄹㅎ" to "ㅀ","ㅂㅅ" to "ㅄ")
    private val finalSplit = mapOf("ㄳ" to ("ㄱ" to "ㅅ"),"ㄵ" to ("ㄴ" to "ㅈ"),"ㄶ" to ("ㄴ" to "ㅎ"),"ㄺ" to ("ㄹ" to "ㄱ"),"ㄻ" to ("ㄹ" to "ㅁ"),"ㄼ" to ("ㄹ" to "ㅂ"),"ㄽ" to ("ㄹ" to "ㅅ"),"ㄾ" to ("ㄹ" to "ㅌ"),"ㄿ" to ("ㄹ" to "ㅍ"),"ㅀ" to ("ㄹ" to "ㅎ"),"ㅄ" to ("ㅂ" to "ㅅ"))
    var buffer = ""; private set

    private fun compose(l: String, v: String, t: String=""): String {
        val li=choseong.indexOf(l); val vi=jungseong.indexOf(v); val ti=jongseong.indexOf(t)
        if(li<0 || vi<0 || ti<0) return l+v+t
        return ((0xAC00 + (li*21+vi)*28+ti).toChar()).toString()
    }

    fun input(jamo: String): Pair<String,String> {
        if(buffer.isEmpty()){ buffer=jamo; return "" to buffer }
        val chars=buffer.toList()
        val last=chars.last().toString()
        if(jungseong.contains(jamo)) {
            if(chars.size==1 && choseong.contains(last)){ buffer=last+jamo; return "" to compose(last,jamo) }
            if(chars.size>=2) {
                val l=chars[0].toString(); val v=chars[1].toString()
                if(chars.size==2) {
                    val merged=vowelMerge[v+jamo]
                    if(merged!=null){buffer=l+merged; return "" to compose(l,merged)}
                } else {
                    val t=chars.drop(2).joinToString("")
                    val split=finalSplit[t]
                    val remain=split?.first ?: ""; val next=split?.second ?: t
                    val committed=compose(l,v,remain)
                    buffer=next+jamo
                    return committed to compose(next,jamo)
                }
            }
        } else if(choseong.contains(jamo) && chars.size>=2) {
            val l=chars[0].toString(); val v=chars[1].toString()
            if(chars.size==2 && jongseong.contains(jamo)){buffer+=jamo; return "" to compose(l,v,jamo)}
            if(chars.size>=3){
                val t=chars.drop(2).joinToString(""); val merged=finalMerge[t+jamo]
                if(merged!=null){buffer=l+v+merged; return "" to compose(l,v,merged)}
                val committed=compose(l,v,t); buffer=jamo; return committed to jamo
            }
        }
        val committed=display(); buffer=jamo
        return committed to jamo
    }

    fun display(): String {
        val c=buffer.toList(); if(c.size<2) return buffer
        return compose(c[0].toString(),c[1].toString(),c.drop(2).joinToString(""))
    }
    fun flush(): String { val s=display(); buffer=""; return s }
    fun backspace(): String {
        if(buffer.isNotEmpty()) buffer=buffer.dropLast(1)
        return display()
    }
}
