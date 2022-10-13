package event

import Organisme

sealed class Décision {
    data class ListeOrganismeEtablie(val organismes: List<Organisme>): Décision()
    data class OrganismeValidé(val organisme: Organisme): Décision()
    object DossierValidé: Décision()
    data class OrganismeVeto(val organisme: Organisme): Décision()
}

