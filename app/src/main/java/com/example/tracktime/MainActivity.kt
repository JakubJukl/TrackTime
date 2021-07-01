package com.example.tracktime

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

abstract class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val ERR_LEAVE = "You must first leave from work."
        val ERR_START = "You must first come to work."

        val context = this
        val db = DbHandler(context)
        val list: MutableList<Record> = db.readData()

        val start: View = findViewById(R.id.addFAB)
        val end: View = findViewById(R.id.deleteFAB)
        val months: TextView = findViewById(R.id.months)

        var monthInt = LocalDateTime.now().monthValue
        months.text = monthInt.toString()

        // Set the LayoutManager that this RecyclerView will use.
        recycler_view_items.layoutManager = LinearLayoutManager(this)

        updateUI(list, monthInt)

        //define listener
        months.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                monthInt = Integer.parseInt(months.text.toString())
                updateUI(list, monthInt)
                return@OnKeyListener true
            }
            false
        })

        start.setOnClickListener {
            if (list.isNotEmpty() && list.last().leaveTime == null) {
                Toast.makeText(this, ERR_LEAVE, Toast.LENGTH_LONG).show()
            } else {
                val record = Record(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                db.insertData(record)
                record.id = list.last().id?.plus(1)
                list.add(record)
                updateUI(list, monthInt)
            }
        }

        end.setOnClickListener {
            if (list.isEmpty() || list.last().leaveTime != null) {
                Toast.makeText(this, ERR_START, Toast.LENGTH_LONG).show()
            } else {
                val time = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
                val leaveTime = time.format(DbHandler.formatter)
                list.last().leaveTime = time
                db.editRecordLeaveTime(list.last().id.toString(), leaveTime)
                updateUI(list, monthInt)
            }
        }
    }

    private fun updateUI(list: MutableList<Record>, monthInt: Int){
        loadRecycler(list, monthInt)
        loadTotal(list, monthInt)
    }

    private fun loadRecycler(list: MutableList<Record>, monthInt: Int){
        // Adapter class is initialized and list is passed in the param.
        val itemAdapter = ItemAdapter(this, list.filter { x -> x.startTime.monthValue == monthInt }.toMutableList())

        // adapter instance is set to the recyclerview to inflate the items.
        recycler_view_items.adapter = itemAdapter
    }

    private fun loadTotal(list: MutableList<Record>, monthInt: Int){
        val TOTAL_TEXT = "Total: "

        val s = list.filter { x -> x.startTime.monthValue == monthInt && x.leaveTime != null }.sumOf { it.duration() }
        val duration = String.format("%03d:%02d:%02d", s / 3600, (s % 3600) / 60, (s % 60))
        total.text = TOTAL_TEXT + duration
    }

}