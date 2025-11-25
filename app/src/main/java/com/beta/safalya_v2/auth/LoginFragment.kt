package com.beta.safalya_v2.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.beta.safalya_v2.MainActivity
import com.beta.safalya_v2.R
import com.beta.safalya_v2.databinding.FragmentLoginBinding
import com.beta.safalya_v2.util.UiState
import com.beta.safalya_v2.util.launchActivity
import com.beta.safalya_v2.util.setVisible
import com.beta.safalya_v2.util.showToast
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.registerLink.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        binding.loginButton.setOnClickListener { handleLogin() }
        observeState()
    }

    private fun handleLogin() {
        val email = binding.emailInput.text.toString().trim()
        val password = binding.passwordInput.text.toString().trim()
        if (email.isEmpty() || password.isEmpty()) {
            showToast(getString(R.string.error_credentials_required))
            return
        }
        viewModel.login(email, password)
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loginState.collect { state ->
                    when (state) {
                        is UiState.Idle -> binding.progressBar.setVisible(false)
                        is UiState.Loading -> binding.progressBar.setVisible(true)
                        is UiState.Success -> {
                            binding.progressBar.setVisible(false)
                            launchActivity<MainActivity> {
                                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            viewModel.resetLoginState()
                            requireActivity().finish()
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

