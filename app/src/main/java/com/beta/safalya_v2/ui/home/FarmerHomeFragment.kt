package com.beta.safalya_v2.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.beta.safalya_v2.databinding.FragmentFarmerHomeBinding
import com.beta.safalya_v2.ui.adapters.MyListingsAdapter

class FarmerHomeFragment : Fragment() {

    private lateinit var binding: FragmentFarmerHomeBinding
    private val vm: ListingsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentFarmerHomeBinding.inflate(inflater, container, false)

        vm.loadMyListings()

        vm.myListings.observe(viewLifecycleOwner) { list ->
            binding.recyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = MyListingsAdapter(list)
            }
        }

        binding.btnCreateListing.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.mainContainer, CreateListingFragment())
                .addToBackStack(null)
                .commit()
        }

        return binding.root
    }
}
