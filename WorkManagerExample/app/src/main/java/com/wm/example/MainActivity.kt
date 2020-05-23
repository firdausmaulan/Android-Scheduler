package com.wm.example

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Constraints
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private var result : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        PermissionUtil.requestPermissions(this)

        btnStart.setOnClickListener {
            if (btnStart.text.toString() == getString(R.string.start)) {
                startWorker()
            } else {
                onBackPressed()
            }
        }
    }

    private fun startWorker() {
        // Start Worker
        // 15 minutes is the lowest time interval
        // https://stackoverflow.com/questions/52915591/workmanagers-periodicworkrequest-is-executing-once-and-not-repeating
        val constraints = Constraints.Builder()
            .setRequiresCharging(true)
            .build()
        val periodicWork = PeriodicWorkRequest
            .Builder(LocationWorker::class.java, 15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .addTag(LocationWorker.TAG)
            .build()
        WorkManager.getInstance().enqueue(periodicWork)

        Toast.makeText(
            this,
            "Location Worker Started : " + periodicWork.id,
            Toast.LENGTH_SHORT
        ).show()

        result += periodicWork.id.toString() + "\n"

        btnStart.text = result
    }

    override fun onBackPressed() {
        super.onBackPressed()
        WorkManager.getInstance().cancelAllWorkByTag(LocationWorker.TAG)
        finish()
    }
}
