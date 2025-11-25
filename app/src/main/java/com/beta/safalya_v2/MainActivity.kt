package com.beta.safalya_v2

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.beta.safalya_v2.auth.AuthActivity
import com.beta.safalya_v2.databinding.ActivityMainBinding
import com.beta.safalya_v2.main.MainSharedViewModel
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val sharedViewModel: MainSharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (auth.currentUser == null) {
            launchAuthFlow()
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupNavigation()
        sharedViewModel.loadCurrentUser()

        sharedViewModel.userState.observe(this) { state ->
            if (state is UiState.Success) {
                val user = state.data
                val role = user.role

                openHomeFragment(role)
                setupBottomNav(role)
            }
        }

    }

    override fun onResume() {
        super.onResume()
        if (auth.currentUser == null) {
            launchAuthFlow()
        }
    }

    private fun setupNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_nav_host) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNav.setupWithNavController(navController)
    }

    private fun launchAuthFlow() {
        startActivity(Intent(this, AuthActivity::class.java))
        finish()
    }

    private fun openHomeFragment(role: String) {
        val frag = if (role == "farmer") {
            FarmerHomeFragment()
        } else {
            BuyerHomeFragment()
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.mainContainer, frag)
            .commit()
    }

    private fun setupBottomNav(role: String) {
        binding.bottomNav.setOnItemSelectedListener { item ->

            when (item.itemId) {

                R.id.nav_home -> {
                    openHomeFragment(role)
                }

                R.id.nav_contracts -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.mainContainer, ContractsFragment())
                        .commit()
                }

                R.id.nav_profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.mainContainer, ProfileFragment())
                        .commit()
                }
            }
            true
        }
    }


}