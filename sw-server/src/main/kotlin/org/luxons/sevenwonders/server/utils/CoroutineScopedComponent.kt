package org.luxons.sevenwonders.server.utils

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import javax.annotation.PreDestroy

open class CoroutineScopedComponent {

    protected val componentScope: CoroutineScope =
        CoroutineScope(CoroutineName("${this::class.simpleName}-coroutine") + SupervisorJob())

    @PreDestroy
    fun cancelScope() {
        componentScope.cancel()
    }
}
