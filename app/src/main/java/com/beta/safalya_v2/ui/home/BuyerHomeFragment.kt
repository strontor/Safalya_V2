package com.beta.safalya_v2.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.beta.safalya_v2.databinding.FragmentBuyerHomeBinding
import com.beta.safalya_v2.ui.adapters.ListingsAdapter

class BuyerHomeFragment : Fragment() {

    private lateinit var binding: FragmentBuyerHomeBinding
    private val vm: ListingsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentBuyerHomeBinding.inflate(inflater, container, false)

        vm.loadActiveListings()

        vm.activeListings.observe(viewLifecycleOwner) { list ->
            binding.recyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = ListingsAdapter(list) { listing ->
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
