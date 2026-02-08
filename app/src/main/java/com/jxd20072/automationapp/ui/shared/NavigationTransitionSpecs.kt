package com.jxd20072.automationapp.ui.shared

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.Scene

val displacementAnimSpec: FiniteAnimationSpec<IntOffset> = spring(
    dampingRatio = Spring.DampingRatioNoBouncy,
    stiffness = Spring.StiffnessMediumLow
)

val sizeAnimSpec: FiniteAnimationSpec<IntSize> = spring(
    dampingRatio = Spring.DampingRatioNoBouncy,
    stiffness = Spring.StiffnessMediumLow
)

val navTransitionSpec: AnimatedContentTransitionScope<Scene<NavKey>>.() -> ContentTransform = {
    ContentTransform(
        targetContentEnter = slideIntoContainer(
            AnimatedContentTransitionScope.SlideDirection.Left,
            initialOffset = { it / 3 },
            animationSpec = displacementAnimSpec
        ) + fadeIn(),
        initialContentExit = slideOutOfContainer(
            AnimatedContentTransitionScope.SlideDirection.Left,
            targetOffset = { it / 5 },
            animationSpec = displacementAnimSpec
        )
    )
}

val navPopTransitionSpec: AnimatedContentTransitionScope<Scene<NavKey>>.() -> ContentTransform = {
    ContentTransform(
        targetContentEnter = slideIntoContainer(
            AnimatedContentTransitionScope.SlideDirection.Right,
            initialOffset = { it / 5 },
            animationSpec = displacementAnimSpec
        ),
        initialContentExit = slideOutOfContainer(
            AnimatedContentTransitionScope.SlideDirection.Right,
            targetOffset = { it / 3 },
            animationSpec = displacementAnimSpec
        ) + fadeOut()
    )
}

val navPopPredictiveTransitionSpec: AnimatedContentTransitionScope<Scene<NavKey>>.(Int) -> ContentTransform =
    {
        ContentTransform(
            targetContentEnter = slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                initialOffset = { it / 5 },
                animationSpec = displacementAnimSpec
            ),
            initialContentExit = slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                targetOffset = { it / 3 },
                animationSpec = displacementAnimSpec
            ) + fadeOut()
        )
    }

val navSheetTransitionSpec: AnimatedContentTransitionScope<Scene<NavKey>>.() -> ContentTransform = {
    ContentTransform(
        targetContentEnter = fadeIn(),
        initialContentExit = fadeOut()
    )
}

val navSheetPopTransitionSpec: AnimatedContentTransitionScope<Scene<NavKey>>.() -> ContentTransform =
    {
        ContentTransform(
            targetContentEnter = fadeIn(),
            initialContentExit = fadeOut()
        )
    }

val navSheetPopPredictiveTransitionSpec: AnimatedContentTransitionScope<Scene<NavKey>>.(Int) -> ContentTransform =
    {
        ContentTransform(
            targetContentEnter = fadeIn(),
            initialContentExit = fadeOut()
        )
    }