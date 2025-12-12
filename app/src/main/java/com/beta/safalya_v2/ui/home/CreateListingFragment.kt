package com.beta.safalya_v2.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.beta.safalya_v2.databinding.FragmentCreateListingBinding
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*

class CreateListingFragment : Fragment() {

    private var _binding: FragmentCreateListingBinding? = null
    private val binding get() = _binding!!
    private val vm: ListingsViewModel by viewModels()
    private var selectedDeliveryDate: Long? = null

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

        // Set up the date picker to show when the date input is clicked
        binding.dateInput.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Delivery Date")
                // Correctly pre-select today's date
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

            datePicker.show(parentFragmentManager, "DATE_PICKER")

            datePicker.addOnPositiveButtonClickListener { selection ->
                // The 'selection' parameter here is already the Long value you need.
                selectedDeliveryDate = selection
                // Format and display the selected date in the input field
                binding.dateInput.setText(
                    SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(selection))
                )
            }
        }

        binding.saveButton.setOnClickListener {
            val crop = binding.cropInput.text.toString().trim()
            val qty = binding.quantityInput.text.toString().trim()
            val price = binding.priceInput.text.toString().trim()
            val desc = binding.descriptionInput.text.toString().trim()

            // Check if a date has been selected
            if (crop.isEmpty() || qty.isEmpty() || price.isEmpty() || desc.isEmpty() || selectedDeliveryDate == null) {
                Toast.makeText(requireContext(), "Please fill all fields and select a date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            vm.createListing(
                crop,
                qty,
                price,
                selectedDeliveryDate!!, // Use the stored date
                desc
            ) {
                Toast.makeText(requireContext(), "Listing created!", Toast.LENGTH_SHORT).show()
                // Safely go back to the previous screen
                activity?.onBackPressedDispatcher?.onBackPressed()
            }
        }

        binding.cancelButton.setOnClickListener {
            // Safely go back to the previous screen
            activity?.onBackPressedDispatcher?.onBackPressed()
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
