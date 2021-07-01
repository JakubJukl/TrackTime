package com.example.tracktime

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList

const val DATABASENAME = "Android SQLite"
const val TABLENAME = "Records"
const val COL_START = "start"
const val COL_LEAVE = "leave"
const val COL_ID = "id"

class DbHandler(var context: Context) : SQLiteOpenHelper(context, DATABASENAME, null,
    1){

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE " + TABLENAME + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_START + " VARCHAR(255)," + COL_LEAVE + " VARCHAR(255))"
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //onCreate(db);
    }

    fun insertData(record: Record) {
        val database = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_START, record.startTime.format(formatter))
        contentValues.put(COL_LEAVE, record.leaveTime?.format(formatter))
        val result = database.insert(TABLENAME, null, contentValues)
        if (result == (0).toLong()) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        }
        else {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
        }
        database.close()
    }
    fun readData(): MutableList<Record> {
        val list: MutableList<Record> = ArrayList()
        val db = this.readableDatabase
        val query = "Select * from $TABLENAME"
        val result = db.rawQuery(query, null)
        if (result.moveToFirst()) {
            do {
                val record = Record(LocalDateTime.parse(result.getString(result.getColumnIndex(COL_START)),formatter))
                record.id = result.getString(result.getColumnIndex(COL_ID)).toInt()
                val leaveTime : String? = result.getString(result.getColumnIndex(COL_LEAVE))
                if (!leaveTime.isNullOrBlank()){
                    record.leaveTime = LocalDateTime.parse(leaveTime, formatter)
                }
                list.add(record)
            }
            while (result.moveToNext())
        }
        result.close()
        db.close()
        return list
    }

    fun editRecordLeaveTime(id: String, leaveTime: String){
        val database = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_LEAVE, leaveTime)
        val result = database.update(TABLENAME, contentValues, "$COL_ID = ?", arrayOf(id))
        if (result == 0) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        }
        else {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    }

}