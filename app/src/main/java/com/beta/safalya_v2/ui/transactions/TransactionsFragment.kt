package com.beta.safalya_v2.ui.transactions
import androidx.navigation.fragment.findNavController
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.beta.safalya_v2.databinding.FragmentTransactionsBinding
import com.beta.safalya_v2.ui.home.ListingsViewModel
import com.beta.safalya_v2.ui.adapters.ListingsAdapter
import com.beta.safalya_v2.R



class TransactionsFragment : Fragment() {

    private lateinit var binding: FragmentTransactionsBinding
    private val listingsViewModel: ListingsViewModel by viewModels()
    private lateinit var adapter: ListingsAdapter

    private var userRole: String = "BUYER" // inject later from shared VM

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setupRecycler()
        setupHeader()
        setupActions()
        loadData()
    }

    private fun setupRecycler() {
        adapter = ListingsAdapter { item ->
            val bundle = Bundle().apply {
                putString("itemId", item.id)
            }
            findNavController().navigate(
                R.id.itemDetailsFragment,
                bundle
            )
        }

        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun setupHeader() {
        if (userRole == "BUYER") {
            binding.tvTitle.text = "My Orders"
            binding.cardCreateOrder.visibility = View.VISIBLE
        } else {
            binding.tvTitle.text = "Browse Orders"
            binding.cardCreateOrder.visibility = View.GONE
        }
    }

    private fun setupActions() {
        binding.cardCreateOrder.setOnClickListener {
            findNavController().navigate(
                R.id.createItemFragment // later
            )
        }
    }

    private fun loadData() {
        if (userRole == "BUYER") {
            // buyer sees their BUY orders
            listingsViewModel.loadBuyOrders()
            listingsViewModel.activeListings.observe(viewLifecycleOwner) {
                adapter.submitList(it)
            }
        } else {
            // farmer sees BUY orders placed by buyers
            listingsViewModel.loadMySellListings()
            listingsViewModel.myListings.observe(viewLifecycleOwner) {
                adapter.submitList(it)
            }
        }
    }
}
