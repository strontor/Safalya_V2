package com.beta.safalya_v2.ui.home

import com.beta.safalya_v2.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.beta.safalya_v2.databinding.FragmentBrowseListingsBinding
import com.beta.safalya_v2.ui.adapters.ListingsAdapter

class BrowseListingsFragment : Fragment() {

    private var _binding: FragmentBrowseListingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ListingsViewModel by viewModels()

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

        setupRecycler()
        observeListings()

        viewModel.loadActiveListings()
    }

    private fun setupRecycler() {
        binding.listingsRecycler.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeListings() {
        viewModel.activeListings.observe(viewLifecycleOwner) { list ->

            binding.progressBar.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
            binding.emptyState.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE

            val adapter = ListingsAdapter { listing ->
                val bundle = Bundle()
                bundle.putString("listingId", listing.id)

                findNavController().navigate(
                    R.id.listingDetailsFragment,
                    bundle
                )
            }

            binding.listingsRecycler.adapter = adapter
            adapter.submitList(list)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
