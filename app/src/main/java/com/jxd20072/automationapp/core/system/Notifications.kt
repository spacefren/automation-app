package com.jxd20072.automationapp.core.system

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE

fun createNotificationChannel(
	context: Context,
	name: String,
	description: String,
	id: String,
	importance: Int = NotificationManager.IMPORTANCE_DEFAULT,
) {
	val name = name
	val descriptionText = description
	val importance = NotificationManager.IMPORTANCE_DEFAULT
	val mChannel = NotificationChannel(id, name, importance)
	mChannel.description = descriptionText

	val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
	notificationManager.createNotificationChannel(mChannel)
}