package com.example.moodmonitoringapp.data.raw

import com.example.moodmonitoringapp.model.Recognized
import com.example.moodmonitoringapp.model.RecognizerName
import com.google.firebase.Timestamp

class RecognizedRaw {

    lateinit var id: String
    lateinit var image: String
    lateinit var recognize_time: Timestamp
    lateinit var recognizer_name: String
    lateinit var emotions: List<EmotionRaw>

    constructor()

    constructor(id: String, image: String, recognize_time: Timestamp, recognizer_name: String, emotions: List<EmotionRaw>) {
        this.id = id
        this.image = image
        this.recognize_time = recognize_time
        this.recognizer_name = recognizer_name
        this.emotions = emotions
    }

    fun convert() = Recognized(
        id = id,
        image = image,
        date = recognize_time.toDate(),
        emotions = emotions.map { Pair(it.name, it.value) }.toMap(),
        recognizer = RecognizerName.valueOf(recognizer_name)
    )
}