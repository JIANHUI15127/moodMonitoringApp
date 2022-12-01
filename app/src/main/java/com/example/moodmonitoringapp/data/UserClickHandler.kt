package com.example.moodmonitoringapp.data

import com.example.moodmonitoringapp.data.UserData


interface Communicator {
    fun passData(position: Int,
                 username : String,
                 phoneNumber: String,
                 email: String ,
                 password: String)
}