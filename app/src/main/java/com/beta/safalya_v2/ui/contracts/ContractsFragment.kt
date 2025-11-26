package com.beta.safalya_v2.ui.contracts

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import com.beta.safalya_v2.util.UiState
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.beta.safalya_v2.data.model.Contract
import com.beta.safalya_v2.databinding.FragmentContractsBinding
import com.beta.safalya_v2.main.MainSharedViewModel
import com.beta.safalya_v2.ui.adapters.ContractAction
import com.beta.safalya_v2.ui.adapters.ContractsAdapter
import com.beta.safalya_v2.util.setVisible
import com.beta.safalya_v2.util.showToast

class ContractsFragment : Fragment() {

    private var _binding: FragmentContractsBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: MainSharedViewModel by activityViewModels()
    private val viewModel: ContractsViewModel by viewModels()

    private var isFarmer: Boolean = false
    private lateinit var adapter: ContractsAdapter

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
        observeLiveData()
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

                        // Determine role
                        isFarmer = user.role == "farmer"

                        // Update adapter based on new role
                        adapter = ContractsAdapter(isFarmer, ::handleAction)
                        binding.contractsRecycler.adapter = adapter

                        // Load contracts for this role
                        viewModel.loadContracts(isFarmer)
                    }
                }
            }
        }
    }




    private fun observeLiveData() {

        viewModel.loading.observe(viewLifecycleOwner) {
            binding.progressBar.setVisible(it)
        }

        viewModel.contracts.observe(viewLifecycleOwner) { list ->
            renderContracts(list)
        }

        viewModel.error.observe(viewLifecycleOwner) { msg ->
            msg?.let { showToast(it) }
        }

        viewModel.actionLoading.observe(viewLifecycleOwner) { loading ->
            binding.actionProgress.setVisible(loading)
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
