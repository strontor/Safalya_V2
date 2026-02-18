package com.beta.safalya_v2.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.beta.safalya_v2.R
import com.beta.safalya_v2.databinding.FragmentBrowseListingsBinding
import com.beta.safalya_v2.main.MainSharedViewModel
import com.beta.safalya_v2.ui.adapters.ListingsAdapter
import com.beta.safalya_v2.util.UiState

class BrowseListingsFragment : Fragment() {

    private var _binding: FragmentBrowseListingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ListingsViewModel by viewModels()
    private val sharedViewModel: MainSharedViewModel by activityViewModels()
    private lateinit var adapter: ListingsAdapter

    private var userRole: String = "buyer"
    private var screenMode: String = "default"

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

        userRole = resolveRole()
        screenMode = arguments?.getString("screen_mode") ?: "default"

        setupHeader()
        setupRecycler()
        observeListings()
        loadData()
        setupCreateAction()
    }

    private fun resolveRole(): String {
        val roleFromArgs = arguments?.getString("role")?.lowercase()
        if (roleFromArgs == "farmer" || roleFromArgs == "buyer") return roleFromArgs

        val roleFromShared = (sharedViewModel.userState.value as? UiState.Success)
            ?.data
            ?.role
            ?.lowercase()

        return if (roleFromShared == "farmer") "farmer" else "buyer"
    }

    private fun setupHeader() {
        if (screenMode == "my_orders" && userRole == "buyer") {
            binding.tvTitle.text = "My Orders"
            binding.tvSubtitle.text = "Orders you created"
        } else if (userRole == "buyer") {
            binding.tvTitle.text = "Browse Listings"
            binding.tvSubtitle.text = "Available farmer listings"
        } else {
            binding.tvTitle.text = "Browse Orders"
            binding.tvSubtitle.text = "Available buyer orders"
        }
    }

    private fun setupCreateAction() {
        if (userRole == "farmer") {
            binding.cardCreate.visibility = View.GONE
            return
        }

        binding.cardCreate.visibility = View.VISIBLE
        binding.tvCreateAction.text = "Create Buy Order"

        binding.cardCreate.setOnClickListener {
            findNavController().navigate(
                R.id.createItemFragment,
                Bundle().apply { putString("itemType", "BUY") }
            )
        }
    }

    private fun setupRecycler() {
        adapter = ListingsAdapter { item ->
            val bundle = Bundle().apply {
                putString("itemId", item.id)
                putString("farmerId", item.farmerId)
                putString("itemType", item.itemType)
                putString("crop", item.cropType)
                putString("qty", item.quantity)
                putString("price", item.price)
                putString("date", item.deliveryDate)
                putString("desc", item.description)
            }

            findNavController().navigate(R.id.itemDetailsFragment, bundle)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun loadData() {
        binding.progressBar.visibility = View.VISIBLE

        if (screenMode == "my_orders" && userRole == "buyer") {
            viewModel.loadMyBuyOrders()
        } else if (userRole == "buyer") {
            viewModel.loadSellListings()
        } else {
            viewModel.loadBuyOrders()
        }
    }

    private fun observeListings() {
        val observer: (List<com.beta.safalya_v2.data.model.Item>) -> Unit = { list ->
            binding.progressBar.visibility = View.GONE
            binding.emptyState.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
            adapter.submitList(list)
        }
        if (screenMode == "my_orders" && userRole == "buyer") {
            viewModel.myListings.observe(viewLifecycleOwner, observer)
        } else {
            viewModel.activeListings.observe(viewLifecycleOwner, observer)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
