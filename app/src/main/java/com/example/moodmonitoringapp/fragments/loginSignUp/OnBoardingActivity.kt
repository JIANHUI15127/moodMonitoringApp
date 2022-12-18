package com.example.moodmonitoringapp.fragments.loginSignUp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.moodmonitoringapp.R
import com.example.moodmonitoringapp.databinding.ActivityOnBoardingBinding


class OnBoardingActivity : AppCompatActivity() {
    private lateinit var binding:ActivityOnBoardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGetStarted.setOnClickListener {
            /*val intent = Intent(this, SignupFragment::class.java)
            startActivity(intent)*/

            val myFragment = LoginFragment()
            val fragment : Fragment? =

            supportFragmentManager.findFragmentByTag(LoginFragment::class.java.simpleName)
            if(fragment !is LoginFragment){
                supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, myFragment, LoginFragment::class.java.simpleName)
                    .commit()
            }
        }




    }
}