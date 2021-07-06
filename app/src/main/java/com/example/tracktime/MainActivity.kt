package com.example.tracktime

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

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val ERR_LEAVE = "You must first leave from work."
        val ERR_START = "You must first come to work."

        val context = this
        val db = DbHandler(context)

        val start: View = findViewById(R.id.addFAB)
        val end: View = findViewById(R.id.deleteFAB)
        val months: TextView = findViewById(R.id.months)

        var monthInt = LocalDateTime.now().monthValue
        months.text = monthInt.toString()

        var list: MutableList<Record> = assignList(db, formatMonth(monthInt))

        // Set the LayoutManager that this RecyclerView will use.
        recycler_view_items.layoutManager = LinearLayoutManager(this)

        updateUI(list)

        //define listener
        months.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                monthInt = Integer.parseInt(months.text.toString())
                list = assignList(db, formatMonth(monthInt))
                updateUI(list)
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
                updateUI(list)
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
                updateUI(list)
            }
        }
    }

    private fun updateUI(list: MutableList<Record>){
        loadRecycler(list)
        loadTotal(list)
    }

    private fun loadRecycler(list: MutableList<Record>){
        // Adapter class is initialized and list is passed in the param.
        val itemAdapter = ItemAdapter(this, list)

        // adapter instance is set to the recyclerview to inflate the items.
        recycler_view_items.adapter = itemAdapter
    }

    private fun loadTotal(list: MutableList<Record>){
        val TOTAL_TEXT = "Total: "

        val s = list.filter { x -> x.leaveTime != null }.sumOf { it.duration() }
        val duration = String.format("%03d:%02d:%02d", s / 3600, (s % 3600) / 60, (s % 60))
        total.text = TOTAL_TEXT + duration
    }

    private fun formatMonth(monthInt: Int) : String{
        return String.format("%02d",monthInt)
    }

    private fun assignList(db: DbHandler, month: String) : MutableList<Record>{
        return db.readData(month)
    }

}