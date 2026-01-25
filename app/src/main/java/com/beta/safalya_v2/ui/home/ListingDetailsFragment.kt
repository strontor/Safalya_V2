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
import com.beta.safalya_v2.databinding.FragmentListingDetailsBinding

class ListingDetailsFragment : Fragment() {

    private lateinit var binding: FragmentListingDetailsBinding

    // ================= REQUIRED NAV ARGS =================
    private lateinit var listingId: String
    private lateinit var listingType: String   // "BUY" or "SELL"
    // ====================================================

    private val vm: ListingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentListingDetailsBinding.inflate(inflater, container, false)

        // ---------- Extract required arguments ----------
        listingId = requireArguments().getString("listingId")!!
        listingType = requireArguments().getString("listingType")!!
        // -----------------------------------------------

        setupStaticUI()
        setupActionsByListingType()

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        return binding.root
    }

    /**
     * ================= STATIC UI =================
     * Display-only data passed from previous screen.
     * This is configurable depending on how much
     * data you want to fetch vs pass via bundle.
     */
    private fun setupStaticUI() {
        binding.cropValue.text = arguments?.getString("crop")
        binding.quantityValue.text = arguments?.getString("qty")
        binding.priceValue.text = arguments?.getString("price")
        binding.dateValue.text = arguments?.getString("date")
        binding.descriptionValue.text = arguments?.getString("desc")
    }

    /**
     * ================= CORE ACTION GATING =================
     * SINGLE source of truth:
     * listingType decides what actions are visible.
     */
    private fun setupActionsByListingType() {
        when (listingType) {

            // ---------------- SELL LISTING ----------------
            // Farmer created listing
            // Buyer can request contract
            "SELL" -> showSellListingActions()

            // ---------------- BUY ORDER ----------------
            // Buyer created order
            // Farmer can show interest
            "BUY" -> showBuyOrderActions()

            // ---------------- SAFETY ----------------
            else -> hideAllActions()
        }
    }

    /**
     * SELL listing actions
     * - Buyer: request contract
     * - Farmer: view interested buyers
     *
     * NOTE:
     * Ownership is assumed to be decided BEFORE navigation.
     */
    private fun showSellListingActions() {

        // ---- Configurable: which button to show by role ----
        // For now, we allow both and let backend enforce rules
        // ----------------------------------------------------

        binding.btnRequestContract.visibility = View.VISIBLE
        binding.btnViewInterestedBuyers.visibility = View.VISIBLE
        binding.tvStatus.visibility = View.GONE

        binding.btnRequestContract.setOnClickListener {
            vm.requestContract(listingId, farmerId = "") {
                Toast.makeText(requireContext(), "Contract requested", Toast.LENGTH_SHORT).show()
                binding.btnRequestContract.isEnabled = false
                binding.btnRequestContract.text = "Requested"
            }
        }

        binding.btnViewInterestedBuyers.setOnClickListener {
            findNavController().navigate(
                R.id.interestedBuyersFragment,
                Bundle().apply {
                    putString("listingId", listingId)
                }
            )
        }
    }

    /**
     * BUY order actions
     * - Buyer: view interested farmers
     * - Farmer: show interest
     */
    private fun showBuyOrderActions() {

        binding.btnRequestContract.visibility = View.VISIBLE
        binding.btnViewInterestedBuyers.visibility = View.GONE

        // -------- Configurable button text --------
        binding.btnRequestContract.text = "Show Interest"
        // -----------------------------------------

        binding.btnRequestContract.setOnClickListener {
            Toast.makeText(requireContext(), "Interest sent", Toast.LENGTH_SHORT).show()
            binding.btnRequestContract.isEnabled = false
        }

        // -------- Future extension --------
        // Navigate to InterestedFarmersFragment
        // ----------------------------------
        binding.tvStatus.visibility = View.VISIBLE
        binding.tvStatus.text = "Interested farmers"
    }

    /**
     * Safety fallback
     */
    private fun hideAllActions() {
        binding.btnRequestContract.visibility = View.GONE
        binding.btnViewInterestedBuyers.visibility = View.GONE
        binding.tvStatus.visibility = View.GONE
    }
}
