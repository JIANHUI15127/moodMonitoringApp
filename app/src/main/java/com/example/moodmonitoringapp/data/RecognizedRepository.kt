package com.example.moodmonitoringapp.data

import com.example.moodmonitoringapp.data.RxFirebase
import com.example.moodmonitoringapp.data.raw.EmotionRaw
import com.example.moodmonitoringapp.data.raw.RecognizedRaw
import com.example.moodmonitoringapp.model.Recognized
import com.google.firebase.Timestamp
import io.reactivex.Observable


class RecognizedRepository {

    val recognized: MutableList<Recognized> = mutableListOf()

    fun fetch(): Observable<List<Recognized>> = RxFirebase.getCollection("info/recognized")
        .map { response -> response.map { it.toObject(RecognizedRaw::class.java) } }
        .map { response -> response.filterNotNull() }
        .map { response ->
            val converted = response.map { it.convert() }
            recognized.addAll(converted)
            converted
        }

    fun save(data: Recognized): Observable<Recognized> = Observable.just(data)
        .map {
            this.recognized.add(data)
            RecognizedRaw(
                id = it.id,
                image = it.image,
                recognize_time = Timestamp(it.date),
                recognizer_name = it.recognizer.name,
                emotions = it.emotions.map { emotion -> EmotionRaw(emotion.key, emotion.value) }
            )
        }
        .flatMap {
            RxFirebase
                .saveDocument("info/recognized/${data.id}", it)
                .map { data }
        }
}