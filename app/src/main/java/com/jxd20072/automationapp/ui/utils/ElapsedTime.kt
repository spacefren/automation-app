package com.jxd20072.automationapp.ui.utils

import android.text.format.DateUtils
import java.time.Instant

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

fun getRelativeTime(start: Instant, end: Instant): String {
	return formatElapsedTime(start.toEpochMilli(), end.toEpochMilli())
}