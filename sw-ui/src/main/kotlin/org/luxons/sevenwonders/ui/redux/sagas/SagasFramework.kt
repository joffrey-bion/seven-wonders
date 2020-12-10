package org.luxons.sevenwonders.ui.redux.sagas

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import redux.Middleware
import redux.MiddlewareApi
import redux.RAction

@OptIn(ExperimentalCoroutinesApi::class)
class SagaManager<S, A : RAction, R>(
    private val monitor: ((A) -> Unit)? = null,
) {
    private lateinit var context: SagaContext<S, A, R>

    private val actions = BroadcastChannel<A>(16)

    fun createMiddleware(): Middleware<S, A, R, A, R> = ::sagasMiddleware

    private fun sagasMiddleware(api: MiddlewareApi<S, A, R>): ((A) -> R) -> (A) -> R {
        context = SagaContext(api, actions)
        return { nextDispatch ->
            { action ->
                onActionDispatched(action)
                val result = nextDispatch(action)
                handleAction(action)
                result
            }
        }
    }

    private fun onActionDispatched(action: A) {
        monitor?.invoke(action)
    }

    private fun handleAction(action: A) {
        GlobalScope.launch { actions.send(action) }
    }

    fun launchSaga(coroutineScope: CoroutineScope, saga: suspend SagaContext<S, A, R>.() -> Unit): Job {
        checkMiddlewareApplied()
        return coroutineScope.launch {
            context.saga()
        }
    }

    suspend fun runSaga(saga: suspend SagaContext<S, A, R>.() -> Unit) {
        checkMiddlewareApplied()
        context.saga()
    }

    private fun checkMiddlewareApplied() {
        check(::context.isInitialized) {
            "Before running a Saga, you must mount the Saga middleware on the Store using applyMiddleware"
        }
    }
}

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class SagaContext<S, A : RAction, R>(
    private val reduxApi: MiddlewareApi<S, A, R>,
    private val actions: BroadcastChannel<A>,
) {
    /**
     * Gets the current redux state.
     */
    fun getState(): S = reduxApi.getState()

    /**
     * Dispatches the given redux [action].
     */
    fun dispatch(action: A) {
        reduxApi.dispatch(action)
    }

    /**
     * Dispatches all actions from this flow.
     */
    suspend fun Flow<A>.dispatchAll() {
        collect {
            reduxApi.dispatch(it)
        }
    }

    /**
     * Dispatches all actions from this flow in the provided [scope].
     */
    fun Flow<A>.dispatchAllIn(scope: CoroutineScope): Job = scope.launch { dispatchAll() }

    /**
     * Executes [handle] on every action dispatched. This runs forever until the current coroutine is cancelled.
     */
    suspend fun onEach(handle: suspend SagaContext<S, A, R>.(A) -> Unit) {
        val channel = actions.openSubscription()
        try {
            for (a in channel) {
                handle(a)
            }
        } finally {
            channel.cancel()
        }
    }

    /**
     * Executes [handle] on every action dispatched of the type [T]. This runs forever until the current coroutine is
     * cancelled.
     */
    suspend inline fun <reified T : A> onEach(
        crossinline handle: suspend SagaContext<S, A, R>.(T) -> Unit,
    ) = onEach {
        if (it is T) {
            handle(it)
        }
    }

    /**
     * Launches a coroutine in the receiver scope that executes [handle] on every action dispatched of the type [T].
     * The returned [Job] can be used to cancel that coroutine (just like a regular [launch])
     */
    inline fun <reified T : A> CoroutineScope.launchOnEach(
        crossinline handle: suspend SagaContext<S, A, R>.(T) -> Unit,
    ): Job = launch { onEach(handle) }

    /**
     * Suspends until the next action matching the given [predicate] is dispatched, and returns that action.
     */
    suspend fun next(predicate: (A) -> Boolean): A {
        val channel = actions.openSubscription()
        try {
            for (a in channel) {
                if (predicate(a)) {
                    return a
                }
            }
        } finally {
            channel.cancel()
        }
        error("Actions channel closed before receiving a matching action")
    }

    /**
     * Suspends until the next action of type [T] is dispatched, and returns that action.
     */
    suspend inline fun <reified T : A> next(): T = next { it is T } as T
}
