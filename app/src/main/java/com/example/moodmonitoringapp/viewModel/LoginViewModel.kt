package com.example.moodmonitoringapp.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel : ViewModel() {
    val isAbleToLogin = MutableLiveData<Boolean>()
    //val emailVerified = MutableLiveData<Boolean>()
    val error = MutableLiveData<String>()

    var email = ""
    var password = ""

    fun userLogin(mAuth: FirebaseAuth){
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful){
                val user = FirebaseAuth.getInstance().currentUser
                isAbleToLogin.value = true

                /*if (user!!.isEmailVerified){
                    isAbleToLogin.value = true
                }else{
                    emailVerified.value = false
                }*/

            }else{
                isAbleToLogin.value = false
                error.value = it.exception!!.message
            }

        }
    }
}