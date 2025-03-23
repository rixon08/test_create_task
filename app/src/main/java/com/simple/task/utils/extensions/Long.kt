package com.simple.task.utils.extensions

import java.util.Date

fun Long.toDate(): Date? {
    return this?.let { Date(this) }
}