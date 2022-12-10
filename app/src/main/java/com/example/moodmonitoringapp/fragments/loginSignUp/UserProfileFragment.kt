package com.example.moodmonitoringapp.fragments.loginSignUp

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
import com.example.moodmonitoringapp.data.MoodEntrySQLiteDBHelper
import com.example.moodmonitoringapp.data.PastimeAdapter
import com.example.moodmonitoringapp.databinding.FragmentUserProfileBinding
import com.example.moodmonitoringapp.viewModel.UserProfileViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class UserProfileFragment : Fragment() {

    private var _binding : FragmentUserProfileBinding? = null
    private lateinit var db : DatabaseReference
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth

    lateinit var recyclerView: RecyclerView
    lateinit var recyclerViewAdapter: PastimeAdapter
    var pastimeEntries = ArrayList<String>()

    lateinit var databaseHelper: MoodEntrySQLiteDBHelper

    private lateinit var mAuth: FirebaseAuth

    var inputPos: Int? = null
    var inputUsername : String = ""
    var inputEmail: String = ""
    var inputPassword: String = ""
    var inputPhoneNumber: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserProfileBinding.inflate(layoutInflater, container, false)
        val view = binding.root

        mAuth = FirebaseAuth.getInstance()
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

        binding.userImage.setOnClickListener {
            replaceFragment(EditProfileFragment())
        }

        /*binding.userImage.setOnClickListener{
            val intent = Intent(this@UserProfileFragment.requireContext(),EditProfileActivity::class.java)
            startActivity(intent)

        }*/



        viewModel.userWithData.observe(viewLifecycleOwner, Observer {
            binding.email.text = it.email
            binding.username.text = it.username
            binding.phone.text = it.phoneNumber
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

        recyclerView = view?.findViewById(R.id.pastime_list)!!

        databaseHelper = MoodEntrySQLiteDBHelper(activity)
        pastimeEntries = ArrayList<String>()

        pastimeEntries = fetchPastimeData()

        if (activity != null) {
            recyclerViewAdapter = PastimeAdapter(activity?.applicationContext!!, pastimeEntries, requireActivity())
        }
        recyclerView.adapter = recyclerViewAdapter
        recyclerView.layoutManager = LinearLayoutManager(activity?.applicationContext!!)

        val logPastimeButton = view?.findViewById<Button>(R.id.add_pastime_button)
        val uniquePastime = view?.findViewById<EditText>(R.id.unique_pastime)

        logPastimeButton?.setOnClickListener({ view ->
            if (uniquePastime != null && uniquePastime.text.toString() != "") {
                submitPastimeEntry(uniquePastime)
                uniquePastime.getText().clear()
                onResume()
            }
        })

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun submitPastimeEntry(uniquePastime: EditText) {
        databaseHelper.savePastime(uniquePastime.text.toString().toUpperCase())
    }

    private fun fetchPastimeData(): ArrayList<String>{
        val cursor = databaseHelper.listPastimeEntries()

        val fromPastimeColumn = cursor.getColumnIndex(MoodEntrySQLiteDBHelper.PASTIME_ENTRY_COLUMN)

        if(cursor.getCount() == 0) {
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

    /*override fun passData(position: Int, username : String, phoneNumber: String, email: String, password: String) {
        val bundle = Bundle()
        bundle.putInt("input_pos", position)
        bundle.putString("ori_user_username",username)
        bundle.putString("ori_user_phone", phoneNumber)
        bundle.putString("ori_user_email",email)
        bundle.putString("ori_user_password", password)

        val transaction = this.parentFragmentManager.beginTransaction()
        val editDetailsFragment = EditProfileFragment()
        editDetailsFragment.arguments = bundle

        transaction.replace(R.id.fragment_container, editDetailsFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }*/
}