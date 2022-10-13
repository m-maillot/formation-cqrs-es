import command.Commande
import event.Décision
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ValidationDossierSpecs : FunSpec({

    val organisations = listOf(Organisme("A"), Organisme("B"))

    test("Quand j'acte la liste des organismes, j'attends à avoir la liste établie") {
        val commande = Commande.ActeListOrganisme(organisations)
        val newDécisions = process(listOf(), commande)
        newDécisions shouldBe listOf(Décision.ListeOrganismeEtablie(organisations))
    }

    test("Quand un organisme valide, j'attends à avoir un event organisme validé") {
        val history = listOf(Décision.ListeOrganismeEtablie(organisations))
        val commande = Commande.Valide(organisations.first())
        val newDécisions = process(history, commande)
        newDécisions shouldBe listOf(Décision.OrganismeValidé(organisations.first()))
    }

    test("Quand deuxième organisme valide, j'attends à avoir un event organisme validé et dossier validé") {
        val décisions = listOf(Décision.ListeOrganismeEtablie(organisations), Décision.OrganismeValidé(organisations.first()))
        val commande = Commande.Valide(organisations[1])
        val newDécisions = process(décisions, commande)
        newDécisions shouldBe listOf(Décision.OrganismeValidé(organisations[1]), Décision.DossierValidé)
    }

    test("Quand organisme A valide une deuxième fois, j'attends à avoir aucun event") {
        val décisions = listOf(Décision.ListeOrganismeEtablie(organisations), Décision.OrganismeValidé(organisations.first()))
        val commande = Commande.Valide(organisations.first())
        val newDécisions = process(décisions, commande)
        newDécisions shouldBe emptyList()
    }
})


