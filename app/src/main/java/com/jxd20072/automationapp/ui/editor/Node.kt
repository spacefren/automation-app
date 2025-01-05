package com.jxd20072.automationapp.ui.editor

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jxd20072.automationapp.R

enum class TopLevelNodeType { ENTRYPOINT, TRIGGER, FUNCTION }
enum class ChildNodeType { ACTION }

@Composable
fun NodeHeader(
	displayName: List<String>,
	icon: Int,
	modifier: Modifier = Modifier
) {
	Row(
		horizontalArrangement = Arrangement.spacedBy(6.dp),
		verticalAlignment = Alignment.CenterVertically,
		modifier = modifier
			.fillMaxSize()
	) {
		Box(
			modifier = Modifier
				.width(10.dp)
		) {
			Icon(
				painter = painterResource(R.drawable.round_drag_indicator_24),
				tint = MaterialTheme.colorScheme.onSurface,
				contentDescription = "dragIndicator",
				modifier = Modifier
					.size(20.dp)
					.requiredWidth(IntrinsicSize.Max)
			)
		}
		Icon(
			painter = painterResource(icon),
			tint = MaterialTheme.colorScheme.onBackground,
			contentDescription = "dragIndicator",
			modifier = Modifier
				.fillMaxHeight()
				.size(26.dp)
				.aspectRatio(.7f)
		)
		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(6.dp)
		) {
			displayName.forEach { piece ->
				if (piece.startsWith("*")) {
					Box(
						modifier = Modifier
							.shadow(5.dp, RoundedCornerShape(5.dp))
							.clickable { }
							.background(MaterialTheme.colorScheme.surface)
							.padding(horizontal = 6.dp, vertical = 3.dp)
					) {
						Text(
							text = piece.removePrefix("*"),
							color = MaterialTheme.colorScheme.onBackground,
							fontStyle = FontStyle.Italic,
							fontSize = 16.sp,
						)
					}
				} else {
					Text(
						text = piece,
						color = MaterialTheme.colorScheme.onBackground,
						fontSize = 16.sp
					)
				}
			}
		}
	}
}

@Composable
fun ChildNode(
	type: ChildNodeType,
	name: String,
	displayName: List<String>,
	icon: Int,
	modifier: Modifier = Modifier,
) {
	Box(
		modifier = modifier
			.fillMaxWidth()
			.shadow(5.dp, RoundedCornerShape(4.dp))
			.background(MaterialTheme.colorScheme.tertiary)
			.padding(8.dp)
	) {
		NodeHeader(
			displayName = displayName,
			icon = icon,
		)
	}
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TopLevelNode(
	type: TopLevelNodeType,
	name: String,
	displayName: List<String>,
	icon: Int,
	modifier: Modifier = Modifier,
	content: List<@Composable ColumnScope.() -> Unit>
) {
	Box(
		modifier = modifier
			.fillMaxWidth()
			.shadow(5.dp, RoundedCornerShape(8.dp))
			.background(MaterialTheme.colorScheme.surface)
			.padding(10.dp)
	) {
		Column(
			verticalArrangement = Arrangement.spacedBy(10.dp)
		) {
			NodeHeader(
				displayName = displayName,
				icon = icon
			)
			content.forEach { element -> element() }
		}
	}
}
