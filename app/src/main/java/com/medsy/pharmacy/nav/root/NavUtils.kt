package com.medsy.pharmacy.nav.root

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

fun <T : NavKey> NavBackStack<T>.push(route: T) {
    if (lastOrNull() != route) {
        add(route)
    }
}

fun <T : NavKey> NavBackStack<T>.pop() {
    if (size > 1) {
        removeLastOrNull()
    }
}

fun <T : NavKey> NavBackStack<T>.replace(route: T) {
    removeLastOrNull()
    push(route)
}

fun <T : NavKey> NavBackStack<T>.setRoot(root: T) {
    clear()
    add(root)
}

fun <T : NavKey> NavBackStack<T>.popTo(route: T) {
    while (size > 1 && last() != route) {
        removeLastOrNull()
    }
}
