package com.formation.mvc.model.metier;

import java.io.Serializable;

public class Formateur implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nom;
    private String specialite;
    private int anneeExperience;
    
    public Formateur(String nom, String specialite, int anneeExperience) {
        this.nom = nom;
        this.specialite = specialite;
        this.anneeExperience = anneeExperience;
    }
    
    // Getters
    public String getNom() { return nom; }
    public String getSpecialite() { return specialite; }
    public int getAnneeExperience() { return anneeExperience; }
    
    @Override
    public String toString() {
        return String.format("%s - %s (%d années)", nom, specialite, anneeExperience);
    }
}