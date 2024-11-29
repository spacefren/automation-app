package com.jxd20072.automationapp.ui.utils

import android.text.format.DateUtils
import java.time.LocalDateTime
import java.time.ZoneId

fun dateTimeToMillis(dateTime: LocalDateTime): Long {
	return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
}

fun formatElapsedTime(startTimeMillis: Long, endTimeMillis: Long): String {
	val elapsedTime = endTimeMillis - startTimeMillis
	return when {
		elapsedTime < 30_000L -> "Just now"
		elapsedTime < 60_000 -> "30 seconds ago"
		else -> DateUtils.getRelativeTimeSpanString(
			startTimeMillis,
			endTimeMillis,
			DateUtils.MINUTE_IN_MILLIS
		).toString()
	}
}

fun getRelativeTime(startDateTime: LocalDateTime, endTime: LocalDateTime?): String {
	val startTimeMillis = dateTimeToMillis(startDateTime)
	val endTimeMillis = if (endTime != null) dateTimeToMillis(endTime) else System.currentTimeMillis()

	return formatElapsedTime(startTimeMillis, endTimeMillis)
}