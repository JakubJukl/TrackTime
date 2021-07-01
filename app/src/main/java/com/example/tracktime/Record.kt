package com.example.tracktime

import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class Record (var startTime: LocalDateTime) {
    var id : Int? = null
    var leaveTime: LocalDateTime? = null

    fun duration(): Long {
        return Duration.between(this.startTime, this.leaveTime).seconds
    }
}