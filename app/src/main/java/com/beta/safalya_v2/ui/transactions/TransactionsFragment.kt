package com.beta.safalya_v2.ui.transactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.beta.safalya_v2.databinding.FragmentTransactionsBinding
import com.beta.safalya_v2.main.MainSharedViewModel
import com.beta.safalya_v2.util.UiState


class TransactionsFragment : Fragment() {

    private lateinit var binding: FragmentTransactionsBinding
    private val vm: TransactionsViewModel by viewModels()
    private val mainVM: MainSharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTransactionsBinding.inflate(inflater, container, false)

        mainVM.userState.value?.let { state ->
            if (state is UiState.Success) {
                val user = state.data
                val isFarmer = user.role == "farmer"
                vm.loadTransactions(isFarmer)
            }
        }


        vm.transactions.observe(viewLifecycleOwner) { list ->
            binding.recyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = TransactionsAdapter(list)
            }
        }

        return binding.root
    }
}