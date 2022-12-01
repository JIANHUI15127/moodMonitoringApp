package com.example.moodmonitoringapp.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.moodmonitoringapp.data.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterViewModel : ViewModel(){
    val isRegistered = MutableLiveData<Boolean>()
    val error = MutableLiveData<String>()

    var username = ""
    var email = ""
    var password = ""
    var phoneNumber = ""

    fun register(mAuth: FirebaseAuth) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                val user = UserData(username, email,password,phoneNumber)
                FirebaseDatabase.getInstance().getReference("Users")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                    .setValue(user)
                    .addOnCompleteListener { task ->
                        isRegistered.value = task.isSuccessful
                        mAuth.currentUser!!.sendEmailVerification()
                    }
            } else {
                isRegistered.value = false
                error.value = it.exception!!.message.toString()
            }
        }
    }
}