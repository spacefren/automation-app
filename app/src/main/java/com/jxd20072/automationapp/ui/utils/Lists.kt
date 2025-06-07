package com.jxd20072.automationapp.ui.utils

fun <T> List<T>.insertAt(index: Int?, item: T): List<T> {
	val original = this

	return buildList {
		addAll(original)
		if (index != null && index in 0..size) add(index, item)
		else add(item)
	}
}

fun <T> List<T>.transformAt(index: Int, transform: (T) -> T): List<T> {
	val original = this

	return buildList {
		addAll(original.filterIndexed { i, _ -> i < index })
		add(transform(original[index]))
		addAll(original.filterIndexed { i, _ -> i > index })
	}
}