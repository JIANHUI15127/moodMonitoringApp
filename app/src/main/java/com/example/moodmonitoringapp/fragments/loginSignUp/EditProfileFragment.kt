package com.example.moodmonitoringapp.fragments.loginSignUp

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.moodmonitoringapp.R
import com.example.moodmonitoringapp.data.Communicator
import com.example.moodmonitoringapp.data.UserData
import com.example.moodmonitoringapp.databinding.FragmentEditProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class EditProfileFragment : Fragment() {
    private lateinit var binding: FragmentEditProfileBinding
    private lateinit var database : DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth

    private var username=""
    private var email=""
    private var phone = ""
    private var password = ""

    var inputPos: Int? = null
    var inputUsername : String = ""
    var inputEmail: String = ""
    var inputPassword: String = ""
    var inputPhoneNumber: String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentEditProfileBinding.inflate(inflater, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Users")

        val userUId = FirebaseAuth.getInstance().currentUser!!.uid

        //fetch data
        /*database.child(userUId).child("username").setValue().toString()
        database.child(userUId).child("email").setValue(binding.txtEmail).toString()
        database.child(userUId).child("phoneNumber").setValue(binding.txtPhone).toString()
        database.child(userUId).child("password").setValue(binding.txtPassword).toString()*/

        /*binding.txtName.setText(inputUsername)
        binding.txtEmail.setText(inputEmail)
        binding.txtPassword.setText(inputPassword)
        binding.txtPhone.setText(inputPhoneNumber)*/

        readData(userUId)

        binding.btnUpdate.setOnClickListener {

            username = binding.txtName.text.toString().trim()
            email = binding.txtEmail.text.toString().trim()
            phone = binding.txtPhone.text.toString().trim()
            password = binding.txtPassword.text.toString().trim()



            updateUser(username, phone,email,password)
        }

        return binding.root
    }

    private fun updateUser(username:String, phone:String, email:String, password:String){

        val user = UserData(username, phone, email, password)

        val userUId = FirebaseAuth.getInstance().currentUser!!.uid

        database.child(userUId)
            .setValue(user).addOnSuccessListener {
                binding.txtName.text.clear()
                binding.txtEmail.text.clear()
                binding.txtPhone.text.clear()
                binding.txtPassword.text.clear()
                Toast.makeText(context, "Edit Successfully!", Toast.LENGTH_SHORT).show()

                replaceFragment(UserProfileFragment())   // Need to change replace dashboard fragment

            }.addOnFailureListener{
                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
            }

    }

    private fun replaceFragment(fragment: Fragment){
        if(fragment!=null ){

            val fragmentTransaction  = this.parentFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container,fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
    }

    private fun readData(userUId: String){
        database = FirebaseDatabase.getInstance().getReference("Users")
        database.child(userUId).get().addOnSuccessListener {
            val username = it.child("username").value.toString()
            val email = it.child("email").value.toString()
            val password = it.child("password").value.toString()
            val phone = it.child("phoneNumber").value.toString()

            binding.txtName.setText(username)
            binding.txtEmail.setText(email)
            binding.txtPassword.setText(password)
            binding.txtPhone.setText(phone)

        }
    }


}

