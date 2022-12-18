package com.example.moodmonitoringapp.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moodmonitoringapp.data.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import java.io.File
import android.util.Base64
import androidx.core.content.ContentProviderCompat.requireContext
import com.bumptech.glide.Glide
import com.example.moodmonitoringapp.R

class UserProfileViewModel : ViewModel(){

    val userWithData = MutableLiveData<UserData?>()

    val failedToGetdata = MutableLiveData<Boolean>()

    fun signOut(mAuth: FirebaseAuth){
        mAuth.signOut()
    }

    fun getCurrentUser(mAuth: FirebaseAuth){
        viewModelScope.launch {
            val user = mAuth.currentUser
            val reference = FirebaseDatabase.getInstance().getReference("Users")
            val userId = user!!.uid

            reference.child(userId).addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userProfile = snapshot.getValue(UserData::class.java)



                    if (userProfile !=null){
                        userWithData.value = userProfile
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    failedToGetdata.value = true
                }

            })


        }
    }
}