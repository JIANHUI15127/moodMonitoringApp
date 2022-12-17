package com.example.moodmonitoringapp.fragments.moodCheckIn

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowInsetsCompat
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

        activity?.findViewById<View>(R.id.bottom_navigation)?.visibility = View.VISIBLE

        /*activity?.window?.decorView?.setOnApplyWindowInsetsListener { view, insets ->
            val insetsCompat = WindowInsetsCompat.toWindowInsetsCompat(insets, view)
            val isImeVisible = insetsCompat.isVisible(WindowInsetsCompat.Type.ime())
            // below line, do the necessary stuff:
//            binding.bottom.visibility =  if (isImeVisible) View.GONE else View.VISIBLE
            activity?.findViewById<View>(R.id.bottom_navigation)?.visibility  = if (isImeVisible) View.GONE else View.VISIBLE
            view.onApplyWindowInsets(insets)
        }*/

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
            val intent = Intent(this@MoodCheckInFragment.requireContext(), RecognitionActivity::class.java)
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

