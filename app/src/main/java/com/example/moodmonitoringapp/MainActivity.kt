package com.example.moodmonitoringapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.moodmonitoringapp.databinding.ActivityMainBinding

import com.example.moodmonitoringapp.fragments.loginSignUp.LoginFragment
import com.example.moodmonitoringapp.fragments.loginSignUp.SignupFragment

import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding


    private val loginFragment = LoginFragment()
    //private val signUpFragment = SignupFragment()
/*    private val dashBoardFragment = DashBoardFragment()    //Currently not working, pending future works
    private val activeGoalsFragment = ActiveGoalsFragment()         //Testing purpose, need to remove
    private val completedGoalsFragment = CompletedGoalsFragment()   //Testing purpose, need to remove
    private val profileFragment = ProfileFragment()
    //private val communityFragment = CommunityFragment()              //Testing purpose,need to remove
    private val communityDashboardFragment = CommunityDashboardFragment()
    private val myActivityFragment = MyActivityFragment()           //Testing purpose, need to remove*/



    // FloatingActionButton for all the FABs
    private lateinit var addFab: FloatingActionButton
    private lateinit var communityFab: FloatingActionButton

    // These are taken to make visible and invisible along with FABs
    private lateinit var communityText: TextView

    // to check whether sub FAB buttons are visible or not.
    private var isAllFabsVisible: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(loginFragment)


    }

    private fun replaceFragment(fragment: Fragment){
        if(fragment!=null ){

            val fragmentTransaction  = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container,fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
    }

}

