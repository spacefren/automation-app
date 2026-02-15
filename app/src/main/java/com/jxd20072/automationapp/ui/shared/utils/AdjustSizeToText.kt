package com.jxd20072.automationapp.ui.shared.utils

import androidx.compose.ui.text.TextLayoutResult

fun getTextSize(textLayoutResult: TextLayoutResult): Float {
    // I know I'm genius
    val lineEnds = mutableListOf<Float>()
    for (i in 0..textLayoutResult.lineCount - 1) {
        lineEnds.add(textLayoutResult.getLineRight(i) - textLayoutResult.getLineLeft(i))
    }
    return lineEnds.max()
}