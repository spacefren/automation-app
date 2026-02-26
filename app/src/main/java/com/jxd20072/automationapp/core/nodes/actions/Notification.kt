package com.jxd20072.automationapp.core.nodes.actions

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.core.data.model.InputAcceptedValue
import com.jxd20072.automationapp.core.data.model.NodeInputSchema
import com.jxd20072.automationapp.core.data.model.NodeType
import com.jxd20072.automationapp.core.data.model.Value
import com.jxd20072.automationapp.core.executor.ExecutionContext
import com.jxd20072.automationapp.core.executor.ExecutionFlow
import com.jxd20072.automationapp.core.nodes.NodeCategory
import com.jxd20072.automationapp.core.nodes.NodeData
import com.jxd20072.automationapp.core.nodes.NodeDescriptionPiece
import com.jxd20072.automationapp.core.nodes.NodeRegistryEntry
import com.jxd20072.automationapp.core.system.createNotificationChannel
import com.jxd20072.automationapp.utils.getNodeInputPiece

class NotificationAction(id: String) : ActionNode(id) {
    private val CHANNEL_ID = "automationApp_notificationNode"

    override fun execute(context: ExecutionContext, flow: ExecutionFlow) {
        val title = getInput<Value.TEXT>("title", context).value
        val body = getInput<Value.TEXT>("body", context).value

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
        category = NodeCategory.SYSTEM,
        editorActionDescription = { input, variables ->
            listOf(
                NodeDescriptionPiece.Text("Send notification: "),
                getNodeInputPiece(variables, input["title"])
            )
        },
        icon = R.drawable.round_info_24,
        inputSchemas = mapOf(
            "title" to NodeInputSchema.create(
                displayName = "Title",
                description = "The notification's title",
                accepts = listOf(
                    InputAcceptedValue.TEXT(
                        defaultValue = Value.TEXT("TITLE")
                    )
                ),
            ),
            "body" to NodeInputSchema.create(
                displayName = "Body",
                description = "The notification's text body",
                accepts = listOf(
                    InputAcceptedValue.TEXT(
                        defaultValue = Value.TEXT("BODY")
                    )
                )
            )
        ),
        outputDefinitions = emptyMap(),
        permissions = listOf("android.permission.POST_NOTIFICATIONS"),
        canHaveContent = false
    ),
    factory = { NotificationAction(it) }
)