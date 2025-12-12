package com.beta.safalya_v2

import androidx.navigation.NavGraph
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.beta.safalya_v2.ui.auth.AuthActivity
import com.beta.safalya_v2.databinding.ActivityMainBinding
import com.beta.safalya_v2.main.MainSharedViewModel
import com.beta.safalya_v2.util.UiState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import android.view.ViewGroup



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
        //correction for padding
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




        // Load user from Firestore
        sharedViewModel.loadCurrentUser()

        // Wait for userState to decide start destination
        observeUserState()
    }

    private fun observeUserState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                sharedViewModel.userState.collect { state ->

                    if (state is UiState.Success) {

                        val role = state.data.role

                        // Get navHost + navController
                        val navHostFragment =
                            supportFragmentManager.findFragmentById(R.id.main_nav_host) as NavHostFragment
                        val navController = navHostFragment.navController

                        // Dynamically set home based on role
                        val navInflater = navController.navInflater
                        val graph = navInflater.inflate(R.navigation.main_nav_graph) as NavGraph



                        graph.setStartDestination(R.id.homeFragment)
                        navController.graph = graph


                        // Now set up bottom nav AFTER graph is set
                        binding.bottomNav.setupWithNavController(navController)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (auth.currentUser == null) {
            launchAuthFlow()
        }
    }

    private fun launchAuthFlow() {
        startActivity(Intent(this, AuthActivity::class.java))
        finish()
    }
}