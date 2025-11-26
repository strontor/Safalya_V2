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

    private var _binding: FragmentCreateListingBinding? = null
    private val binding get() = _binding!!
    private val vm: ListingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateListingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.saveButton.setOnClickListener {
            val crop = binding.cropInput.text.toString().trim()
            val qty = binding.quantityInput.text.toString().trim()
            val price = binding.priceInput.text.toString().trim()
            val date = binding.dateInput.text.toString().trim()
            val desc = binding.descriptionInput.text.toString().trim()

            if (crop.isEmpty() || qty.isEmpty() || price.isEmpty() || date.isEmpty()) {
                Toast.makeText(requireContext(), "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            vm.createListing(
                crop,
                qty,
                price,
                date,
                desc
            ) {
                Toast.makeText(requireContext(), "Listing created!", Toast.LENGTH_SHORT).show()
                requireActivity().onBackPressed()
            }
        }

        binding.cancelButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        vm.error.observe(viewLifecycleOwner) { err ->
            if (err != null) {
                Toast.makeText(requireContext(), err, Toast.LENGTH_LONG).show()
                vm.error.value = null
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
