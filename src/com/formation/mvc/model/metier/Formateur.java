package com.formation.mvc.model.metier;

import com.formation.mvc.model.patterns.Observer;
import java.io.Serializable;

public class Formateur implements Observer, Serializable {
    private static final long serialVersionUID = 1L;
    private String nom;
    private String specialite;
    
    public Formateur(String nom, String specialite) {
        this.nom = nom;
        this.specialite = specialite;
    }
    
    @Override
    public void update(String message) {
        System.out.println("👨‍🏫 Notification Formateur " + nom + ": " + message);
    }
    
    public String getNom() { return nom; }
    public String getSpecialite() { return specialite; }
    
    @Override
    public String toString() {
        return nom + " (" + specialite + ")";
    }
}