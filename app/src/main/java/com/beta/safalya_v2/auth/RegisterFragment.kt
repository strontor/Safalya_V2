package com.beta.safalya_v2.auth

import android.os.Bundle
import com.beta.safalya_v2.MainActivity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.beta.safalya_v2.R
import com.beta.safalya_v2.databinding.FragmentRegisterBinding
import com.beta.safalya_v2.util.UiState
import com.beta.safalya_v2.util.setVisible
import com.beta.safalya_v2.util.showToast
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.loginLink.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.registerButton.setOnClickListener { validateInputs() }

    }

    private fun validateInputs() {
        val name = binding.nameInput.text.toString().trim()
        val phone = binding.phoneInput.text.toString().trim()
        val email = binding.emailInput.text.toString().trim()
        val password = binding.passwordInput.text.toString().trim()

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showToast(getString(R.string.error_all_fields_required))
            return
        }

        // read selected role from UI
        val role = if (binding.radioFarmer.isChecked) "farmer" else "buyer"

        viewModel.register(name, phone, email, password, role) {
            // registration completed → go straight to MainActivity
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

