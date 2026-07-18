package com.medsy.pharmacy.nav.root

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

fun <T : NavKey> NavBackStack<T>.navigateSingleTop(route: T) {
    if (lastOrNull() != route) {
        add(route)
    }
}
