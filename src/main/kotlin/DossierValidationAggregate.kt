import command.Command
import event.Event

fun process(events: List<Event>, command: Command): List<Event> {
    val projectionDecision = DossierValidationEventProjection(events)
    return when (command) {
        is Command.ActeListOrganisme -> listOf(Event.ListeOrganismeEtablie(command.organismes))
        is Command.Valide -> {
            if (projectionDecision.vetoPosé) {
                throw IllegalStateException("Dossier a été refusé")
            }
            if (!projectionDecision.organismesNonValidés.contains(command.orga)) {
                return emptyList()
            }

            val eventValidé = Event.OrganismeValidé(command.orga)
            val newEvents = mutableListOf<Event>(Event.OrganismeValidé(command.orga))
            projectionDecision.on(eventValidé)
            if (projectionDecision.organismesNonValidés.isEmpty()) {
                newEvents.add(Event.DossierValidé)
            }
            return newEvents
        }

        is Command.PoseVeto -> {
            listOf(Event.OrganismeVetoDéposé(command.orga), Event.DossierRefusé)
        }
    }
}

private class DossierValidationEventProjection(events: List<Event>) {

    var organismesNonValidés: MutableSet<Organisme> = mutableSetOf()

    var vetoPosé: Boolean = false

    init {
        events.forEach { on(it) }
    }

    fun on(event: Event) {
        when (event) {
            Event.DossierValidé -> {
                // Do nothing
            }

            is Event.OrganismeValidé -> {
                this.organismesNonValidés.remove(event.organisme)
            }

            is Event.ListeOrganismeEtablie -> {
                this.organismesNonValidés.addAll(event.organismes)
            }

            is Event.OrganismeVetoDéposé -> {
                vetoPosé = true
            }

            Event.DossierRefusé -> {
                // Do nothing
            }
        }
    }

}