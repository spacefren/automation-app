package com.jxd20072.automationapp.ui.theme

enum class NestedLevel { ONE, TWO, THREE }

fun getNextNestedLevel(nestedLevel: NestedLevel): NestedLevel = when (nestedLevel) {
    NestedLevel.ONE -> NestedLevel.TWO
    NestedLevel.TWO -> NestedLevel.THREE
    NestedLevel.THREE -> NestedLevel.THREE
}

fun getPreviousNestedLevel(nestedLevel: NestedLevel): NestedLevel = when (nestedLevel) {
    NestedLevel.ONE -> NestedLevel.ONE
    NestedLevel.TWO -> NestedLevel.ONE
    NestedLevel.THREE -> NestedLevel.TWO
}