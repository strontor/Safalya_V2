package com.beta.safalya_v2.ui.home

import com.google.firebase.auth.FirebaseAuth
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.beta.safalya_v2.databinding.FragmentListingDetailsBinding
import androidx.navigation.fragment.findNavController
import androidx.fragment.app.viewModels
import androidx.fragment.app.Fragment
import com.beta.safalya_v2.R

class ListingDetailsFragment : Fragment() {

    private lateinit var binding: FragmentListingDetailsBinding

    private lateinit var listingId: String
    private lateinit var farmerId: String
    private lateinit var buyerId: String



    private val vm: ListingsViewModel by viewModels()

    private val auth = FirebaseAuth.getInstance()
    private val currentUserId: String
        get() = auth.currentUser?.uid ?: ""
    val isOrder = arguments?.getString("type") == "order"


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListingDetailsBinding.inflate(inflater, container, false)

        listingId = arguments?.getString("listingId") ?: ""
        farmerId = arguments?.getString("farmerId") ?: ""
        buyerId = arguments?.getString("buyerId") ?: ""


        val isOwner = currentUserId == farmerId
        val isFarmer = isOwner        // farmer owns the listing
        val isBuyer = !isOwner        // buyer browsing listing
        val isOrder = arguments?.getString("type") == "order"



        gateActions(
            isBuyer = isBuyer,
            isOwner = isOwner
        )


        // -------- REQUEST CONTRACT BUTTON ----------
        binding.requestButton.setOnClickListener {
            vm.requestContract(listingId, farmerId) {
                Toast.makeText(requireContext(), "Request sent!", Toast.LENGTH_SHORT).show()
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

        // --------- SET UI DATA FROM ARGUMENTS ----------
        binding.cropValue.text = arguments?.getString("crop")
        binding.quantityValue.text = arguments?.getString("qty")
        binding.priceValue.text = arguments?.getString("price")
        binding.dateValue.text = arguments?.getString("date")
        binding.descriptionValue.text = arguments?.getString("desc")
        // This assumes farmer is viewing his own listing
        binding.btnViewInterestedBuyers.visibility = View.VISIBLE

        binding.btnViewInterestedBuyers.setOnClickListener {
            findNavController().navigate(
                R.id.interestedBuyersFragment,
                Bundle().apply {
                    putString("listingId", listingId)
                }
            )
        }

        // Toolbar back button
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        return binding.root
    }

    private fun gateActions(
        isBuyer: Boolean,
        isOwner: Boolean
    ) {

        vm.checkBuyerRequestStatus(
            listingId = listingId,
            buyerId = currentUserId
        ) { state ->
            setupActionButton(
                isBuyer = true,
                isOwnListing = false,
                state = state
            )
        }

        when {
            // BUYER owns ORDER
            isOrder && currentUserId == buyerId -> {
                showViewInterestedFarmers()
            }

            // FARMER browsing ORDER
            isOrder && currentUserId != buyerId -> {
                showShowInterestButton()
            }

            // BUYER browsing LISTING
            !isOrder && currentUserId != farmerId -> {
                showRequestContract()
            }

            // FARMER owns LISTING
            !isOrder && currentUserId == farmerId -> {
                showViewInterestedBuyers()
            }
        }

    }
    // ================= UI STATE HELPERS =================

    // Farmer owns LISTING → see interested buyers
    private fun showViewInterestedBuyers() {
        binding.btnViewInterestedBuyers.visibility = View.VISIBLE
        binding.btnRequestContract.visibility = View.GONE
        binding.tvStatus.visibility = View.GONE

        binding.btnViewInterestedBuyers.setOnClickListener {
            findNavController().navigate(
                R.id.interestedBuyersFragment,
                Bundle().apply {
                    putString("listingId", listingId)
                }
            )
        }
    }

    // Buyer browsing LISTING → can request contract
    private fun showRequestContract() {
        binding.btnViewInterestedBuyers.visibility = View.GONE
        binding.btnRequestContract.visibility = View.VISIBLE

        setupActionButton(
            isBuyer = true,
            isOwnListing = false,
            state = ContractState.NONE
        )

        binding.btnRequestContract.setOnClickListener {
            vm.requestContract(listingId, farmerId) {
                setupActionButton(
                    isBuyer = true,
                    isOwnListing = false,
                    state = ContractState.REQUESTED
                )
            }
        }
    }

    // Buyer owns ORDER → see interested farmers
    private fun showViewInterestedFarmers() {
        binding.btnRequestContract.visibility = View.GONE
        binding.btnViewInterestedBuyers.visibility = View.GONE
        binding.tvStatus.visibility = View.VISIBLE
        binding.tvStatus.text = "Interested farmers"
        // TODO: navigate to InterestedFarmersFragment when ready
    }

    // Farmer browsing ORDER → show interest
    private fun showShowInterestButton() {
        binding.btnViewInterestedBuyers.visibility = View.GONE
        binding.btnRequestContract.visibility = View.VISIBLE
        binding.btnRequestContract.text = "Show Interest"

        binding.btnRequestContract.setOnClickListener {
            Toast.makeText(requireContext(), "Interest sent", Toast.LENGTH_SHORT).show()
        }
    }

    // Safety fallback
    private fun hideAllActions() {
        binding.btnRequestContract.visibility = View.GONE
        binding.btnViewInterestedBuyers.visibility = View.GONE
        binding.tvStatus.visibility = View.GONE
    }



    private fun setupActionButton(
        isBuyer: Boolean,
        isOwnListing: Boolean,
        state: ContractState
    ) {
        when {
            !isBuyer || isOwnListing -> {
                binding.btnRequestContract.visibility = View.GONE
            }

            state == ContractState.NONE -> {
                binding.btnRequestContract.isEnabled = true
                binding.btnRequestContract.text = "Request Contract"
                binding.tvStatus.visibility = View.GONE
            }

            state == ContractState.REQUESTED -> {
                binding.btnRequestContract.isEnabled = false
                binding.btnRequestContract.text = "Requested"
                binding.tvStatus.visibility = View.VISIBLE
            }

            state == ContractState.ACCEPTED -> {
                binding.btnRequestContract.visibility = View.GONE
                binding.tvStatus.text = "Contract accepted 🎉"
                binding.tvStatus.visibility = View.VISIBLE
            }

            state == ContractState.REJECTED -> {
                binding.btnRequestContract.visibility = View.GONE
                binding.tvStatus.text = "Not selected ❌"
                binding.tvStatus.visibility = View.VISIBLE
            }

        }
    }

    companion object {
        fun newInstance(
            id: String,
            farmerId: String,
            crop: String,
            qty: String,
            price: String,
            date: String,
            desc: String
        ): ListingDetailsFragment {
            val f = ListingDetailsFragment()
            val b = Bundle()

            b.putString("listingId", id)
            b.putString("farmerId", farmerId)
            b.putString("crop", crop)
            b.putString("qty", qty)
            b.putString("price", price)
            b.putString("date", date)
            b.putString("desc", desc)

            f.arguments = b
            return f
        }
    }
}
enum class ContractState {
    NONE,        // no action yet
    REQUESTED,   // buyer requested
    ACCEPTED,    // farmer accepted
    REJECTED     // farmer rejected
}
