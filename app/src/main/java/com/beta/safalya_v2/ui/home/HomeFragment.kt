package com.beta.safalya_v2.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.beta.safalya_v2.R
import com.beta.safalya_v2.data.model.Listing
import com.beta.safalya_v2.databinding.FragmentHomeBinding
import com.beta.safalya_v2.main.MainSharedViewModel
import com.beta.safalya_v2.ui.adapters.ListingsAdapter
import com.beta.safalya_v2.util.UiState
import com.beta.safalya_v2.util.setVisible
import com.beta.safalya_v2.util.showToast
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: MainSharedViewModel by activityViewModels()
    private val listingsViewModel: ListingsViewModel by activityViewModels()
    private lateinit var adapter: ListingsAdapter
    private var isFarmer: Boolean = false
    private var currentUserId: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycler()
        setupActions()
        observeUser()
        observeListings()
    }

    override fun onResume() {
        super.onResume()
        if (currentUserId.isNotEmpty()) {
            fetchListings()
        }
    }

    private fun setupRecycler() {
        adapter = ListingsAdapter { listing ->
            val args = bundleOf("listingId" to listing.id)
            findNavController().navigate(R.id.action_homeFragment_to_listingDetailsFragment, args)
        }
        binding.listingsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.listingsRecycler.adapter = adapter
    }

    private fun setupActions() {
        binding.homePrimaryAction.setOnClickListener {
            val destination = if (isFarmer) {
                R.id.action_homeFragment_to_createListingFragment
            } else {
                R.id.action_homeFragment_to_browseListingsFragment
            }
            findNavController().navigate(destination)
        }
    }

    private fun observeUser() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.userState.collect { state ->
                    if (state is UiState.Success) {
                        val user = state.data
                        isFarmer = user.role == "farmer"
                        currentUserId = user.id
                        binding.welcomeMessage.text = getString(R.string.home_welcome, user.name)
                        configurePrimaryButton()
                        fetchListings()
                    }
                }
            }
        }
    }

    private fun observeListings() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                listingsViewModel.listingsState.collect { state ->
                    when (state) {
                        is UiState.Idle -> binding.progressBar.setVisible(false)
                        is UiState.Loading -> binding.progressBar.setVisible(true)
                        is UiState.Success -> {
                            binding.progressBar.setVisible(false)
                            renderListings(state.data)
                        }
                        is UiState.Error -> {
                            binding.progressBar.setVisible(false)
                            showToast(state.message)
                        }
                    }
                }
            }
        }
    }

    private fun configurePrimaryButton() {
        binding.homePrimaryAction.text = if (isFarmer) {
            getString(R.string.action_create_listing)
        } else {
            getString(R.string.action_browse_listings)
        }
    }

    private fun fetchListings() {
        if (isFarmer) {
            listingsViewModel.loadFarmerListings(currentUserId)
        } else {
            listingsViewModel.loadActiveListings()
        }
    }

    private fun renderListings(listings: List<Listing>) {
        binding.emptyState.setVisible(listings.isEmpty())
        binding.listingsRecycler.setVisible(listings.isNotEmpty())
        adapter.submitList(listings)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

