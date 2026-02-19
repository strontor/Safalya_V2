package com.beta.safalya_v2.util

import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

fun Long.toFormattedDate(pattern: String = "dd MMM yyyy"): String {
    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
    return formatter.format(Date(this))
}

fun Number.toRupeeFormat(): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    val value = this.toDouble()
    formatter.minimumFractionDigits = 0
    formatter.maximumFractionDigits = if (value % 1.0 == 0.0) 0 else 2
    return formatter.format(this)
}

