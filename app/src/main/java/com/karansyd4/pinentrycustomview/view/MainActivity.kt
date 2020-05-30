package com.karansyd4.pinentrycustomview.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.karansyd4.pinentrycustomview.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btPinEntry.setOnClickListener {
            startActivity(Intent(this, PinEntryActivity::class.java))
        }
    }
}