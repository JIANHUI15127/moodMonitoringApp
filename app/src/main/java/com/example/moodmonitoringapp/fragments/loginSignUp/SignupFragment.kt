package com.example.moodmonitoringapp.fragments.loginSignUp

import android.graphics.Color
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.moodmonitoringapp.R
import com.example.moodmonitoringapp.databinding.FragmentSignupBinding
import com.example.moodmonitoringapp.viewModel.RegisterViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth


class SignupFragment : Fragment() {
    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!



    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignupBinding.inflate(layoutInflater, container, false)
        val view = binding.root

        /*activity?.window?.decorView?.setOnApplyWindowInsetsListener { view, insets ->
            val insetsCompat = WindowInsetsCompat.toWindowInsetsCompat(insets, view)
            val isImeVisible = insetsCompat.isVisible(WindowInsetsCompat.Type.ime())
            // below line, do the necessary stuff:
//            binding.bottom.visibility =  if (isImeVisible) View.GONE else View.VISIBLE
            activity?.findViewById<View>(R.id.bottom_navigation)?.visibility  = View.GONE
            view.onApplyWindowInsets(insets)
        }*/

        /*activity?.window?.decorView?.setOnApplyWindowInsetsListener { view, insets ->
            val insetsCompat = WindowInsetsCompat.toWindowInsetsCompat(insets, view)
            val isImeVisible = insetsCompat.isVisible(WindowInsetsCompat.Type.ime())
            // below line, do the necessary stuff:
//            binding.bottom.visibility =  if (isImeVisible) View.GONE else View.VISIBLE
            activity?.findViewById<View>(R.id.bottom_navigation)?.visibility  = if (isImeVisible) View.GONE else View.VISIBLE
            view.onApplyWindowInsets(insets)
        }*/

        mAuth = FirebaseAuth.getInstance()

        val viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)

        binding.signupLoginTextView.setOnClickListener {
            replaceFragment(LoginFragment())
        }

        binding.signupSignupBtn.setOnClickListener {
            val email = binding.signupEmailEditText.text.toString()
            val password = binding.signupPasswordEditText.text.toString()
            val username = binding.signupNameEditText.text.toString()
            val phoneNumber = binding.signupPhoneEditText.text.toString()

            if (username.isEmpty()) {
                binding.signupNameEditText.error = "Username is required!"
                binding.signupNameEditText.requestFocus()
                return@setOnClickListener
            }
            if (email.isEmpty()) {
                binding.signupEmailEditText.error = "Email is required!"
                binding.signupEmailEditText.requestFocus()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.signupEmailEditText.error = "Please enter a valid email!"
                binding.signupEmailEditText.requestFocus()
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding.signupPasswordEditText.error = "Password is required!"
                binding.signupPasswordEditText.requestFocus()
                return@setOnClickListener
            }
            if (password.length < 6) {
                binding.signupPasswordEditText.error = "Password must be longer than 6!"
                binding.signupPasswordEditText.requestFocus()
                return@setOnClickListener
            }
            if(phoneNumber.isEmpty()){
                binding.signupPhoneEditText.error = "Phone Number is required!"
                binding.signupPhoneEditText.requestFocus()
                return@setOnClickListener
            }
            if(!Patterns.PHONE.matcher(phoneNumber).matches()){
                binding.signupPhoneEditText.error = "Please enter a valid phone number!"
                binding.signupPhoneEditText.requestFocus()
                return@setOnClickListener
            }

            binding.progressBar.visibility = View.VISIBLE

            viewModel.email = email
            viewModel.password = password
            viewModel.username = username
            viewModel.phoneNumber = phoneNumber
            viewModel.imageUrl = ""

            viewModel.register(mAuth)

        }

        viewModel.isRegistered.observe(viewLifecycleOwner, Observer {
            if (it) {
                Snackbar.make(view,"Successfully registered user", Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(Color.GREEN).show()
                binding.progressBar.visibility = View.GONE
                //findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
                replaceFragment(LoginFragment())
            } else {
                binding.progressBar.visibility = View.GONE
            }
        })

        viewModel.error.observe(viewLifecycleOwner, Observer {
            Snackbar.make(view,it, Snackbar.LENGTH_SHORT).setBackgroundTint(Color.RED).show()
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