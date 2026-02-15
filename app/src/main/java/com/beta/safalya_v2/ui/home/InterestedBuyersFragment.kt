package com.beta.safalya_v2.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.beta.safalya_v2.databinding.FragmentInterestedBuyersBinding
import com.google.firebase.auth.FirebaseAuth
class InterestedBuyersFragment : Fragment() {

    private lateinit var binding: FragmentInterestedBuyersBinding
    private val vm: ListingsViewModel by viewModels()

    private lateinit var itemId: String
    private lateinit var buyerId: String
    private lateinit var adapter: InterestedBuyersAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentInterestedBuyersBinding.inflate(inflater, container, false)

        itemId = arguments?.getString("itemId") ?: ""

        setupRecycler()
        loadBuyers()

        return binding.root

    }

    private fun setupRecycler() {
        adapter = InterestedBuyersAdapter { requestId ->
            vm.acceptBuyer(
                itemId = itemId,
                requestId = requestId,
                farmerId = FirebaseAuth.getInstance().currentUser!!.uid,
                buyerId = buyerId
            ) {
                Toast.makeText(requireContext(), "Contract created", Toast.LENGTH_SHORT).show()
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }

        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun loadBuyers() {
        vm.loadInterestedBuyers(itemId) { buyers ->
            adapter.submitList(buyers)
        }
    }
}
