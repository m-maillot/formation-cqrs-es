import command.Commande
import event.Décision

private class DossierValidationDécisionProjection {

    enum class Status {
        EnCours, Validé, Refusé
    }

    var organismes: MutableSet<Organisme> = mutableSetOf()

    var organismesValidés: MutableSet<Organisme> = mutableSetOf()

    var organismeVeto: Organisme? = null

    var status: Status = Status.EnCours


    constructor(décisions: List<Décision>) {
        décisions.forEach { on(it) }
    }

    fun on(décision: Décision) {
        when (décision) {
            Décision.DossierValidé -> this.status = Status.Validé
            is Décision.OrganismeValidé -> {
                this.organismesValidés.add(décision.organisme)
            }

            is Décision.ListeOrganismeEtablie -> {
                this.organismes.addAll(décision.organismes)
            }

            is Décision.OrganismeVeto -> {
                this.organismeVeto = décision.organisme
                this.status = Status.Refusé

            }
        }
    }

}

fun process(décisions: List<Décision>, commande: Commande): List<Décision> {
    val projectionDecision = DossierValidationDécisionProjection(décisions)
    return when (commande) {
        is Commande.ActeListOrganisme -> listOf(Décision.ListeOrganismeEtablie(commande.organismes))
        is Commande.Valide -> {
            val hasBeenAdded = projectionDecision.organismesValidés.add(commande.orga)
            if (!hasBeenAdded) {
                return emptyList()
            }

            val newDécisions = mutableListOf<Décision>(Décision.OrganismeValidé(commande.orga))
            if (projectionDecision.organismesValidés == projectionDecision.organismes) {
                newDécisions.add(Décision.DossierValidé)
            }
            return newDécisions
        }
    }
}