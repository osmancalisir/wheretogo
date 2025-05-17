package edu.utexas.wheretogo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import edu.utexas.wheretogo.databinding.ForgotPasswordBinding
import edu.utexas.wheretogo.services.FirebaseService
import edu.utexas.wheretogo.R

class ForgotPasswordFragment : Fragment() {

    private var _binding: ForgotPasswordBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseService: FirebaseService

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = ForgotPasswordBinding.inflate(inflater, container, false)
        firebaseService = FirebaseService()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonResetPassword.setOnClickListener {
            val email = binding.editTextEmail.text.toString().trim()
            if (email.isNotEmpty()) {
                firebaseService.sendPasswordResetEmail(email) { success, exception ->
                    if (success) {
                        Toast.makeText(context, "Reset link sent to your email", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "Failed to send reset link: ${exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(context, "Please enter your email", Toast.LENGTH_LONG).show()
            }
        }

        binding.buttonBackToLogin.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, LoginFragment())
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
