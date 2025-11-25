package com.beta.safalya_v2.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.beta.safalya_v2.auth.AuthActivity
import com.beta.safalya_v2.databinding.FragmentProfileBinding
import com.beta.safalya_v2.main.MainSharedViewModel
import com.beta.safalya_v2.util.UiState
import com.beta.safalya_v2.util.showToast
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: MainSharedViewModel by activityViewModels()
    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.logoutButton.setOnClickListener { logout() }
        observeUser()
    }

    private fun observeUser() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.userState.collect { state ->
                    when (state) {
                        is UiState.Success -> {
                            val user = state.data
                            binding.nameValue.text = user.name
                            binding.emailValue.text = user.email
                            binding.roleValue.text = user.role.uppercase()
                            binding.phoneValue.text = user.phone
                        }
                        is UiState.Error -> showToast(state.message)
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun logout() {
        profileViewModel.logout()
        val intent = Intent(requireContext(), AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

