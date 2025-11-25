package com.beta.safalya_v2.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.beta.safalya_v2.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}

