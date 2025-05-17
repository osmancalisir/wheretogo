package edu.utexas.wheretogo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import edu.utexas.wheretogo.databinding.LoginBinding
import edu.utexas.wheretogo.services.FirebaseService
import edu.utexas.wheretogo.R

class LoginFragment : Fragment() {

    private var _binding: LoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseService: FirebaseService

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = LoginBinding.inflate(inflater, container, false)
        firebaseService = FirebaseService()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonLogin.setOnClickListener {
            val email = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Email and password cannot be empty.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            firebaseService.loginUser(email, password) { user, exception ->
                if (user != null) {
                    Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
                    navigateToMapFragment()
                } else {
                    Toast.makeText(context, "Login failed: ${exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.textViewSignUp.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, RegisterFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.textViewForgotPassword.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ForgotPasswordFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun navigateToMapFragment() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, MapFragment())
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
