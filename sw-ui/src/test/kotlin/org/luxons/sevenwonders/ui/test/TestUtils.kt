package org.luxons.sevenwonders.ui.test

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise

@OptIn(DelicateCoroutinesApi::class) // OK in JS tests
fun runSuspendingTest(testBody: suspend CoroutineScope.() -> Unit) = GlobalScope.promise { testBody() }
