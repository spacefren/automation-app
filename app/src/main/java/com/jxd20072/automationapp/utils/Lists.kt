package com.jxd20072.automationapp.utils

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

fun <T> List<T>.findAndReplace(search: (T) -> Boolean, replacement: T): List<T> {
    return this.map { if (search(it)) replacement else it }
}

fun <T> List<T>.findAndTransform(search: (T) -> Boolean, transform: (T) -> T): List<T> {
    return this.map { if (search(it)) transform(it) else it }
}

fun <T> List<T>.findAndTransformRecursive(
    search: (T) -> Boolean,
    transform: (T) -> T,
    getContents: (T) -> List<T>,
    setContents: (T, List<T>) -> T
): List<T> {
    return this.map {
        if (search(it)) transform(it)
        else setContents(
            it, getContents(it).findAndTransformRecursive(
                search, transform, getContents, setContents
            )
        )
    }
}

fun <T> List<T>.findAndReplaceRecursive(
    search: (T) -> Boolean,
    replacement: T,
    getContents: (T) -> List<T>,
    setContents: (T, List<T>) -> T
): List<T> {
    return this.map {
        if (search(it)) replacement
        else setContents(
            it, getContents(it).findAndReplaceRecursive(
                search, replacement, getContents, setContents
            )
        )
    }
}

fun <T, V> List<T>.associateWithIndexed(valueSelector: (element: T, index: Int) -> V): Map<T, V> {
    val map = mutableMapOf<T, V>()
    this.forEachIndexed { index, element ->
        map[element] = valueSelector(element, index)
    }
    return map
}