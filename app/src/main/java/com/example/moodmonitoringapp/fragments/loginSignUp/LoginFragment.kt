package com.example.moodmonitoringapp.fragments.loginSignUp

import android.graphics.Color
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsCompat.toWindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import com.example.moodmonitoringapp.R
import com.example.moodmonitoringapp.databinding.FragmentLoginBinding
import com.example.moodmonitoringapp.fragments.moodCheckIn.MoodCheckInFragment
import com.example.moodmonitoringapp.viewModel.LoginViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        val view = binding.root

        activity?.findViewById<View>(R.id.bottom_navigation)?.visibility = View.GONE


        /*activity?.window?.decorView?.setOnApplyWindowInsetsListener { view, insets ->
            val insetsCompat = WindowInsetsCompat.toWindowInsetsCompat(insets, view)
            val isImeVisible = insetsCompat.isVisible(WindowInsetsCompat.Type.ime())
            // below line, do the necessary stuff:
//            binding.bottom.visibility =  if (isImeVisible) View.GONE else View.VISIBLE
            activity?.findViewById<View>(R.id.bottom_navigation)?.visibility  = View.GONE
            view.onApplyWindowInsets(insets)
        }*/

        mAuth = FirebaseAuth.getInstance()

        binding.loginSignupTextView.setOnClickListener {
            //findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
            replaceFragment(SignupFragment())
        }

        val viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)


        binding.loginForgotTv.setOnClickListener {
            //findNavController().navigate(R.id.action_loginFragment_to_otpActivity)
            replaceFragment(ForgotPasswordFragment())
        }

        binding.loginBtn.setOnClickListener {
            val email = binding.loginEmailEditText.text.toString()
            val password = binding.loginPasswordEditText.text.toString()

            if (email.isEmpty()) {
                binding.loginEmailEditText.error = "Enter your email!"
                binding.loginEmailEditText.requestFocus()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.loginEmailEditText.error = "Please enter a valid email!"
                binding.loginEmailEditText.requestFocus()
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding.loginPasswordEditText.error = "Enter password!"
                binding.loginPasswordEditText.requestFocus()
                return@setOnClickListener
            }
            if (password.length < 6) {
                binding.loginPasswordEditText.error = "Enter a password longer than 6!"
                binding.loginPasswordEditText.requestFocus()
                return@setOnClickListener
            }

            binding.progressBar.visibility = View.VISIBLE

            viewModel.email = email
            viewModel.password = password

            viewModel.userLogin(mAuth)
        }

        viewModel.isAbleToLogin.observe(viewLifecycleOwner, Observer {
            if (it) {
                replaceFragment(MoodCheckInFragment())
            } else {
                binding.progressBar.visibility = View.GONE
            }
        })

        viewModel.error.observe(viewLifecycleOwner, Observer {
            Snackbar.make(view, it, Snackbar.LENGTH_SHORT).setBackgroundTint(Color.RED).show()
        })

        /*viewModel.emailVerified.observe(viewLifecycleOwner, Observer {
            if (!it){
                Snackbar.make(view, "Please verify your account!", Snackbar.LENGTH_LONG).setBackgroundTint(Color.RED).show()
                binding.progressBar.visibility = View.GONE
            }
        })*/

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