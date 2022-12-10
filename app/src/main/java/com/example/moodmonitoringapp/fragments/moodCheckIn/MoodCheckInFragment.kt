package com.example.moodmonitoringapp.fragments.moodCheckIn

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.moodmonitoringapp.R
import com.example.moodmonitoringapp.databinding.FragmentEditProfileBinding
import com.example.moodmonitoringapp.databinding.FragmentMoodCheckInBinding
import com.example.moodmonitoringapp.fragments.loginSignUp.HistoryCheckInFragment


class MoodCheckInFragment : Fragment() {

    private lateinit var binding: FragmentMoodCheckInBinding



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMoodCheckInBinding.inflate(inflater, container, false)

        binding.ratingView.setOnClickListener {
            replaceFragment(CheckInRatingFragment())

        }

        binding.emojiView.setOnClickListener {
            replaceFragment(CheckInEmojiFragment())
        }

        binding.colorView.setOnClickListener {
            replaceFragment(CheckInColorFragment())

        }

        binding.cameraView.setOnClickListener {
            val intent = Intent(this@MoodCheckInFragment.requireContext(), CheckInCameraActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    private fun replaceFragment(fragment: Fragment){
        if(fragment!=null ){

            val fragmentTransaction  = this.parentFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container,fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
    }

}

