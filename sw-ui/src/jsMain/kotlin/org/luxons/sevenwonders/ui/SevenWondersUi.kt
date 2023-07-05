package org.luxons.sevenwonders.ui

import kotlinx.browser.window
import kotlinx.coroutines.*
import org.luxons.sevenwonders.ui.components.*
import org.luxons.sevenwonders.ui.redux.*
import org.luxons.sevenwonders.ui.redux.sagas.*
import react.*
import react.dom.client.*
import react.redux.*
import redux.*
import web.dom.document
import web.html.*

fun main() {
    window.onload = { init() }
}

private fun init() {
    val rootElement = document.getElementById("root")
    if (rootElement == null) {
        console.error("Element with ID 'root' was not found, cannot bootstrap react app")
        return
    }
    renderRoot(rootElement)
}

private fun renderRoot(rootElement: HTMLElement) {
    val store = initRedux()
    val connectedApp = StrictMode.create {
        Provider {
            this.store = store
            Application()
        }
    }
    createRoot(rootElement).render(connectedApp)
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
