package com.example.moodmonitoringapp.data.raw

class EmotionRaw {

    lateinit var name: String
    var value: Int = 0

    constructor()

    constructor(name: String, value: Int) {
        this.name = name
        this.value = value
    }
}