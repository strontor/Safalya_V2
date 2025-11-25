package com.beta.safalya_v2.ui.contracts

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.beta.safalya_v2.data.model.Contract
import com.beta.safalya_v2.databinding.FragmentContractsBinding
import com.beta.safalya_v2.main.MainSharedViewModel
import com.beta.safalya_v2.ui.adapters.ContractAction
import com.beta.safalya_v2.ui.adapters.ContractsAdapter
import com.beta.safalya_v2.util.UiState
import com.beta.safalya_v2.util.setVisible
import com.beta.safalya_v2.util.showToast
import kotlinx.coroutines.launch

class ContractsFragment : Fragment() {

    private var _binding: FragmentContractsBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: MainSharedViewModel by activityViewModels()
    private val viewModel: ContractsViewModel by viewModels()
    private var isFarmer: Boolean = false
    private lateinit var adapter: ContractsAdapter
    private var currentUserId: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContractsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycler()
        observeUser()
        observeContracts()
        observeActions()
    }

    private fun setupRecycler() {
        adapter = ContractsAdapter(isFarmer, ::handleAction)
        binding.contractsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.contractsRecycler.adapter = adapter
    }

    private fun observeUser() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.userState.collect { state ->
                    if (state is UiState.Success) {
                        val user = state.data
                        currentUserId = user.id
                        val newIsFarmer = user.role == "farmer"
                        if (newIsFarmer != isFarmer) {
                            isFarmer = newIsFarmer
                            adapter = ContractsAdapter(isFarmer, ::handleAction)
                            binding.contractsRecycler.adapter = adapter
                        }
                        viewModel.loadContracts(user.id, isFarmer)
                    }
                }
            }
        }
    }

    private fun observeContracts() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.contractsState.collect { state ->
                    when (state) {
                        is UiState.Idle -> binding.progressBar.setVisible(false)
                        is UiState.Loading -> binding.progressBar.setVisible(true)
                        is UiState.Success -> {
                            binding.progressBar.setVisible(false)
                            renderContracts(state.data)
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

    private fun observeActions() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.actionState.collect { state ->
                    when (state) {
                        is UiState.Loading -> binding.actionProgress.setVisible(true)
                        is UiState.Success -> {
                            binding.actionProgress.setVisible(false)
                            showToast("Updated")
                            viewModel.resetActionState()
                            if (currentUserId.isNotEmpty()) {
                                viewModel.loadContracts(currentUserId, isFarmer)
                            }
                        }
                        is UiState.Error -> {
                            binding.actionProgress.setVisible(false)
                            showToast(state.message)
                        }
                        else -> binding.actionProgress.setVisible(false)
                    }
                }
            }
        }
    }

    private fun renderContracts(contracts: List<Contract>) {
        binding.emptyState.setVisible(contracts.isEmpty())
        binding.contractsRecycler.setVisible(contracts.isNotEmpty())
        adapter.submitList(contracts)
    }

    private fun handleAction(contract: Contract, action: ContractAction) {
        when (action) {
            ContractAction.ACCEPT -> viewModel.acceptContract(contract)
            ContractAction.REJECT -> viewModel.rejectContract(contract)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

