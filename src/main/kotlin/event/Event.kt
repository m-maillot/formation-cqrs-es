package event

import Organisme

sealed class Event {
    data class ListeOrganismeEtablie(val organismes: List<Organisme>) : Event() {
        constructor() : this(emptyList())
    }

    data class OrganismeValidé(val organisme: Organisme) : Event() {
        constructor() : this(Organisme(""))
    }

    object DossierValidé : Event()
    object DossierRefusé : Event()
    data class OrganismeVetoDéposé(val organisme: Organisme) : Event() {
        constructor() : this(Organisme(""))
    }
}

