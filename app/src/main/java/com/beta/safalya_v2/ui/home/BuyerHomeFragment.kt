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
        observeListings()

        vm.loadActiveListings()
    }

    private fun setupRecycler() {
        adapter = ListingsAdapter { listing ->

            val bundle = Bundle()
            bundle.putString("listingId", listing.id)

            findNavController().navigate(
                R.id.listingDetailsFragment,
                bundle
            )
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun observeListings() {
        vm.activeListings.observe(viewLifecycleOwner) { listings ->
            adapter.submitList(listings)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
