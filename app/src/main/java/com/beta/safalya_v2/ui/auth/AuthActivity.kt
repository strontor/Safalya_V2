package com.beta.safalya_v2.ui.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.beta.safalya_v2.R
import com.beta.safalya_v2.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup NavHost for auth flow
        val navHost =
            supportFragmentManager.findFragmentById(R.id.auth_nav_host) as NavHostFragment

        val navController = navHost.navController
        binding.authToolbar.setupWithNavController(navController)
    }
}
