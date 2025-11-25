package com.beta.safalya_v2.util

import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment

fun Fragment.showToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

fun View.setVisible(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}

inline fun <reified T> Fragment.launchActivity(block: Intent.() -> Unit = {}) {
    val intent = Intent(requireContext(), T::class.java)
    intent.block()
    startActivity(intent)
}

