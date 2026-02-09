package com.jxd20072.automationapp.ui.theme

enum class NestedLevel { ONE, TWO, THREE }

fun getPreviousNestedLevel(nestedLevel: NestedLevel): NestedLevel = when (nestedLevel) {
    NestedLevel.ONE -> NestedLevel.TWO
    NestedLevel.TWO -> NestedLevel.THREE
    NestedLevel.THREE -> NestedLevel.THREE
}