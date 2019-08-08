package org.luxons.sevenwonders.ui.redux.sagas

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
import kotlin.coroutines.CoroutineContext

@UseExperimental(ExperimentalCoroutinesApi::class)
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

    fun startSaga(saga: Saga<S, A, R>): Job {
        check(::context.isInitialized) {
            "Before running a Saga, you must mount the Saga middleware on the Store using applyMiddleware"
        }
        return context.launch {
            saga.execute(context)
        }
    }

    suspend fun runSaga(saga: Saga<S, A, R>) {
        check(::context.isInitialized) {
            "Before running a Saga, you must mount the Saga middleware on the Store using applyMiddleware"
        }
        saga.execute(context)
    }
}

fun <S, A : RAction, R> saga(block: suspend SagaContext<S, A, R>.() -> Unit) = Saga(block)

inline fun <S, A : RAction, R, reified AT : A> actionHandlerSaga(
    noinline block: suspend SagaContext<S, A, R>.(AT) -> Unit
) = saga<S, A, R> {
    onEach<AT> {
        block(it)
    }
}

class Saga<S, A : RAction, R>(private val body: suspend SagaContext<S, A, R>.() -> Unit) {

    internal suspend fun execute(context: SagaContext<S, A, R>) = context.body()
}

@UseExperimental(FlowPreview::class, ExperimentalCoroutinesApi::class)
class SagaContext<S, A : RAction, R>(
    private val reduxApi: MiddlewareApi<S, A, R>,
    private val actions: BroadcastChannel<A>
): CoroutineScope {

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = job

    fun dispatch(action: A) {
        reduxApi.dispatch(action)
    }

    /**
     * Starts a concurrent coroutine that executes [handle] on every action dispatched.
     * Returns an "unsubscribe" function.
     */
    suspend fun onEach(handle: suspend SagaContext<S, A, R>.(A) -> Unit) {
        val channel = actions.openSubscription()
        for (a in channel) {
            if (!isActive) {
                channel.cancel()
                break
            }
            handle(a)
        }
    }

    /**
     * Starts a concurrent coroutine that executes [handle] on every action dispatched of the given type.
     * Returns an "unsubscribe" function.
     */
    suspend inline fun <reified AT : A> onEach(
        crossinline handle: suspend SagaContext<S, A, R>.(AT) -> Unit
    ) = onEach {
        if (it is AT) {
            handle(it)
        }
    }

    /**
     * Suspends until the next action matching the given [predicate] is dispatched, and returns that action.
     */
    suspend fun next(predicate: (A) -> Boolean): A {
        val channel = actions.openSubscription()
        for (a in channel) {
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

    fun fork(saga: Saga<S, A, R>) = SagaContext(reduxApi, actions).launch {
        saga.execute(this@SagaContext)
    }
}
