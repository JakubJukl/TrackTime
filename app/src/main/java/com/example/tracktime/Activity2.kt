package com.example.tracktime

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_2.*


class Activity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_2)

        val START = "Shift started: "
        val END = "Shift ended: "
        val DURATION = "Shift lasted: "
        detailStart.text = START + intent.getStringExtra(INTENT_START)
        detailEnd.text = END + intent.getStringExtra(INTENT_END)
        detailDuration.text = DURATION + intent.getStringExtra(INTENT_DURATION)
    }

    companion object {
        const val INTENT_START = "Start"
        const val INTENT_END = "End"
        const val INTENT_DURATION = "Duration"
    }

}