package com.jxd20072.automationapp.ui.userSettings.categories

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.jxd20072.automationapp.core.data.model.ConfigurationEntry
import com.jxd20072.automationapp.ui.shared.LocalSnackbarState
import com.jxd20072.automationapp.ui.shared.SnackbarData
import com.jxd20072.automationapp.ui.shared.SnackbarType
import com.jxd20072.automationapp.ui.userSettings.UserSettingsUiState
import com.jxd20072.automationapp.ui.userSettings.settingsItems.UserSettingsItem

@SuppressLint("UseKtx")
@Composable
fun GeneralSettingsCategory(
	userSettingsUiState: UserSettingsUiState.Success,
	onAutoSaveChanged: (newVal: ConfigurationEntry.BOOL) -> Unit,
	onAutoOpenKeyboardChanged: (newVal: ConfigurationEntry.BOOL) -> Unit,
) {
	val context = LocalContext.current

	SettingsCategory(
		name = "General"
	) {
		UserSettingsItem(
			item = userSettingsUiState.settings.autoSave,
			displayName = "Auto-save automations",
			onValueChanged = onAutoSaveChanged,
		)
		UserSettingsItem(
			item = userSettingsUiState.settings.autoOpenKeyboard,
			displayName = "Automatically open keyboard",
			description = "When navigating to a screen with a text field, automatically show the keyboard",
			onValueChanged = onAutoOpenKeyboardChanged
		)
		UserSettingsItem(
			displayName = "Open automations folder",
			onActivate = {
				val appDataPath = "Android%2Fdata%2F${context.packageName}%2Ffiles"
				val uri =
					Uri.parse("content://com.android.externalstorage.documents/document/primary%3A$appDataPath%2Fautomations")

				val intent = Intent(Intent.ACTION_VIEW)
				intent.setDataAndType(uri, DocumentsContract.Document.MIME_TYPE_DIR)
				intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

				try {
					context.startActivity(intent)
				} catch (e: ActivityNotFoundException) {
					// Fallback
					LocalSnackbarState.showSnackbar(
						SnackbarData(
							text = "No compatible file manager found",
							type = SnackbarType.ERROR
						)
					)
				}
			}
		)
	}
}