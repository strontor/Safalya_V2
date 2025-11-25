package com.beta.safalya_v2.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.beta.safalya_v2.databinding.FragmentCreateListingBinding

class CreateListingFragment : Fragment() {

    private lateinit var binding: FragmentCreateListingBinding
    private val vm: ListingsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCreateListingBinding.inflate(inflater, container, false)

        binding.btnCreateListing.setOnClickListener {

            val crop = binding.etCropType.text.toString()
            val qty = binding.etQuantity.text.toString().toIntOrNull() ?: 0
            val price = binding.etPrice.text.toString().toIntOrNull() ?: 0
            val date = binding.etDeliveryDate.text.toString()
            val desc = binding.etDescription.text.toString()

            if (crop.isEmpty() || qty <= 0 || price <= 0 || date.isEmpty()) {
                Toast.makeText(requireContext(), "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            vm.createListing(
                cropType,
                quantity,
                price,
                deliveryDate,
                description
            ) {
                Toast.makeText(requireContext(), "Listing created!", Toast.LENGTH_SHORT).show()
                requireActivity().onBackPressed()
            }

        }

        vm.error.observe(viewLifecycleOwner) {
            if (it != null) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                vm.error.value = null
            }
        }

        return binding.root
    }
}
