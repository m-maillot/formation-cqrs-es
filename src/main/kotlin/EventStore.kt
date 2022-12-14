import com.eventstore.dbclient.*
import event.Event
import java.util.concurrent.ExecutionException


interface EventStore {
    fun store(id: DossierId, events: List<Event>, version: Version)
    fun get(id: DossierId): EventHistory
}

typealias Version = Int

data class EventHistory(val events: List<Event>, val version: Version)

class EventStoreInMemory : EventStore {

    private val eventsStored: HashMap<DossierId, List<Event>> = hashMapOf()

    override fun store(id: DossierId, events: List<Event>, version: Version) {
        val storedEvents = eventsStored[id] ?: listOf()
        if (storedEvents.size != version) {
            throw WrongVersion(version, storedEvents.size)
        }
        eventsStored[id] = storedEvents + events
    }

    override fun get(id: DossierId): EventHistory {
        return (eventsStored[id] ?: emptyList()).let { EventHistory(it, it.size) }
    }

}

class EventStoreDb : EventStore {

    private var client: EventStoreDBClient

    init {
        val setts = EventStoreDBConnectionString.parseOrThrow("esdb://localhost:2113?tls=false")
        client = EventStoreDBClient.create(setts)
    }

    override fun store(id: DossierId, events: List<Event>, version: Version) {
        val options: AppendToStreamOptions =
            AppendToStreamOptions.get().expectedRevision(ExpectedRevision.expectedRevision(version.toLong() - 1L))
        val eventsData = events.map { event ->
            EventData
                .builderAsJson<Event>(event::class.java.name, event)
                .build()
        }

        try {
            client.appendToStream(id.toString(), options, eventsData.iterator())
                .get()
        } catch (e: ExecutionException) {
            val cause = e.cause
            if (cause is WrongExpectedVersionException) {
                throw WrongVersion(version, cause.actualVersion?.valueUnsigned?.toInt() ?: -1)
            }
            throw e
        }
    }

    override fun get(id: DossierId): EventHistory {
        val readStreamOptions = ReadStreamOptions.get()
            .fromStart()
            .notResolveLinkTos()

        val readResult: ReadResult = client
            .readStream(id.toString(), readStreamOptions)
            .get()
        val events = readResult.events.map { unserializable(it.originalEvent) }
        return EventHistory(events, events.size)
    }

    private fun unserializable(recorededEvent: RecordedEvent): Event {
        return when (recorededEvent.eventType) {
            Event.OrganismeValid??::class.java.name -> recorededEvent.getEventDataAs(Event.OrganismeValid??::class.java)
            Event.ListeOrganismeEtablie::class.java.name -> recorededEvent.getEventDataAs(Event.ListeOrganismeEtablie::class.java)
            Event.OrganismeVetoD??pos??::class.java.name -> recorededEvent.getEventDataAs(Event.OrganismeVetoD??pos??::class.java)
            Event.DossierValid??::class.java.name -> Event.DossierValid??
            Event.DossierRefus??::class.java.name -> Event.DossierRefus??
            else -> {
                throw IllegalStateException("Type not found")
            }
        }
    }
}

class WrongVersion(versionProvided: Version, versionExpected: Version) :
    Exception("Version incompatible. Version $versionExpected attendu, re??u version $versionProvided")