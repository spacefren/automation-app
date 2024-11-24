package com.jxd20072.automationapp.ui.utils

import android.text.format.DateUtils
import java.time.LocalDateTime
import java.time.ZoneId

fun dateTimeToMillis(dateTime: LocalDateTime): Long {
	return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
}

fun getRelativeTime(startDateTime: LocalDateTime): String {
	val startTimeMillis = dateTimeToMillis(startDateTime)
	val currentTimeMillis = System.currentTimeMillis()

	if (currentTimeMillis - startTimeMillis < DateUtils.SECOND_IN_MILLIS) return "Just now"

	return DateUtils.getRelativeTimeSpanString(
		startTimeMillis,
		currentTimeMillis,
		DateUtils.SECOND_IN_MILLIS
	).toString()
}