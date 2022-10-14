package command

import DossierId
import Organisme

sealed class Command(
    val id: DossierId
) {
    data class ActeListOrganisme(
        val dossierId: DossierId,
        val organismes: List<Organisme>
    ) : Command(dossierId)

    data class Valide(
        val dossierId: DossierId, val orga: Organisme
    ) : Command(dossierId)

    data class PoseVeto(
        val dossierId: DossierId, val orga: Organisme
    ) : Command(dossierId)
}
