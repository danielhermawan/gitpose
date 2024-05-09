package com.coco.gitcompose.core.util

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn
import javax.inject.Inject

class DateUtil @Inject constructor() {
    fun getTimeBefore(value: Int, unit: DateTimeUnit.DateBased): String {
        return Clock.System.todayIn(TimeZone.UTC).minus(7, DateTimeUnit.DAY).toString()
    }
}