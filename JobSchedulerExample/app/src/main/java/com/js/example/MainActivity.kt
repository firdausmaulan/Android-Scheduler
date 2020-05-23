package com.js.example

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnStart.setOnClickListener {
            val serviceIntent = Intent(applicationContext, MyService::class.java)
            startService(serviceIntent)
            Toast.makeText(this, "See notification", Toast.LENGTH_SHORT).show()
            btnStart.visibility = View.GONE
        }
    }
}
