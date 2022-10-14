import event.Event

interface EventHandler {
    fun on(dossierId: DossierId, event: Event)
}

interface EventPublisher {
    fun publish(id: DossierId, events: List<Event>, version: Version)
}

class EventPublisherImpl(
    private val eventStore: EventStore,
    private val eventHandlers: List<EventHandler>
) : EventPublisher {

    override fun publish(id: DossierId, events: List<Event>, version: Version) {
        eventStore.store(id, events, version)
        events.forEach { event ->
            eventHandlers.forEach { it.on(id, event) }
        }
    }
}