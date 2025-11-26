package com.formation.mvc.model.metier;

public enum Etat {
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
    
    public String getNom() {
        return nom;
    }
    
    @Override
    public String toString() {
        return nom;
    }
}