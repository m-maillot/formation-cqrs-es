import event.Event
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import repository.OrganismesRestantAValiderRepository
import java.util.*

class EventPublisherSpecs : FunSpec({

    test("Quand je passe une décision, j'attends à appeler les différents handler") {
        var subscriberHasBeenCall = false
        val eventHandler = object : EventHandler {
            override fun on(dossierId: DossierId, event: Event) {
                subscriberHasBeenCall = true
            }
        }
        var eventStoreHasBeenCalled = false
        val eventStore = object : EventStore {
            override fun store(id: DossierId, events: List<Event>, version: Version) {
                eventStoreHasBeenCalled = true
            }

            override fun get(id: DossierId): EventHistory {
                TODO("Not yet implemented")
            }
        }

        val eventPublisher = EventPublisherImpl(eventStore, listOf(eventHandler))
        eventPublisher.publish(DossierId(UUID.randomUUID()), listOf(Event.ListeOrganismeEtablie(listOf())), 0)

        eventStoreHasBeenCalled shouldBe true
        subscriberHasBeenCall shouldBe true
    }

    test("Quand je passe une décision, j'attends à ce que la projection soit à jour") {
        val eventStore = object : EventStore {
            override fun store(id: DossierId, events: List<Event>, version: Version) {
            }

            override fun get(id: DossierId): EventHistory{
                TODO("Not yet implemented")
            }
        }

        val dossierId = DossierId(UUID.randomUUID())
        val repo = OrganismesRestantAValiderRepository()
        val eventPublisher = EventPublisherImpl(eventStore, listOf(DossierValidationEventHandler(repo)))
        eventPublisher.publish(
            dossierId,
            listOf(Event.ListeOrganismeEtablie(listOf(Organisme("A"), Organisme("B")))),
            0
        )

        repo.nombreAValidé(dossierId) shouldBe 2
    }
})


