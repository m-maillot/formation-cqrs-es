import command.Command

class CommandHandler(
    val eventStore: EventStore,
    val eventPublisher: EventPublisher,
) {

    fun on(command: Command) {
        val history = eventStore.get(command.id)
        val events = process(history.events, command)
        eventPublisher.publish(command.id, events, history.version)
    }
}