package com.simple.task.extensions

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Date.toLong(): Long? {
    return this?.time
}

fun Date.toStringDateFormat() : String {
    val formatDate = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    return formatDate.format(this)
}