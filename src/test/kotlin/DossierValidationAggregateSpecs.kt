import command.Command
import event.Event
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.*

class DossierValidationAggregateSpecs : FunSpec({

    val organisations = listOf(Organisme("A"), Organisme("B"))
    val dossierId = DossierId(UUID.randomUUID())

    test("Quand j'acte la liste des organismes, j'attends à avoir la liste établie") {
        val command = Command.ActeListOrganisme(dossierId, organisations)
        val newEvents = process(listOf(), command)
        newEvents shouldBe listOf(Event.ListeOrganismeEtablie(organisations))
    }

    test("Quand un organisme valide, j'attends à avoir un event organisme validé") {
        val history = listOf(Event.ListeOrganismeEtablie(organisations))
        val command = Command.Valide(dossierId, organisations.first())
        val newEvents = process(history, command)
        newEvents shouldBe listOf(Event.OrganismeValidé(organisations.first()))
    }

    test("Quand deuxième organisme valide, j'attends à avoir un event organisme validé et dossier validé") {
        val events =
            listOf(Event.ListeOrganismeEtablie(organisations), Event.OrganismeValidé(organisations.first()))
        val command = Command.Valide(dossierId, organisations[1])
        val newEvents = process(events, command)
        newEvents shouldBe listOf(Event.OrganismeValidé(organisations[1]), Event.DossierValidé)
    }

    test("Quand organisme A valide une deuxième fois, j'attends à avoir aucun event") {
        val events =
            listOf(Event.ListeOrganismeEtablie(organisations), Event.OrganismeValidé(organisations.first()))
        val command = Command.Valide(dossierId, organisations.first())
        val newEvents = process(events, command)
        newEvents shouldBe emptyList()
    }

    test("Quand organisme A pose son véto, j'attends à avoir une décision Dossier Réfusé et Véto déposé") {
        val events = listOf(Event.ListeOrganismeEtablie(organisations))
        val command = Command.PoseVeto(dossierId, organisations.first())
        val newEvents = process(events, command)
        newEvents shouldBe listOf(Event.OrganismeVetoDéposé(organisations.first()), Event.DossierRefusé)
    }

    test("Quand organisme A pose son véto et Organisme B valide, j'attends à avoir une erreur") {
        val events = listOf(
            Event.ListeOrganismeEtablie(organisations),
            Event.OrganismeVetoDéposé(organisations.first()),
            Event.DossierRefusé
        )
        val command = Command.Valide(dossierId, organisations[1])
        val newEvents = kotlin.runCatching { process(events, command) }
        newEvents.isFailure shouldBe true
    }
})


