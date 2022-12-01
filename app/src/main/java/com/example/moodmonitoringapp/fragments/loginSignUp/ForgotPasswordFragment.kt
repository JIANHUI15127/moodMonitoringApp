package com.example.moodmonitoringapp.fragments.loginSignUp

import android.graphics.Color
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.moodmonitoringapp.R
import com.example.moodmonitoringapp.databinding.FragmentForgotPasswordBinding
import com.example.moodmonitoringapp.viewModel.ForgotPasswordViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth


class ForgotPasswordFragment : Fragment() {

    private var _binding : FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!

    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentForgotPasswordBinding.inflate(layoutInflater, container, false)
        val view = binding.root

        mAuth = FirebaseAuth.getInstance()

        val viewModel = ViewModelProvider(this).get(ForgotPasswordViewModel::class.java)

        binding.resetPasswordButton.setOnClickListener {
            val email = binding.emailAdress.text.toString()
            if (email.isEmpty()){
                binding.emailAdress.error = "Enter email"
                binding.emailAdress.requestFocus()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.emailAdress.error = "Enter a valid email"
                binding.emailAdress.requestFocus()
                return@setOnClickListener
            }

            viewModel.email = email

            binding.progressBar.visibility = View.VISIBLE

            viewModel.resetPassword(mAuth,email)

        }

        fun recoverPassword(){

        }

        viewModel.emailSent.observe(viewLifecycleOwner, Observer {
            if (it){
                Snackbar.make(view, "Password change email sent!", Snackbar.LENGTH_SHORT).setBackgroundTint(
                    Color.GREEN).show()
                //findNavController().navigate(R.id.action_forgotPasswordFragment_to_loginFragment)
                replaceFragment(LoginFragment())
            }
        })

        viewModel.error.observe(viewLifecycleOwner, Observer {
            Snackbar.make(view, it, Snackbar.LENGTH_SHORT).setBackgroundTint(Color.RED).show()
            binding.progressBar.visibility = View.GONE
        })
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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