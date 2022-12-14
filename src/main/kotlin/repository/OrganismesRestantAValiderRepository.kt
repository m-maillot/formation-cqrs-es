package repository

import DossierId
import NombreAValider
import java.util.HashMap

class OrganismesRestantAValiderRepository(val dossierAValid√©: HashMap<DossierId, NombreAValider> = hashMapOf()) {

    fun nombreAValid√©(dossierId: DossierId): Int {
        return dossierAValid√©[dossierId] ?: 0
    }
}