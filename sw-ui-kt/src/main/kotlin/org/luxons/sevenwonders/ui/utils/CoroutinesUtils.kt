package com.palantir.blueprintjs.org.luxons.sevenwonders.ui.utils

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.selects.select

suspend inline fun <R> awaitFirst(
    crossinline f1: suspend () -> R,
    crossinline f2: suspend () -> R
): R = coroutineScope {
    val deferred1 = async { f1() }
    val deferred2 = async { f2() }
    select<R> {
        deferred1.onAwait { deferred2.cancel(); it }
        deferred2.onAwait { deferred1.cancel(); it }
    }
}
