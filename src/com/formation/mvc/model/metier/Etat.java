package com.formation.mvc.model.metier;

import com.formation.mvc.model.patterns.EtatSession;
import java.util.EnumSet;
import java.util.Set;

/**
 * États d'une session de formation (pattern State).
 *
 * Cycle de vie :
 * PLANIFIEE -> OUVERTE -> (PLEINE) -> FERMEE -> EN_COURS -> TERMINEE
 * Une session peut être annulée tant qu'elle n'est pas terminée.
 */
public enum Etat implements EtatSession {
    OUVERTE("Ouverte"),
    PLANIFIEE("Planifiée"),
    EN_COURS("En cours"),
    TERMINEE("Terminée"),
    ANNULEE("Annulée"),
    FERMEE("Fermée"),
    PLEINE("Pleine");

    private final String nom;

    Etat(String nom) {
        this.nom = nom;
    }

    @Override
    public boolean peutInscrire() {
        return this == OUVERTE || this == PLANIFIEE;
    }

    @Override
    public boolean peutMettreEnAttente() {
        return this == PLEINE;
    }

    @Override
    public Set<Etat> getTransitionsPossibles() {
        switch (this) {
            case PLANIFIEE: return EnumSet.of(OUVERTE, ANNULEE);
            case OUVERTE:   return EnumSet.of(PLANIFIEE, PLEINE, FERMEE, EN_COURS, ANNULEE);
            case PLEINE:    return EnumSet.of(OUVERTE, FERMEE, EN_COURS, ANNULEE);
            case FERMEE:    return EnumSet.of(OUVERTE, EN_COURS, ANNULEE);
            case EN_COURS:  return EnumSet.of(TERMINEE, ANNULEE);
            default:        return EnumSet.noneOf(Etat.class); // TERMINEE et ANNULEE sont définitifs
        }
    }

    @Override
    public String getNom() {
        return nom;
    }

    @Override
    public String toString() {
        return nom;
    }
}
