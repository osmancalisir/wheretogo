package edu.utexas.wheretogo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import edu.utexas.wheretogo.databinding.RegisterBinding
import edu.utexas.wheretogo.services.FirebaseService
import edu.utexas.wheretogo.R

class RegisterFragment : Fragment() {

    private var _binding: RegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseService: FirebaseService

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = RegisterBinding.inflate(inflater, container, false)
        firebaseService = FirebaseService()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonRegister.setOnClickListener {
            val email = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Email and password cannot be empty.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            firebaseService.registerUser(email, password) { user, exception ->
                if (user != null) {
                    Toast.makeText(context, "Registration successful!", Toast.LENGTH_SHORT).show()
                    navigateToLoginFragment()
                } else {
                    Toast.makeText(context, "Registration failed: ${exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.textViewLogin.setOnClickListener {
            navigateToLoginFragment()
        }
    }

    private fun navigateToLoginFragment() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, LoginFragment())
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
