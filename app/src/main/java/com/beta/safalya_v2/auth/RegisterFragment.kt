package com.beta.safalya_v2.auth

import android.os.Bundle
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
        observeState()
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
        viewModel.register(name, phone, email, password)
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.registerState.collect { state ->
                    when (state) {
                        is UiState.Idle -> binding.progressBar.setVisible(false)
                        is UiState.Loading -> binding.progressBar.setVisible(true)
                        is UiState.Success -> {
                            binding.progressBar.setVisible(false)
                            findNavController().navigate(R.id.action_registerFragment_to_roleSelectionFragment)
                            viewModel.resetRegisterState()
                        }
                        is UiState.Error -> {
                            binding.progressBar.setVisible(false)
                            showToast(state.message.ifEmpty { getString(R.string.error_generic) })
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

