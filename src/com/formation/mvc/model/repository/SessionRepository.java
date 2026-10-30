package com.formation.mvc.model.repository;

import com.formation.mvc.model.metier.Session;
import com.formation.mvc.model.metier.Formation;
import com.formation.mvc.model.metier.Etat;
import com.formation.mvc.model.service.JournalActivite;
import java.util.*;
import java.util.stream.Collectors;

public class SessionRepository {
    private static SessionRepository instance;
    private List<Session> sessions;
    
    private SessionRepository() {
        sessions = new ArrayList<>();
    }
    
    public static SessionRepository getInstance() {
        if (instance == null) {
            instance = new SessionRepository();
        }
        return instance;
    }
    
    public void sauvegarder(Session session) {
        sessions.removeIf(s -> s.getCodeSession().equals(session.getCodeSession()));
        sessions.add(session);
        // chaque session enregistrée est observée par le journal d'activité
        session.addObserver(JournalActivite.getInstance());
    }
    
    // Méthode pour supprimer une session
    public boolean supprimer(String codeSession) {
        return sessions.removeIf(s -> s.getCodeSession().equals(codeSession));
    }
    
    public Session trouverParCode(String codeSession) {
        return sessions.stream()
            .filter(s -> s.getCodeSession().equals(codeSession))
            .findFirst()
            .orElse(null);
    }
    
    public List<Session> trouverToutes() {
        return new ArrayList<>(sessions);
    }
    
    public int compter() {
        return sessions.size();
    }
    
    public List<Session> trouverParFormation(Formation formation) {
        return sessions.stream()
            .filter(s -> s.getFormation().equals(formation))
            .collect(Collectors.toList());
    }
    
    public List<Session> trouverSessionsOuvertes() {
        return sessions.stream()
            .filter(s -> s.getEtat() == Etat.OUVERTE)
            .collect(Collectors.toList());
    }
    
    // NOUVELLES MÉTHODES AVANCÉES
    public List<Session> rechercherParFormation(String nomFormation) {
        return sessions.stream()
            .filter(s -> s.getFormation().getTitre().toLowerCase().contains(nomFormation.toLowerCase()))
            .collect(Collectors.toList());
    }
    
    public List<Session> filtrerParEtat(Etat etat) {
        return sessions.stream()
            .filter(s -> s.getEtat() == etat)
            .collect(Collectors.toList());
    }
    
    public List<Session> trouverSessionsPleine() {
        return sessions.stream()
            .filter(s -> s.getInscrits().size() >= s.getPlacesMax())
            .collect(Collectors.toList());
    }
    
    public List<Session> trouverSessionsAvecPlaces() {
        return sessions.stream()
            .filter(s -> s.getInscrits().size() < s.getPlacesMax())
            .collect(Collectors.toList());
    }
    
    public Map<Formation, Long> compterSessionsParFormation() {
        return sessions.stream()
            .collect(Collectors.groupingBy(Session::getFormation, Collectors.counting()));
    }
    
    public double tauxRemplissageMoyen() {
        return sessions.stream()
            .mapToDouble(s -> (double) s.getInscrits().size() / s.getPlacesMax() * 100)
            .average()
            .orElse(0.0);
    }
}