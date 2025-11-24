package com.formation.mvc.model.metier;

import com.formation.mvc.model.patterns.EtatSession;
import com.formation.mvc.model.patterns.Observable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Session extends Observable implements Serializable {
    private static final long serialVersionUID = 1L;
    private String codeSession;
    private Formation formation;
    private int placesMax;
    private List<Apprenant> inscrits;
    private EtatSession etat;
    
    public Session(String codeSession, Formation formation, int placesMax) {
        this.codeSession = codeSession;
        this.formation = formation;
        this.placesMax = placesMax;
        this.inscrits = new ArrayList<>();
        this.etat = Etat.OUVERT;
    }
    
    public void inscrire(Apprenant apprenant) {
        if (etat.peutInscrire() && inscrits.size() < placesMax) {
            inscrits.add(apprenant);
            addObserver(apprenant);
            etat.inscrireApprenant();
            notifyObservers("Nouvelle inscription: " + apprenant.getNom());
            
            if (inscrits.size() >= placesMax) {
                etat = Etat.COMPLET;
                notifyObservers("Session COMPLÈTE");
            }
        } else {
            System.out.println("❌ Inscription impossible");
        }
    }
    
    public void listerInscrits() {
        System.out.println("\n👥 INSCRITS: " + inscrits.size() + "/" + placesMax);
        for (Apprenant apprenant : inscrits) {
            System.out.println("• " + apprenant);
        }
    }
    
    // Getters
    public String getCodeSession() { return codeSession; }
    public Formation getFormation() { return formation; }
    public int getPlacesMax() { return placesMax; }
    public List<Apprenant> getInscrits() { return inscrits; }
    public EtatSession getEtat() { return etat; }
    
    @Override
    public String toString() {
        return "Session " + codeSession + " - " + formation.getTitre() + " [" + etat.getNom() + "]";
    }
}