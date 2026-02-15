package com.beta.safalya_v2.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.beta.safalya_v2.databinding.FragmentCreateItemBinding
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*
import androidx.navigation.fragment.findNavController

class CreateItemFragment : Fragment() {

    private var _binding: FragmentCreateItemBinding? = null
    private val binding get() = _binding!!
    private val vm: ListingsViewModel by viewModels()

    private var selectedDate: Long? = null
    private lateinit var itemType: String // BUY or SELL

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // 🔹 Listing type comes from navigation
        itemType = arguments?.getString("itemType") ?: "SELL"

        // 🔹 Toolbar back
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        // 🔹 Date picker
        binding.dateInput.setOnClickListener {
            val picker = MaterialDatePicker.Builder.datePicker().build()
            picker.show(parentFragmentManager, "DATE_PICKER")

            picker.addOnPositiveButtonClickListener {
                selectedDate = it
                binding.dateInput.setText(
                    SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                        .format(Date(it))
                )
            }
        }

        // 🔹 Save
        binding.saveButton.setOnClickListener {
            publish()
        }

        // 🔹 Cancel
        binding.cancelButton.setOnClickListener {
            findNavController().popBackStack()
        }

        // 🔹 Loader
        vm.loading.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }

        // 🔹 Errors
        vm.error.observe(viewLifecycleOwner) { msg ->
            msg?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun publish() {
        val crop = binding.cropInput.text.toString().trim()
        val qty = binding.quantityInput.text.toString().trim()
        val price = binding.priceInput.text.toString().trim()
        val desc = binding.descriptionInput.text.toString().trim()

        if (
            crop.isBlank() ||
            qty.isBlank() ||
            price.isBlank() ||
            desc.isBlank() ||
            selectedDate == null
        ) {
            Toast.makeText(requireContext(), "Fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        vm.createListing(
            cropType = crop,
            quantity = qty,
            price = price,
            deliveryDate = selectedDate!!,
            description = desc,
            itemType = itemType
        ) {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
