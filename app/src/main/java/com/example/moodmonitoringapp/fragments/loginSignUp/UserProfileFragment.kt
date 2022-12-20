package com.example.moodmonitoringapp.fragments.loginSignUp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moodmonitoringapp.R
import com.example.moodmonitoringapp.databinding.FragmentUserProfileBinding
import com.example.moodmonitoringapp.viewModel.UserProfileViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList
import android.util.Base64
import android.widget.Toast
import androidx.core.content.ContentProviderCompat
import com.bumptech.glide.Glide
import com.example.moodmonitoringapp.data.*
import com.google.firebase.storage.FirebaseStorage
import java.io.File


class UserProfileFragment : Fragment() {

    private var _binding : FragmentUserProfileBinding? = null
    private lateinit var db : DatabaseReference
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth

    lateinit var recyclerView: RecyclerView
    lateinit var recyclerViewAdapter: PastimeAdapter
    var pastimeEntries = ArrayList<String>()

    lateinit var databaseHelper: MoodEntrySQLiteDBHelper
    lateinit var moodEntries: ArrayList<MoodEntry>
    private lateinit var userUId: String


    private lateinit var mAuth: FirebaseAuth

    private var checkIn = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserProfileBinding.inflate(layoutInflater, container, false)
        val view = binding.root

        mAuth = FirebaseAuth.getInstance()

        userUId = FirebaseAuth.getInstance().currentUser!!.uid

        db = FirebaseDatabase.getInstance().getReference("Users")

        val viewModel = ViewModelProvider(this).get(UserProfileViewModel::class.java)

        viewModel.getCurrentUser(mAuth)

        binding.signOutButton.setOnClickListener {
            viewModel.signOut(mAuth)
            replaceFragment(LoginFragment())
        }

        binding.btnHistory.setOnClickListener {
            replaceFragment(HistoryCheckInFragment())
        }

        binding.btnInfo.setOnClickListener {
            replaceFragment(EditProfileFragment())
        }

        val myRef = FirebaseDatabase.getInstance().getReference("Check-In")

        myRef.child(userUId).get().addOnSuccessListener {

            val count = it.child("checkIn").value
            binding.tvCheckIn.text = count.toString()

        }




        /*myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.getValue(Int::class.java)
                binding.tvCheckIn.text = data.toString().trim().
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })*/


        /*val numRows = arguments?.getInt("num_rows")
        if (numRows != null) {
            binding.tvCheckIn.text = numRows.toString()
        }*/



        viewModel.userWithData.observe(viewLifecycleOwner, Observer {
            binding.email.text = it!!.email
            binding.username.text = it.username
            binding.phone.text = it.phoneNumber

            val img = it.imageUrl
            Glide.with(this).load(img).placeholder(R.drawable.ic_person).into(binding.userImage)
        })


        viewModel.failedToGetdata.observe(viewLifecycleOwner, Observer {
            if (it){
                Snackbar.make(view,"An error occurred!", Snackbar.LENGTH_SHORT).setBackgroundTint(
                    Color.RED).show()
            }
        })

        /*binding.userImage.setOnClickListener(){ v:View ->

            //Send data
            val bundle = Bundle()
            bundle.putString("ori_user_username",inputUsername)
            bundle.putString("ori_user_email", inputEmail)
            bundle.putString("ori_user_password",inputPassword)
            bundle.putString("ori_user_phone", inputPhoneNumber)

            // Navigate fragment
            val transaction = this.parentFragmentManager.beginTransaction()
            val userProfileFragment = UserProfileFragment()
            userProfileFragment.arguments = bundle

            transaction.replace(R.id.fragment_container, EditProfileFragment())
            transaction.addToBackStack(null)
            transaction.commit()

        }*/

        recyclerView = view.findViewById(R.id.pastime_list)!!

        databaseHelper = MoodEntrySQLiteDBHelper(activity)
        pastimeEntries = ArrayList<String>()

        pastimeEntries = fetchPastimeData()

        if (activity != null) {
            recyclerViewAdapter = PastimeAdapter(activity?.applicationContext!!, pastimeEntries, requireActivity())
        }
        recyclerView.adapter = recyclerViewAdapter
        recyclerView.layoutManager = LinearLayoutManager(activity?.applicationContext!!)

        val logPastimeButton = view.findViewById<Button>(R.id.add_pastime_button)
        val uniquePastime = view.findViewById<EditText>(R.id.unique_pastime)

        logPastimeButton?.setOnClickListener({ view ->
            if (uniquePastime != null && uniquePastime.text.toString() != "") {
                submitPastimeEntry(uniquePastime)
                uniquePastime.text.clear()
                onResume()
            }
        })

        return view
    }

    private fun getUserProfile(){
        val storageReference = FirebaseStorage.getInstance().reference.child("userProfile/$userUId.jpg")
        val localFile = File.createTempFile("tempImage", "jpg")
        storageReference.getFile(localFile).addOnSuccessListener{

            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            binding.userImage.setImageBitmap(bitmap)

        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun submitPastimeEntry(uniquePastime: EditText) {
        databaseHelper.savePastime(uniquePastime.text.toString().uppercase(Locale.getDefault()))
    }

    private fun fetchPastimeData(): ArrayList<String>{
        val cursor = databaseHelper.listPastimeEntries()

        val fromPastimeColumn = cursor.getColumnIndex(MoodEntrySQLiteDBHelper.PASTIME_ENTRY_COLUMN)

        if(cursor.count == 0) {
            Log.i("NO PASTIME ENTRIES", "Fetched data and found none.")
        } else {
            Log.i("PASTIME ENTRIES FETCHED", "Fetched data and found pastime entries.")
            pastimeEntries.clear()

            while (cursor.moveToNext()) {
                val nextPastime = cursor.getString(fromPastimeColumn)
                pastimeEntries.add(nextPastime.toString())
            }
        }
        return pastimeEntries
    }

    /*private fun checkUser() {
        val firebaseUser = mAuth.currentUser
        if(firebaseUser == null){
            binding.email.text ="Not Logged In"
            binding.phone.text =""
            binding.username.text =""
        }
        else{
            val email = firebaseUser.email
            binding.subTitleTv.text = email

        }
    }*/

    private fun replaceFragment(fragment: Fragment){
        if(fragment!=null ){

            val fragmentTransaction  = this.parentFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container,fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
    }

}