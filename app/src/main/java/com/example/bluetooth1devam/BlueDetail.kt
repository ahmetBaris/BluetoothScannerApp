package com.example.bluetooth1devam

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.bluetooth1devam.databinding.ActivityBlueDetailBinding

class BlueDetail : AppCompatActivity() {

    private lateinit var binding: ActivityBlueDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_blue_detail)
        binding = ActivityBlueDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val name = intent?.getStringExtra("name")
        //Log.e("gelenName", name.toString())
        binding.bluetoothName.text = name

        val address = intent.getStringExtra("address")
        binding.bluetoothAddress.text = address
    }
}