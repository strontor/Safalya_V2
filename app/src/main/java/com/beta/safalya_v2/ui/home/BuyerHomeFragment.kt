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
import com.beta.safalya_v2.databinding.FragmentBuyerHomeBinding
import com.beta.safalya_v2.ui.adapters.ListingsAdapter

class BuyerHomeFragment : Fragment() {

    private var _binding: FragmentBuyerHomeBinding? = null
    private val binding get() = _binding!!
    private val vm: ListingsViewModel by viewModels()

    private lateinit var adapter: ListingsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBuyerHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecycler()
        setupActions()
        observeActiveListings()

        vm.loadMySellListings()
    }

    private fun setupRecycler() {
        adapter = ListingsAdapter { listing ->

            val bundle = Bundle().apply {
                putString("itemId", listing.id)

                // ownership + role resolution
                putString("farmerId", listing.farmerId)

                // critical: BUY / SELL routing
                putString("itemType", listing.itemType)

                // UI data (read-only)
                putString("crop", listing.cropType)
                putString("qty", listing.quantity)
                putString("price", listing.price)
                putString("date", listing.deliveryDate)
                putString("desc", listing.description)
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


    private fun setupActions() {
        binding.cardMyOrders.setOnClickListener {
            findNavController().navigate(
                R.id.action_buyerHome_to_browseListings
            )

        }
    }

    private fun observeActiveListings() {
        vm.activeListings.observe(viewLifecycleOwner) { listings ->
            adapter.submitList(listings.take(3)) //  preview only
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
