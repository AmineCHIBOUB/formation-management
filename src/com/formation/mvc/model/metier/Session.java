package com.formation.mvc.model.metier;

import java.util.*;

public class Session {
    private String codeSession;
    private Formation formation;
    private int placesMax;
    private Etat etat;
    private List<Apprenant> inscrits;
    
    public Session(String codeSession, Formation formation, int placesMax) {
        this.codeSession = codeSession;
        this.formation = formation;
        this.placesMax = placesMax;
        this.etat = Etat.OUVERTE;
        this.inscrits = new ArrayList<>();
    }
    
    // Getters
    public String getCodeSession() { return codeSession; }
    public Formation getFormation() { return formation; }
    public int getPlacesMax() { return placesMax; }
    public Etat getEtat() { return etat; }
    public List<Apprenant> getInscrits() { return new ArrayList<>(inscrits); }
    
    // Setters manquants
    public void setPlacesMax(int placesMax) {
        this.placesMax = placesMax;
    }
    
    // Méthodes d'inscription
    public boolean inscrire(Apprenant apprenant) {
        if (inscrits.size() < placesMax && !inscrits.contains(apprenant)) {
            inscrits.add(apprenant);
            
            // Mettre à jour l'état si la session est pleine
            if (inscrits.size() >= placesMax) {
                etat = Etat.PLEINE;
            }
            return true;
        }
        return false;
    }
    
    public boolean desinscrire(Apprenant apprenant) {
        boolean removed = inscrits.remove(apprenant);
        if (removed && etat == Etat.PLEINE) {
            etat = Etat.OUVERTE;
        }
        return removed;
    }
    
    public int getPlacesRestantes() {
        return placesMax - inscrits.size();
    }
    
    public double getTauxRemplissage() {
        return (double) inscrits.size() / placesMax * 100;
    }
    
    // Méthodes de gestion d'état
    public void ouvrir() {
        this.etat = Etat.OUVERTE;
    }
    
    public void fermer() {
        this.etat = Etat.FERMEE;
    }
    
    public void commencer() {
        this.etat = Etat.EN_COURS;
    }
    
    public void terminer() {
        this.etat = Etat.TERMINEE;
    }
    
    // Méthode pour changer l'état directement
    public void setEtat(Etat nouvelEtat) {
        this.etat = nouvelEtat;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Session session = (Session) obj;
        return Objects.equals(codeSession, session.codeSession);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(codeSession);
    }
    
    @Override
    public String toString() {
        return String.format("%s - %s [%s] %d/%d places (%.1f%%)", 
            codeSession, formation.getTitre(), etat, 
            inscrits.size(), placesMax, getTauxRemplissage());
    }
}