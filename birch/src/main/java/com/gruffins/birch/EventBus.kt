package com.gruffins.birch

internal class EventBus {
    interface Listener {
        fun onEvent(event: Event)
    }

    sealed class Event {
        data class SourceUpdated(val source: Source) : Event()
    }

    private val listeners = mutableSetOf<Listener>()

    fun subscribe(listener: Listener) {
        listeners.add(listener)
    }

    fun unsubscribe(listener: Listener) {
        listeners.remove(listener)
    }

    fun publish(event: Event) {
        listeners.forEach { it.onEvent(event) }
    }

}