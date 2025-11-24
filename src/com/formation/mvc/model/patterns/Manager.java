package com.formation.mvc.model.patterns;

import com.formation.mvc.model.metier.Formation;
import com.formation.mvc.model.metier.Session;
import com.formation.mvc.model.metier.Apprenant;
import com.formation.mvc.model.metier.Formateur;
import java.util.ArrayList;
import java.util.List;

public class Manager {
    private static Manager instance;
    private List<Formation> formations;
    private List<Session> sessions;
    private List<Apprenant> apprenants;
    private List<Formateur> formateurs;
    
    private Manager() {
        this.formations = new ArrayList<>();
        this.sessions = new ArrayList<>();
        this.apprenants = new ArrayList<>();
        this.formateurs = new ArrayList<>();
        System.out.println("Manager créé (Singleton)");
    }
    
    public static Manager getInstance() {
        if (instance == null) {
            instance = new Manager();
        }
        return instance;
    }
    
    // Méthodes Formations
    public void ajouterFormation(Formation formation) {
        formations.add(formation);
        System.out.println("✅ Formation ajoutée: " + formation.getTitre());
    }
    
    public void listerFormations() {
        System.out.println("\n📚 LISTE DES FORMATIONS:");
        if (formations.isEmpty()) {
            System.out.println("Aucune formation disponible");
        } else {
            for (int i = 0; i < formations.size(); i++) {
                System.out.println((i + 1) + ". " + formations.get(i));
            }
        }
    }
    
    public Formation getFormation(int index) {
        if (index >= 0 && index < formations.size()) {
            return formations.get(index);
        }
        return null;
    }
    
    // Méthodes Sessions
    public void ajouterSession(Session session) {
        sessions.add(session);
        System.out.println("✅ Session ajoutée: " + session.getCodeSession());
    }
    
    public void listerSessions() {
        System.out.println("\n📅 LISTE DES SESSIONS:");
        if (sessions.isEmpty()) {
            System.out.println("Aucune session disponible");
        } else {
            for (int i = 0; i < sessions.size(); i++) {
                System.out.println((i + 1) + ". " + sessions.get(i));
            }
        }
    }
    
    public Session getSession(int index) {
        if (index >= 0 && index < sessions.size()) {
            return sessions.get(index);
        }
        return null;
    }
    
    // Méthodes Apprenants
    public void ajouterApprenant(Apprenant apprenant) {
        apprenants.add(apprenant);
        System.out.println("✅ Apprenant ajouté: " + apprenant.getNom());
    }
    
    public void listerApprenants() {
        System.out.println("\n👥 LISTE DES APPRENANTS:");
        if (apprenants.isEmpty()) {
            System.out.println("Aucun apprenant inscrit");
        } else {
            for (int i = 0; i < apprenants.size(); i++) {
                System.out.println((i + 1) + ". " + apprenants.get(i));
            }
        }
    }
    
    public Apprenant getApprenant(int index) {
        if (index >= 0 && index < apprenants.size()) {
            return apprenants.get(index);
        }
        return null;
    }
    
    // Méthodes Formateurs
    public void ajouterFormateur(Formateur formateur) {
        formateurs.add(formateur);
        System.out.println("✅ Formateur ajouté: " + formateur.getNom());
    }
    
    public void listerFormateurs() {
        System.out.println("\n👨‍🏫 LISTE DES FORMATEURS:");
        if (formateurs.isEmpty()) {
            System.out.println("Aucun formateur disponible");
        } else {
            for (int i = 0; i < formateurs.size(); i++) {
                System.out.println((i + 1) + ". " + formateurs.get(i));
            }
        }
    }
    
    public Formateur getFormateur(int index) {
        if (index >= 0 && index < formateurs.size()) {
            return formateurs.get(index);
        }
        return null;
    }
    
    // Getters pour les listes
    public List<Formation> getFormations() { return formations; }
    public List<Session> getSessions() { return sessions; }
    public List<Apprenant> getApprenants() { return apprenants; }
    public List<Formateur> getFormateurs() { return formateurs; }
    
    // Statistiques
    public void afficherStatistiques() {
        System.out.println("\n📊 STATISTIQUES DU SYSTÈME:");
        System.out.println("Formations: " + formations.size());
        System.out.println("Sessions: " + sessions.size());
        System.out.println("Apprenants: " + apprenants.size());
        System.out.println("Formateurs: " + formateurs.size());
        
        int totalInscrits = 0;
        for (Session session : sessions) {
            totalInscrits += session.getInscrits().size();
        }
        System.out.println("Total inscriptions: " + totalInscrits);
    }
}