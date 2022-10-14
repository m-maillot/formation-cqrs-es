import event.Event
import repository.OrganismesRestantAValiderRepository
import java.util.*

@JvmInline
value class DossierId(val id: UUID)

typealias NombreAValider = Int

class DossierValidationEventHandler(
    private val repo: OrganismesRestantAValiderRepository = OrganismesRestantAValiderRepository(),
) : EventHandler {

    override fun on(dossierId: DossierId, event: Event) {
        when (event) {
            Event.DossierValidé -> {
                repo.dossierAValidé[dossierId] = 0
            }

            is Event.ListeOrganismeEtablie -> {
                repo.dossierAValidé[dossierId] = event.organismes.size
            }

            is Event.OrganismeValidé -> {
                val dossierNombreOrganismeAValidé =
                    repo.dossierAValidé[dossierId] ?: throw IllegalArgumentException("Dossier invalide")
                repo.dossierAValidé[dossierId] = dossierNombreOrganismeAValidé - 1
            }

            is Event.OrganismeVetoDéposé -> {
            }

            Event.DossierRefusé -> {

            }
        }
    }
}