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
import com.beta.safalya_v2.databinding.FragmentFarmerHomeBinding
import com.beta.safalya_v2.ui.adapters.ListingsAdapter

class FarmerHomeFragment : Fragment() {

    private var _binding: FragmentFarmerHomeBinding? = null
    private val binding get() = _binding!!
    private val vm: ListingsViewModel by viewModels()

    private lateinit var adapter: ListingsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFarmerHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecycler()
        setupActions()
        observeOrders()
        // TEMP: reuse active listings until Orders VM exists
        vm.loadBuyOrders()

    }

    private fun setupRecycler() {
        adapter = ListingsAdapter { listing ->

            val bundle = Bundle().apply {
                putString("itemId", listing.id)

                // ownership
                putString("farmerId", listing.farmerId)

                // SELL listing (farmer-created)
                putString("itemType", listing.itemType)

                // UI fields
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
        binding.cardMyListings.setOnClickListener {
            findNavController().navigate(
                R.id.action_farmerHome_to_browseListings,
                Bundle().apply { putString("screen_mode", "my_listings") }
            )
        }

        binding.btnViewMoreOrders.setOnClickListener {
            findNavController().navigate(
                R.id.action_farmerHome_to_browseListings
            )
        }
    }

    private fun observeOrders() {
        vm.activeListings.observe(viewLifecycleOwner) { listings ->
            adapter.submitList(listings.take(3)) // preview
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
