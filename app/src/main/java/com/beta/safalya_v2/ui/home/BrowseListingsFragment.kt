package com.beta.safalya_v2.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.beta.safalya_v2.databinding.FragmentBrowseListingsBinding
import com.beta.safalya_v2.ui.adapters.ListingsAdapter

class BrowseListingsFragment : Fragment() {

    private lateinit var binding: FragmentBrowseListingsBinding
    private val vm: ListingsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentBrowseListingsBinding.inflate(inflater, container, false)

        vm.loadActiveListings()

        vm.activeListings.observe(viewLifecycleOwner) { listings ->
            binding.recyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = ListingsAdapter(listings) { listing ->
                    val frag = ListingDetailsFragment.newInstance(
                        listing.id,
                        listing.farmerId,
                        listing.cropType,
                        listing.quantity,
                        listing.price,
                        listing.description
                    )
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.mainContainer, frag)
                        .addToBackStack(null)
                        .commit()
                }
            }
        }

        return binding.root
    }
}
