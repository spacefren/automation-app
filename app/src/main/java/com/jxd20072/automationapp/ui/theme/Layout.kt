package com.jxd20072.automationapp.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class ShadowSystem(
	val primary: Dp,
	val secondary: Dp
)

data class ShapeSystem(
	val cornerRadiusLevel1: Dp,
	val cornerRadiusLevel2: Dp,
	val cornerRadiusLevel3: Dp,
)

data class SpacingSystem(
	val paddingLevel1: Dp,
	val paddingLevel2: Dp,
	val paddingLevel3: Dp,
)

data class LayoutSystem(
	val shadows: ShadowSystem,
	val shapes: ShapeSystem,
	val spacing: SpacingSystem
)

val defaultLayoutSystem = LayoutSystem(
	shadows = ShadowSystem(
		primary = 5.dp,
		secondary = 2.dp,
	),
	shapes = ShapeSystem(
		cornerRadiusLevel1 = 10.dp,
		cornerRadiusLevel2 = 5.dp,
		cornerRadiusLevel3 = 2.dp,
	),
	spacing = SpacingSystem(
		paddingLevel1 = 20.dp,
		paddingLevel2 = 10.dp,
		paddingLevel3 = 6.dp
	)
)