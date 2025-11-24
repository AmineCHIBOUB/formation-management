package com.formation.mvc.model.metier;

import com.formation.mvc.model.patterns.Observer;
import java.io.Serializable;

public class Apprenant implements Observer, Serializable {
    private static final long serialVersionUID = 1L;
    private String nom;
    private String entreprise;
    
    public Apprenant(String nom, String entreprise) {
        this.nom = nom;
        this.entreprise = entreprise;
    }
    
    @Override
    public void update(String message) {
        System.out.println("📩 " + nom + ": " + message);
    }
    
    public String getNom() { return nom; }
    public String getEntreprise() { return entreprise; }
    
    @Override
    public String toString() {
        return nom + " (" + entreprise + ")";
    }
}