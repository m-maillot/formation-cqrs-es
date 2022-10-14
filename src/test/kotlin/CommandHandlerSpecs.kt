import command.Command
import event.Event
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.*

class CommandHandlerSpecs : FunSpec({

    test("Quand je passe une commande, j'attends à appeler le store et le publisher") {
        var eventPublisherHasBeenCall = false
        val eventPublisher = object : EventPublisher {
            override fun publish(id: DossierId, events: List<Event>, version: Version) {
                eventPublisherHasBeenCall = true
            }

        }
        var eventStoreHasBeenCalled = false
        val eventStore = object : EventStore {
            override fun store(id: DossierId, events: List<Event>, version: Version) {

            }

            override fun get(id: DossierId): EventHistory {
                eventStoreHasBeenCalled = true
                return EventHistory(emptyList(), 0)
            }
        }

        val commandHandler = CommandHandler(eventStore, eventPublisher)
        commandHandler.on(Command.ActeListOrganisme(DossierId(UUID.randomUUID()), emptyList()))

        eventStoreHasBeenCalled shouldBe true
        eventPublisherHasBeenCall shouldBe true
    }
})


