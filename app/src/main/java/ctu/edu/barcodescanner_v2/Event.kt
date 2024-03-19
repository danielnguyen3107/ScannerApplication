package ctu.edu.barcodescanner_v2

import java.util.*

data class Event(
    val eventName: String,
    val host: String,
    val location: String,
    val numberOfMembers: Long,
    val dayPick: Date,
    val beginTime: Date,
    val endTime: Date
)
