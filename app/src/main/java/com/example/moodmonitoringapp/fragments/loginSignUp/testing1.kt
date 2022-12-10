package com.example.moodmonitoringapp.fragments.loginSignUp

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.service.voice.VoiceInteractionSession.ActivityId
import android.util.Log
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.example.moodmonitoringapp.R
import com.example.moodmonitoringapp.databinding.ActivityTesting1Binding
import com.google.firebase.messaging.FirebaseMessaging


class testing1 : AppCompatActivity() {

    private lateinit var _binding : ActivityTesting1Binding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityTesting1Binding.inflate(layoutInflater)
        setContentView(binding.root)


        /*binding.buttonSubscribe.setOnClickListener {
            Log.d(javaClass.name, "Subscribing to discount-offers topic")

            FirebaseMessaging.getInstance().subscribeToTopic("discount-offers")
                .addOnCompleteListener { task ->
                    showToast("Subscribed! You will get all discount offers notifications")
                    if (!task.isSuccessful) {
                        showToast("Failed! Try again.")
                    }
                }
        }*/
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

    }

}