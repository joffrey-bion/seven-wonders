package org.luxons.sevenwonders.ui.redux.sagas

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.promise
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

private data class UpdateData(val newData: String): RAction
private class DuplicateData: RAction
private class SideEffectAction(val data: String): RAction

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
    val sagas: SagaManager<State, RAction, WrapperAction>
)

@UseExperimental(ExperimentalCoroutinesApi::class)
class SagaContextTest {

    @Test
    fun dispatch(): dynamic = GlobalScope.promise {

        val redux = configureTestStore(State("initial"))

        val saga = saga<State, RAction, WrapperAction> {
            dispatch(UpdateData("Bob"))
        }
        redux.sagas.runSaga(saga)

        assertEquals(State("Bob"), redux.store.getState(), "state is not as expected")
    }

    @Test
    fun next(): dynamic = GlobalScope.promise {
        val redux = configureTestStore(State("initial"))

        val saga = saga<State, RAction, WrapperAction> {
            val action = next<SideEffectAction>()
            dispatch(UpdateData("effect-${action.data}"))
        }
        redux.sagas.startSaga(saga)

        assertEquals(State("initial"), redux.store.getState())

        redux.store.dispatch(SideEffectAction("data"))
        delay(50)
        assertEquals(State("effect-data"), redux.store.getState())
    }

    @Test
    fun onEach(): dynamic = GlobalScope.promise {

        val redux = configureTestStore(State("initial"))

        val saga = saga<State, RAction, WrapperAction> {
            onEach<SideEffectAction> {
                dispatch(UpdateData("effect-${it.data}"))
            }
        }
        val job = redux.sagas.startSaga(saga)

        assertEquals(State("initial"), redux.store.getState())

        redux.store.dispatch(SideEffectAction("data"))
        delay(50)
        assertEquals(State("effect-data"), redux.store.getState())
        job.cancel()
    }
}
