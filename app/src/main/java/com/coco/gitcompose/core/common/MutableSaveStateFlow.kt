package com.coco.gitcompose.core.common

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


const val UI_STATE_KEY = "UI_STATE_KEY"

class MutableSaveStateFlow<T>(
    private val savedStateHandle: SavedStateHandle,
    private val key: String,
    defaultValue: T
) : MutableStateFlow<T> {

    private val _state: MutableStateFlow<T> =
        MutableStateFlow(savedStateHandle.get<T>(key) ?: defaultValue)
    override val replayCache: List<T>
        get() = _state.replayCache
    override val subscriptionCount: StateFlow<Int>
        get() = _state.subscriptionCount
    override var value: T
        get() = _state.value
        set(value) {
            _state.value = value
            savedStateHandle[key] = value
        }

    override suspend fun collect(collector: FlowCollector<T>): Nothing {
        _state.collect(collector)
    }

    override fun compareAndSet(expect: T, update: T): Boolean {
        val compareAndSet = _state.compareAndSet(expect, update)
        if (compareAndSet) {
            savedStateHandle[key] = update
        }
        return compareAndSet
    }

    @ExperimentalCoroutinesApi
    override fun resetReplayCache() {
        _state.resetReplayCache()
    }

    override fun tryEmit(value: T): Boolean {
        return _state.tryEmit(value)
    }

    override suspend fun emit(value: T) {
        _state.emit(value)
    }
}

fun <T> SavedStateHandle.getMutableStateFlow(
    initialValue: T,
    key: String = UI_STATE_KEY
): MutableSaveStateFlow<T> =
    MutableSaveStateFlow(this, key, initialValue)