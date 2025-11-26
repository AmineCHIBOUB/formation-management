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
        formations = new ArrayList<>();
        sessions = new ArrayList<>();
        apprenants = new ArrayList<>();
        formateurs = new ArrayList<>();
    }
    
    public static Manager getInstance() {
        if (instance == null) {
            instance = new Manager();
        }
        return instance;
    }
    
    // Gestion des formations
    public void ajouterFormation(Formation formation) {
        formations.add(formation);
        System.out.println("✅ Formation ajoutée: " + formation.getTitre());
    }
    
    public List<Formation> getFormations() {
        return new ArrayList<>(formations);
    }
    
    // Gestion des sessions
    public void ajouterSession(Session session) {
        sessions.add(session);
        System.out.println("✅ Session ajoutée: " + session.getCodeSession());
    }
    
    public List<Session> getSessions() {
        return new ArrayList<>(sessions);
    }
    
    // Gestion des apprenants
    public void ajouterApprenant(Apprenant apprenant) {
        apprenants.add(apprenant);
        System.out.println("✅ Apprenant ajouté: " + apprenant.getNom());
    }
    
    public List<Apprenant> getApprenants() {
        return new ArrayList<>(apprenants);
    }
    
    // Gestion des formateurs
    public void ajouterFormateur(Formateur formateur) {
        formateurs.add(formateur);
        System.out.println("✅ Formateur ajouté: " + formateur.getNom());
    }
    
    public List<Formateur> getFormateurs() {
        return new ArrayList<>(formateurs);
    }
}