package com.beta.safalya_v2.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.beta.safalya_v2.R
import com.beta.safalya_v2.databinding.FragmentBrowseListingsBinding
import com.beta.safalya_v2.ui.adapters.ListingsAdapter
import com.google.firebase.auth.FirebaseAuth


class BrowseListingsFragment : Fragment() {

    private var _binding: FragmentBrowseListingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ListingsViewModel by viewModels()
    private lateinit var adapter: ListingsAdapter

    // CONFIGURABLE: role passed via navigation
    private lateinit var userRole: String // "BUYER" or "FARMER"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBrowseListingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userRole = arguments?.getString("role") ?: "BUYER"

        setupHeader()
        setupRecycler()
        observeListings()
        loadData()
        setupCreateAction()

    }

    // ---------------- UI SETUP ----------------

    private fun setupHeader() {
        if (userRole == "BUYER") {
            binding.tvTitle.text = "Browse Listings"
            binding.tvSubtitle.text = "Available farmer listings"
        } else {
            binding.tvTitle.text = "Browse Orders"
            binding.tvSubtitle.text = "Available buyer orders"
        }
    }

    private fun setupCreateAction() {

        // TEMP role check — later replace with shared VM
        val isFarmer = arguments?.getString("role") == "farmer"

        if (isFarmer) {
            binding.tvCreateAction.text = "Create Sell Listing"
        } else {
            binding.tvCreateAction.text = "Create Buy Order"
        }

        binding.cardCreate.setOnClickListener {

            val bundle = Bundle().apply {
                putString(
                    "itemType",
                    if (isFarmer) "SELL" else "BUY"
                )
            }

            findNavController().navigate(
                R.id.createItemFragment,
                bundle
            )
        }
    }


    private fun setupRecycler() {
        adapter = ListingsAdapter { item ->
            val bundle = Bundle().apply {
                putString("itemId", item.id)
                putString("farmerId", item.farmerId)
                putString("itemType", item.itemType)

                // UI fields (optional but recommended)
                putString("crop", item.cropType)
                putString("qty", item.quantity)
                putString("price", item.price)
                putString("date", item.deliveryDate)
                putString("desc", item.description)
            }

            findNavController().navigate(
                R.id.browseListingsFragment,
                Bundle().apply {
                    putString("role", "BUYER") // or "FARMER"
                }
            )

        }

        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext())

        binding.recyclerView.adapter = adapter
    }

    // ---------------- DATA ----------------

    private fun loadData() {
        binding.progressBar.visibility = View.VISIBLE

        if (userRole == "BUYER") {
            viewModel.loadSellListings() // BUYER sees SELL listings
        } else {
            viewModel.loadBuyOrders()   // FARMER sees BUY orders
        }
    }

    private fun observeListings() {
        viewModel.activeListings.observe(viewLifecycleOwner) { list ->
            binding.progressBar.visibility = View.GONE

            binding.emptyState.visibility =
                if (list.isEmpty()) View.VISIBLE else View.GONE

            adapter.submitList(list)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}