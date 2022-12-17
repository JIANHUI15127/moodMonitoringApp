package com.example.moodmonitoringapp.util.extensions

fun CharSequence.toTitleCase(): CharSequence {
    val sb = StringBuilder()
    var isFirstSymbol = true
    this.forEach {
        if (it.isLetter() && isFirstSymbol) {
            sb.append(it.titlecaseChar())
            isFirstSymbol = false
        } else {
            sb.append(it.lowercaseChar())
        }
    }
    return sb.toString()
}