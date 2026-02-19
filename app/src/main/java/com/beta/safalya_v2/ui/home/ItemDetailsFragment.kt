package com.beta.safalya_v2.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.beta.safalya_v2.R
import com.beta.safalya_v2.data.model.ContractState
import com.beta.safalya_v2.databinding.FragmentItemDetailsBinding
import com.beta.safalya_v2.util.toFormattedDate
import com.beta.safalya_v2.util.toRupeeFormat
import com.google.firebase.auth.FirebaseAuth

class ItemDetailsFragment : Fragment() {

    private lateinit var binding: FragmentItemDetailsBinding

    // ================= CORE IDENTIFIERS =================
    private lateinit var itemId: String
    private lateinit var farmerId: String
    private lateinit var buyerId: String
    private lateinit var itemType: String   // "BUY" or "SELL"

    private val vm: ListingsViewModel by viewModels()

    private val auth = FirebaseAuth.getInstance()
    private val currentUserId: String
        get() = auth.currentUser?.uid ?: ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentItemDetailsBinding.inflate(inflater, container, false)

        // ================= ARGUMENTS =================
        itemId = arguments?.getString("itemId") ?: ""
        farmerId = arguments?.getString("farmerId") ?: ""
        buyerId = arguments?.getString("buyerId") ?: ""
        itemType = arguments?.getString("itemType") ?: "SELL" // DEFAULT SAFE

        bindUiData()
        resolveActions()

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        return binding.root
    }
    //================= UI DATA (CONFIGURABLE) =================
    private fun bindUiData() {
        binding.cropValue.text = arguments?.getString("crop")
        binding.quantityValue.text = arguments?.getString("qty")
        val rawPrice = arguments?.getString("price").orEmpty()
        binding.priceValue.text = rawPrice.toLongOrNull()?.toRupeeFormat()
            ?: rawPrice.toDoubleOrNull()?.toRupeeFormat()
            ?: rawPrice
        val rawDate = arguments?.getString("date").orEmpty()
        binding.dateValue.text = rawDate.toLongOrNull()?.toFormattedDate() ?: rawDate
        binding.descriptionValue.text = arguments?.getString("desc")
    }

    //================= CORE DECISION ENGINE =================
    private fun resolveActions() {

        val isOwner = when (itemType) {
            "SELL" -> currentUserId == farmerId
            "BUY" -> currentUserId == buyerId
            else -> false
        }

        when (itemType) {
            // ----------SELL LISTING ----------
            "SELL" -> {
                if (isOwner) {
                    showViewInterestedBuyers()
                } else {
                    showRequestContract()
                }
            }

            // ---------- BUY ORDER ----------
            "BUY" -> {
                if (isOwner) {
                    showViewInterestedFarmers()
                } else {
                    showShowInterestButton()
                }
            }
        }
    }
    //================= ACTION STATES =================
    // Farmer owns SELL listing
    private fun showViewInterestedBuyers() {
        hideAllActions()

        binding.btnViewInterestedBuyers.visibility = View.VISIBLE

        binding.btnViewInterestedBuyers.setOnClickListener {
            findNavController().navigate(
                R.id.interestedBuyersFragment,
                Bundle().apply {
                    putString("itemId", itemId)
                }
            )
        }
    }

    // Buyer browsing SELL listing

    private fun showRequestContract() {
        hideAllActions()
        binding.btnRequestContract.visibility = View.VISIBLE
        binding.btnRequestContract.text = "Request Contract"

        vm.checkBuyerRequestStatus(itemId, currentUserId) { state ->
            updateRequestState(state)
        }

        binding.btnRequestContract.setOnClickListener {
            vm.requestContract(itemId, farmerId) {
                updateRequestState(ContractState.REQUESTED)
            }
        }
    }

    // Buyer owns BUY order
    private fun showViewInterestedFarmers() {
        hideAllActions()
        binding.tvStatus.visibility = View.VISIBLE
        binding.tvStatus.text = "Interested farmers"
        // CONFIGURABLE: navigate to InterestedFarmersFragment later
    }

    // Farmer browsing BUY order
    private fun showShowInterestButton() {
        hideAllActions()
        binding.btnRequestContract.visibility = View.VISIBLE
        binding.btnRequestContract.text = "Show Interest"
        binding.btnRequestContract.setOnClickListener {
            Toast.makeText(requireContext(), "Interest sent", Toast.LENGTH_SHORT).show()
        }
    }

    //================= STATE UI =================
    private fun updateRequestState(state: ContractState) {
        when (state) {
            ContractState.NONE -> {
                binding.btnRequestContract.isEnabled = true
                binding.btnRequestContract.text = "Request Contract"
                binding.tvStatus.visibility = View.GONE
            }

            ContractState.REQUESTED -> {
                binding.btnRequestContract.isEnabled = false
                binding.btnRequestContract.text = "Requested"
                binding.tvStatus.visibility = View.VISIBLE
            }

            ContractState.ACCEPTED -> {
                hideAllActions()
                binding.tvStatus.text = "Contract accepted 🎉"
                binding.tvStatus.visibility = View.VISIBLE
            }

            ContractState.REJECTED -> {
                hideAllActions()
                binding.tvStatus.text = "Not selected ❌"
                binding.tvStatus.visibility = View.VISIBLE
            }
        }
    }

    private fun hideAllActions() {
        binding.btnRequestContract.visibility = View.GONE
        binding.btnViewInterestedBuyers.visibility = View.GONE
        binding.tvStatus.visibility = View.GONE
    }
}
