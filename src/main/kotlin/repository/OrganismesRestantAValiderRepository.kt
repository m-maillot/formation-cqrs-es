package repository

import DossierId
import NombreAValider
import java.util.HashMap

class OrganismesRestantAValiderRepository(val dossierAValidé: HashMap<DossierId, NombreAValider> = hashMapOf()) {

    fun nombreAValidé(dossierId: DossierId): Int {
        return dossierAValidé[dossierId] ?: 0
    }
}