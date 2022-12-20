package com.example.moodmonitoringapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.moodmonitoringapp.databinding.ActivityMainBinding

import com.example.moodmonitoringapp.fragments.loginSignUp.LoginFragment
import com.example.moodmonitoringapp.fragments.loginSignUp.OnBoardingActivity
import com.example.moodmonitoringapp.fragments.loginSignUp.UserProfileFragment
import com.example.moodmonitoringapp.fragments.moodCheckIn.MoodCheckInFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    private val loginFragment = LoginFragment()
    private val profileFragment = UserProfileFragment()
    private val onBoardingActivity = OnBoardingActivity()
    //private val signUpFragment = SignupFragment()
/*    private val dashBoardFragment = DashBoardFragment()    //Currently not working, pending future works
    private val activeGoalsFragment = ActiveGoalsFragment()         //Testing purpose, need to remove
    private val completedGoalsFragment = CompletedGoalsFragment()   //Testing purpose, need to remove

    //private val communityFragment = CommunityFragment()              //Testing purpose,need to remove
    private val communityDashboardFragment = CommunityDashboardFragment()
    private val myActivityFragment = MyActivityFragment()           //Testing purpose, need to remove*/


    // FloatingActionButton for all the FABs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(loginFragment)



        binding.bottomNavigation.setOnNavigationItemSelectedListener{
            when(it.itemId){
                R.id.home -> replaceFragment(MoodCheckInFragment())
                //R.id.home -> replaceFragment(recommendationFragment)        //Testing purpose
                //R.id.stats ->replaceFragment(statsFragment)
                //R.id.goals -> replaceFragment(dashBoardFragment)   //Currently not working completely, pending future works
                //R.id.goals -> replaceFragment(activeGoalsFragment)   //Testing purpose, need to remove
                //R.id.goals -> replaceFragment(completedGoalsFragment)   //Testing purpose, need to remove
                R.id.profile -> replaceFragment(profileFragment)
            }
            true
        }

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






