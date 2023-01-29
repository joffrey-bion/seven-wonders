package org.luxons.sevenwonders.ui

import kotlinx.browser.window
import kotlinx.coroutines.*
import org.luxons.sevenwonders.ui.components.*
import org.luxons.sevenwonders.ui.redux.*
import org.luxons.sevenwonders.ui.redux.sagas.*
import react.dom.*
import react.redux.*
import redux.*
import web.dom.*
import web.dom.document

fun main() {
    window.onload = {
        val rootElement = document.getElementById("root")
        if (rootElement != null) {
            initializeAndRender(rootElement)
        } else {
            console.error("Element with ID 'root' was not found, cannot bootstrap react app")
        }
    }
}

private fun initializeAndRender(rootElement: Element) {
    val store = initRedux()

    // With the new API this might look something like:
    // createRoot(rootElement).render(FC<Props> { .. }.create())
    // See: https://github.com/karakum-team/kotlin-mui-showcase/blob/main/src/main/kotlin/team/karakum/App.kt
    @Suppress("DEPRECATION")
    render(rootElement) {
        provider(store) {
            application()
        }
    }
}

@OptIn(DelicateCoroutinesApi::class)
private fun initRedux(): Store<SwState, RAction, WrapperAction> {
    val sagaManager = SagaManager<SwState, RAction, WrapperAction>()
    val store = configureStore(sagaManager = sagaManager)
    GlobalScope.launch {
        sagaManager.launchSaga(this) {
            rootSaga()
        }
    }
    return store
}
