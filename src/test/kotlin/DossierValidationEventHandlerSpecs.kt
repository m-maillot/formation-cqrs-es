import event.Event
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import repository.OrganismesRestantAValiderRepository
import java.util.*

class DossierValidationEventHandlerSpecs : FunSpec({

    val organisations = listOf(Organisme("A"), Organisme("B"))
    val dossierId = DossierId(UUID.randomUUID())

    test("Quand j'acte la liste des organismes, j'attends à 2 organismes en attente") {
        val repo = OrganismesRestantAValiderRepository()
        val projection = DossierValidationEventHandler(repo)
        projection.on(dossierId, Event.ListeOrganismeEtablie(organisations))

        repo.nombreAValidé(dossierId) shouldBe 2
    }

    test("Quand je valide un organisme, j'attends à avoir 1 organismes en attente") {
        val repo = OrganismesRestantAValiderRepository()
        val projection = DossierValidationEventHandler(repo)
        projection.on(dossierId, Event.ListeOrganismeEtablie(organisations))
        projection.on(dossierId, Event.OrganismeValidé(organisations.first()))

        repo.nombreAValidé(dossierId) shouldBe 1
    }

    test("Quand je valide 2 organismes, j'attends à avoir 0 organisme en attente") {
        val repo = OrganismesRestantAValiderRepository()
        val projection = DossierValidationEventHandler(repo)
        projection.on(dossierId, Event.ListeOrganismeEtablie(organisations))
        projection.on(dossierId, Event.OrganismeValidé(organisations.first()))
        projection.on(dossierId, Event.OrganismeValidé(organisations[1]))

        repo.nombreAValidé(dossierId) shouldBe 0
    }

})


