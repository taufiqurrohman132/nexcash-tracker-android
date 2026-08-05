package com.example.nexcashapplication.core.common

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

abstract class BaseViewModel<State, Event, Effect>(initialState: State) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state.asStateFlow()

    private val _effect = Channel<Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    abstract fun onEvent(event: Event)

    protected fun updateState(update: (State) -> State) {
        _state.update(update)
    }

    protected suspend fun sendEffect(effect: Effect) {
        _effect.send(effect)
    }
}
