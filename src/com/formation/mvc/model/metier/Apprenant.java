package com.formation.mvc.model.metier;

import java.util.*;

public class Apprenant {
    private String nom;
    private String entreprise;
    private List<Session> sessionsInscrites;
    
    public Apprenant(String nom, String entreprise) {
        this.nom = nom;
        this.entreprise = entreprise;
        this.sessionsInscrites = new ArrayList<>();
    }
    
    // Getters
    public String getNom() { return nom; }
    public String getEntreprise() { return entreprise; }
    public List<Session> getSessionsInscrites() { return new ArrayList<>(sessionsInscrites); }
    
    // Méthodes d'inscription
    public void ajouterSession(Session session) {
        if (!sessionsInscrites.contains(session)) {
            sessionsInscrites.add(session);
        }
    }
    
    public void retirerSession(Session session) {
        sessionsInscrites.remove(session);
    }
    
    // Nouvelles méthodes statistiques
    public int getNombreSessionsInscrites() {
        return sessionsInscrites.size();
    }
    
    public List<Formation> getFormationsSuivies() {
        return sessionsInscrites.stream()
            .map(Session::getFormation)
            .distinct()
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    public boolean estInscritFormation(Formation formation) {
        return sessionsInscrites.stream()
            .anyMatch(s -> s.getFormation().equals(formation));
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Apprenant apprenant = (Apprenant) obj;
        return Objects.equals(nom, apprenant.nom) && 
               Objects.equals(entreprise, apprenant.entreprise);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(nom, entreprise);
    }
    
    @Override
    public String toString() {
        return String.format("%s - %s (%d sessions)", nom, entreprise, sessionsInscrites.size());
    }
}