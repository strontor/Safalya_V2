package com.beta.safalya_v2.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.beta.safalya_v2.databinding.ActivityAuthBinding
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import android.view.ViewGroup


class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Remove the top "Welcome Back" action bar
        supportActionBar?.hide()
        //padding fix
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val root = findViewById<ViewGroup>(android.R.id.content).getChildAt(0)

        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            view.setPadding(
                view.paddingLeft,
                bars.top,        // status bar safe padding
                view.paddingRight,
                bars.bottom      // navigation bar safe padding
            )

            WindowInsetsCompat.CONSUMED
        }



    }
}

