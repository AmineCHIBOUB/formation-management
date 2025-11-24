package com.formation.mvc.model.metier;

import com.formation.mvc.model.patterns.EtatSession;
import java.io.Serializable;

public class Etat implements EtatSession, Serializable {
    private static final long serialVersionUID = 1L;
    private String nom;
    private boolean peutInscrire;
    
    public Etat(String nom, boolean peutInscrire) {
        this.nom = nom;
        this.peutInscrire = peutInscrire;
    }
    
    @Override
    public boolean peutInscrire() {
        return peutInscrire;
    }
    
    @Override
    public String getNom() {
        return nom;
    }
    
    @Override
    public void inscrireApprenant() {
        if (peutInscrire) {
            System.out.println("✅ Inscription réussie - Session " + nom);
        } else {
            System.out.println("❌ Impossible de s'inscrire - Session " + nom);
        }
    }
    
    // États prédéfinis
    public static Etat OUVERT = new Etat("OUVERT", true);
    public static Etat COMPLET = new Etat("COMPLET", false);
}