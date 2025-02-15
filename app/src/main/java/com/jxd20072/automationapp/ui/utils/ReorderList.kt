package com.jxd20072.automationapp.ui.utils

fun <T> reorderElementInList(list: List<T>, oldIndex: Int, newIndex: Int): List<T> {
	return list
		.toMutableList()
		.also {
			val element = it.removeAt(oldIndex)
			it.add(if (oldIndex < newIndex) newIndex - 1 else newIndex, element)
		}
}