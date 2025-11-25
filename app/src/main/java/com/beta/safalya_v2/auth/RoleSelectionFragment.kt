package com.beta.safalya_v2.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.beta.safalya_v2.MainActivity
import com.beta.safalya_v2.data.model.UserRole
import com.beta.safalya_v2.databinding.FragmentRoleSelectionBinding
import com.beta.safalya_v2.util.UiState
import com.beta.safalya_v2.util.setVisible
import com.beta.safalya_v2.util.showToast
import kotlinx.coroutines.launch

class RoleSelectionFragment : Fragment() {

    private var _binding: FragmentRoleSelectionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRoleSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.farmerCard.setOnClickListener { viewModel.completeRoleSelection(UserRole.FARMER) }
        binding.buyerCard.setOnClickListener { viewModel.completeRoleSelection(UserRole.BUYER) }
        observeState()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.roleState.collect { state ->
                    when (state) {
                        is UiState.Idle -> binding.progressBar.setVisible(false)
                        is UiState.Loading -> binding.progressBar.setVisible(true)
                        is UiState.Success -> {
                            binding.progressBar.setVisible(false)
                            launchMain()
                            viewModel.resetRoleState()
                        }
                        is UiState.Error -> {
                            binding.progressBar.setVisible(false)
                            showToast(state.message)
                        }
                    }
                }
            }
        }
    }

    private fun launchMain() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

