package com.exa.android.loctrace.Fragments.Authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.exa.android.loctrace.Fragments.UsersEntryFragment
import com.exa.android.loctrace.Helper.AppConstants
import com.exa.android.loctrace.R
import com.exa.android.loctrace.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class SignUpFragment : Fragment() {

    private var _binding : FragmentSignUpBinding?= null
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding=FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth= FirebaseAuth.getInstance()
        binding.btnSignUp.setOnClickListener {

            signUpUser()
        }
        binding.loginNavigation.setOnClickListener {
            // navigate to sing in
            findNavController().navigate(R.id.action_signUpFragment_to_signInFragment)
        }
    }

    private fun signUpUser() {
        val email:String=binding.emailAddressEt.text.toString()
        val password=binding.passwordEt.text.toString()
        val confirmPassword=binding.cnfPasswordEt.text.toString()


        val verifyEmail= AppConstants.verifyEmail(email)
        val verifyPassword= AppConstants.verifyPassword(password)
        val verifyConfirmPassword= AppConstants.verifyConfirmPassword(password, confirmPassword)

        if(!verifyPassword.first && !verifyEmail.first && !verifyConfirmPassword.first){
            binding.emailTextInputLayout.error=verifyEmail.second
            binding.passwordTextInputLayout.error=verifyPassword.second
            binding.confirmPasswordTextInputLayout.error=verifyConfirmPassword.second
            return
        }else if(!verifyPassword.first && !verifyEmail.first){
            binding.emailTextInputLayout.error=verifyEmail.second
            binding.passwordTextInputLayout.error=verifyPassword.second
            return
        }else if(!verifyPassword.first && !verifyConfirmPassword.first){
            binding.passwordTextInputLayout.error=verifyPassword.second
            binding.confirmPasswordTextInputLayout.error=verifyConfirmPassword.second
            return
        }else if(!verifyConfirmPassword.first && !verifyEmail.first){
            binding.emailTextInputLayout.error=verifyEmail.second
            binding.confirmPasswordTextInputLayout.error=verifyConfirmPassword.second
            return
        }else if(!verifyConfirmPassword.first){
            binding.confirmPasswordTextInputLayout.error=verifyConfirmPassword.second
            return
        }else if(!verifyEmail.first) {
            binding.emailTextInputLayout.error = verifyEmail.second
            return
        }else if(!verifyPassword.first){
            binding.passwordTextInputLayout.error=verifyPassword.second
            return
        }
        registerWithEmail(email, password)
    }

    private fun registerWithEmail(email: String, password: String) {

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{
                if(it.isSuccessful){
                    Toast.makeText(requireContext(), "Sign Up Successful: ${it.result.user?.email}", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_signUpFragment_to_signInFragment)
                }else{
                    Toast.makeText(requireContext(), "Error creating user: ${it.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = firebaseAuth.currentUser

        if (currentUser != null ) {
            AppConstants.userId = currentUser.displayName
            findNavController().navigate(R.id.action_signUpFragment_to_signInFragment)
        }
    }
}