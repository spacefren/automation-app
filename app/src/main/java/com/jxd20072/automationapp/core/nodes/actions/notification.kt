package com.jxd20072.automationapp.core.nodes.actions

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.core.data.model.ConfigurationValue
import com.jxd20072.automationapp.core.data.model.NodeType
import com.jxd20072.automationapp.core.executor.ExecutionContext
import com.jxd20072.automationapp.core.nodes.NodeData
import com.jxd20072.automationapp.core.nodes.NodeRegistryEntry
import com.jxd20072.automationapp.core.nodes.props.NodeInputDefinition
import com.jxd20072.automationapp.core.nodes.props.ValidationResult
import com.jxd20072.automationapp.core.system.createNotificationChannel

class NotificationAction(id: String) : ActionNode(id) {
	private val CHANNEL_ID = "automationApp_notificationNode"

	override fun execute(context: ExecutionContext) {
		val title = getInput<ConfigurationValue.TEXT>("Title").value
		val body = getInput<ConfigurationValue.TEXT>("Body").value

		createNotificationChannel(
			context = context.appContext,
			name = "NotificationNode",
			description = "Notifications sent by the \"notification\" node",
			id = CHANNEL_ID
		)

		val builder = NotificationCompat.Builder(context.appContext, CHANNEL_ID)
			.setSmallIcon(R.drawable.ic_launcher_foreground)
			.setContentTitle(title)
			.setContentText(body)
			.setPriority(NotificationCompat.PRIORITY_DEFAULT)


		if (ActivityCompat.checkSelfPermission(
				context.appContext, Manifest.permission.POST_NOTIFICATIONS
			) == PackageManager.PERMISSION_GRANTED
		) {
			NotificationManagerCompat.from(context.appContext).notify(
				System.currentTimeMillis().toInt(),
				builder.build()
			)
		}
	}
}

val NotificationNode = NodeRegistryEntry<ActionNode>(
	data = NodeData(
		nodeType = NodeType.ACTION,
		displayName = "Notification",
		name = "notification",
		description = "send a system notification",
		category = "system",
		editorActionDescription = { input ->
			listOf(
				"Send notification: ",
				"*${input["Title"]?.value}"
			)
		},
		icon = R.drawable.round_info_24,
		inputDefinitions = mapOf(
			"Title" to NodeInputDefinition(
				description = "The notification's title",
				defaultValue = ConfigurationValue.TEXT("TITLE"),
				validate = { ValidationResult.Success },
			),
			"Body" to NodeInputDefinition(
				description = "The notification's text body",
				defaultValue = ConfigurationValue.TEXT("BODY"),
				validate = { ValidationResult.Success },
			)
		),
		outputDefinitions = emptyMap(),
		permissions = listOf("android.permission.POST_NOTIFICATIONS")
	),
	factory = { NotificationAction(it) }
)