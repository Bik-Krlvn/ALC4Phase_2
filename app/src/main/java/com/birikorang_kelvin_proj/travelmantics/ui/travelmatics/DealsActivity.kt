package com.birikorang_kelvin_proj.travelmantics.ui.travelmatics

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.birikorang_kelvin_proj.travelmantics.R
import com.birikorang_kelvin_proj.travelmantics.common.utils.FirebaseUtil

class DealsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseUtil.openFbReference("",this)
    }

    fun showMenu(){

    }
}
