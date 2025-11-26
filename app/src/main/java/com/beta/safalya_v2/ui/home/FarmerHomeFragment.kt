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
        observeListings()

        vm.loadMyListings()

        binding.btnCreateListing.setOnClickListener {
            findNavController().navigate(R.id.createListingFragment)
        }
    }

    private fun setupRecycler() {
        adapter = ListingsAdapter { listing ->
            val bundle = Bundle().apply {
                putString("listingId", listing.id)
            }
            findNavController().navigate(R.id.listingDetailsFragment, bundle)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun observeListings() {
        vm.myListings.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
