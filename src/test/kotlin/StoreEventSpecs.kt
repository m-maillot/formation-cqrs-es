import event.Event
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.instanceOf
import repository.OrganismesRestantAValiderRepository
import java.util.*

class StoreEventSpecs : FunSpec({

    test("Quand je passe une liste de décisions, j'attends à sauvegarder cette liste") {
        val eventStore = EventStoreInMemory()

        val dossierId = DossierId(UUID.randomUUID())
        val events = listOf(Event.ListeOrganismeEtablie(listOf(Organisme("A"), Organisme("B"))))
        val events2 = listOf(Event.OrganismeValidé(Organisme("A")))
        eventStore.store(dossierId, events, 0)
        eventStore.store(dossierId, events2, 1)
        val eventsStored = eventStore.get(dossierId)

        eventsStored.events shouldBe events + events2
    }

    test("Quand je passe une liste de décisions pour 2 dossiers, j'attends à récupérer la liste pour le premier dossier") {
        val eventStore = EventStoreInMemory()

        val dossier1Id = DossierId(UUID.randomUUID())
        val dossier2Id = DossierId(UUID.randomUUID())
        val events = listOf(Event.ListeOrganismeEtablie(listOf(Organisme("A"), Organisme("B"))))
        val events2 = listOf(Event.OrganismeValidé(Organisme("A")))
        eventStore.store(dossier1Id, events, 0)
        eventStore.store(dossier2Id, events2, 0)
        val eventsStored = eventStore.get(dossier1Id)

        eventsStored.events shouldBe events
    }

    test("Quand je passe une liste de décisions en version 3 alors que l'event store est en version 4, j'attends une exception sur la version") {
        val eventStore = EventStoreInMemory()

        val version = 3
        val dossierId = DossierId(UUID.randomUUID())
        val events = listOf(Event.ListeOrganismeEtablie(listOf(Organisme("A"), Organisme("B"))))

        val result = kotlin.runCatching { eventStore.store(dossierId, events, version) }

        result.isFailure shouldBe true
        result.exceptionOrNull() shouldBe instanceOf<WrongVersion>()
    }

})
