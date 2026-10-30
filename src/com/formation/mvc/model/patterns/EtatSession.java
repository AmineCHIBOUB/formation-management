package com.formation.mvc.model.patterns;

import com.formation.mvc.model.metier.Etat;
import java.util.Set;

/**
 * Pattern State : chaque état d'une session décide des actions permises.
 * Implémenté par l'énumération Etat.
 */
public interface EtatSession {
    /** Une inscription directe est-elle possible dans cet état ? */
    boolean peutInscrire();

    /** Une mise en liste d'attente est-elle possible dans cet état ? */
    boolean peutMettreEnAttente();

    /** États vers lesquels la session peut passer depuis cet état. */
    Set<Etat> getTransitionsPossibles();

    /** La transition vers l'état cible est-elle autorisée ? */
    default boolean peutPasserA(Etat cible) {
        return getTransitionsPossibles().contains(cible);
    }

    String getNom();
}
