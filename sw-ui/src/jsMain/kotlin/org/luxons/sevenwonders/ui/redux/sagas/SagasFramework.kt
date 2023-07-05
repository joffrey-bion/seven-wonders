package org.luxons.sevenwonders.ui.redux.sagas

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*
import redux.Middleware
import redux.MiddlewareApi
import redux.RAction

class SagaManager<S, A : RAction, R>(
    private val monitor: ((A) -> Unit)? = null,
) {
    private lateinit var context: SagaContext<S, A, R>

    private val actions = MutableSharedFlow<A>(extraBufferCapacity = Channel.UNLIMITED)

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
        val emitted = actions.tryEmit(action)
        if (!emitted) {
            // should never happen since our buffer is 'unlimited' (in reality it's Int.MAX_VALUE)
            error("Couldn't dispatch redux action, buffer is full")
        }
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

class SagaContext<S, A : RAction, R>(
    private val reduxApi: MiddlewareApi<S, A, R>,
    val reduxActions: SharedFlow<A>,
) {
    /**
     * The current redux state.
     */
    val reduxState: S
        get() = reduxApi.getState()

    /**
     * Dispatches the given redux [action].
     */
    fun dispatch(action: A) {
        reduxApi.dispatch(action)
    }

    /**
     * Executes [handle] on every action dispatched of the type [T]. This runs forever until the current coroutine is
     * cancelled.
     */
    suspend inline fun <reified T : A> onEach(
        crossinline handle: suspend SagaContext<S, A, R>.(T) -> Unit,
    ) {
        reduxActions.filterIsInstance<T>().collect { handle(it) }
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
    suspend fun next(predicate: (A) -> Boolean): A = reduxActions.first { predicate(it) }

    /**
     * Suspends until the next action of type [T] is dispatched, and returns that action.
     */
    suspend inline fun <reified T : A> next(): T = reduxActions.filterIsInstance<T>().first()
}
