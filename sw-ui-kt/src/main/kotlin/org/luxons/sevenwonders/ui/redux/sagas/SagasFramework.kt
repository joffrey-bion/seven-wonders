package org.luxons.sevenwonders.ui.redux.sagas

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.promise
import redux.Middleware
import redux.MiddlewareApi
import redux.RAction
import kotlin.coroutines.coroutineContext

@OptIn(ExperimentalCoroutinesApi::class)
class SagaManager<S, A : RAction, R>(
    private val monitor: ((A) -> Unit)? = null
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
        GlobalScope.promise { actions.send(action) }
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
    private val reduxApi: MiddlewareApi<S, A, R>, private val actions: BroadcastChannel<A>
) {
    /**
     * Dispatches the given redux [action].
     */
    fun dispatch(action: A) {
        reduxApi.dispatch(action)
    }

    /**
     * Executes [handle] on every action dispatched. This runs forever until the current coroutine is cancelled.
     */
    suspend fun onEach(handle: suspend SagaContext<S, A, R>.(A) -> Unit) {
        val channel = actions.openSubscription()
        for (a in channel) {
            if (!coroutineContext.isActive) {
                channel.cancel()
                break
            }
            handle(a)
        }
    }

    /**
     * Executes [handle] on every action dispatched of the type [T]. This runs forever until the current coroutine is
     * cancelled.
     */
    suspend inline fun <reified T : A> onEach(
        crossinline handle: suspend SagaContext<S, A, R>.(T) -> Unit
    ) = onEach {
        if (it is T) {
            handle(it)
        }
    }

    /**
     * Suspends until the next action matching the given [predicate] is dispatched, and returns that action.
     */
    suspend fun next(predicate: (A) -> Boolean): A {
        val channel = actions.openSubscription()
        for (a in channel) {
            if (!coroutineContext.isActive) {
                channel.cancel()
                throw CancellationException("The expected action was not received before cancellation")
            }
            if (predicate(a)) {
                channel.cancel()
                return a
            }
        }
        throw IllegalStateException("Actions channel closed before receiving a matching action")
    }

    /**
     * Suspends until the next action of type [T] is dispatched, and returns that action.
     */
    suspend inline fun <reified T : A> next(): T = next { it is T } as T
}
