package com.gruffins.birch

import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EventBusTest {
    internal class TestListener: EventBus.Listener {
        internal val events = mutableListOf<EventBus.Event>()

        override fun onEvent(event: EventBus.Event) {
            events.add(event)
        }
    }

    private lateinit var listener: TestListener
    private lateinit var eventBus: EventBus

    @Before
    fun setup() {
        listener = TestListener()
        eventBus = EventBus()
    }

    @Test
    fun `subscribe receives events`() {
        val source = mockk<Source>()
        eventBus.subscribe(listener)
        eventBus.publish(EventBus.Event.SourceUpdated(source))
        assert(listener.events.isNotEmpty())
    }

    @Test
    fun `unsubscribe does not receive events`() {
        val source = mockk<Source>()
        eventBus.subscribe(listener)
        eventBus.unsubscribe(listener)
        eventBus.publish(EventBus.Event.SourceUpdated(source))
        assert(listener.events.isEmpty())
    }

}