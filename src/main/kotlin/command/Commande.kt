package command

import Organisme

sealed class Commande {
    data class ActeListOrganisme(val organismes: List<Organisme>): Commande()
    data class Valide(val orga: Organisme) : Commande()
}
