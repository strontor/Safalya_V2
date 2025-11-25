package com.beta.safalya_v2.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.beta.safalya_v2.databinding.FragmentListingDetailsBinding

class ListingDetailsFragment : Fragment() {

    private lateinit var binding: FragmentListingDetailsBinding
    private val vm: ListingsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentListingDetailsBinding.inflate(inflater, container, false)

        val listingId = arguments?.getString("listingId") ?: ""
        val farmerId = arguments?.getString("farmerId") ?: ""

        binding.btnRequestContract.setOnClickListener {
            vm.requestContract(listingId, farmerId) {
                Toast.makeText(requireContext(), "Request sent!", Toast.LENGTH_SHORT).show()
                requireActivity().onBackPressed()
            }
        }

        binding.tvCrop.text = arguments?.getString("crop")
        binding.tvQuantity.text = arguments?.getString("qty")
        binding.tvPrice.text = arguments?.getString("price")
        binding.tvDescription.text = arguments?.getString("desc")

        return binding.root
    }

    companion object {
        fun newInstance(id: String, farmerId: String, crop: String, qty: String, price: String, desc: String): ListingDetailsFragment {
            val f = ListingDetailsFragment()
            val b = Bundle()
            b.putString("listingId", id)
            b.putString("farmerId", farmerId)
            b.putString("crop", crop)
            b.putString("qty", qty)
            b.putString("price", price)
            b.putString("desc", desc)
            f.arguments = b
            return f
        }
    }
}
