package org.luxons.sevenwonders.ui.redux.sagas

import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import redux.RAction
import redux.Store
import redux.WrapperAction
import redux.applyMiddleware
import redux.compose
import redux.createStore
import redux.rEnhancer
import kotlin.test.Test
import kotlin.test.assertEquals

private data class State(val data: String)

private data class UpdateData(val newData: String) : RAction
private class DuplicateData : RAction
private class SideEffectAction(val data: String) : RAction

private fun reduce(state: State, action: RAction): State = when (action) {
    is UpdateData -> State(action.newData)
    is DuplicateData -> State(state.data + state.data)
    else -> state
}

private fun configureTestStore(initialState: State): TestRedux {
    val sagaMiddlewareFactory = SagaManager<State, RAction, WrapperAction>()
    val sagaMiddleware = sagaMiddlewareFactory.createMiddleware()
    val enhancers = compose(applyMiddleware(sagaMiddleware), rEnhancer())
    val store = createStore(::reduce, initialState, enhancers)
    return TestRedux(store, sagaMiddlewareFactory)
}

private data class TestRedux(
    val store: Store<State, RAction, WrapperAction>,
    val sagas: SagaManager<State, RAction, WrapperAction>,
)

@OptIn(ExperimentalCoroutinesApi::class) // for advanceUntilIdle
class SagaContextTest {

    @Test
    fun dispatch_dispatchesToStore() = runTest {
        val redux = configureTestStore(State("initial"))

        redux.sagas.runSaga {
            dispatch(UpdateData("Bob"))
        }

        assertEquals(State("Bob"), redux.store.getState(), "state is not as expected")
    }

    @Test
    fun next_waitsForNextAction() = runTest {
        val redux = configureTestStore(State("initial"))

        val job = redux.sagas.launchSaga(this) {
            val action = next<SideEffectAction>()
            dispatch(UpdateData("effect-${action.data}"))
        }
        advanceUntilIdle() // make sure the saga is launched

        assertEquals(State("initial"), redux.store.getState())

        redux.store.dispatch(SideEffectAction("data"))
        job.join()
        assertEquals(State("effect-data"), redux.store.getState())
    }

    @Test
    fun onEach() = runTest {
        val redux = configureTestStore(State("initial"))

        val job = redux.sagas.launchSaga(this) {
            onEach<SideEffectAction> {
                dispatch(UpdateData("effect-${it.data}"))
            }
        }
        advanceUntilIdle() // make sure the saga is launched

        assertEquals(State("initial"), redux.store.getState())

        redux.store.dispatch(SideEffectAction("data"))
        yield()
        assertEquals(State("effect-data"), redux.store.getState())
        job.cancel()
    }
}
