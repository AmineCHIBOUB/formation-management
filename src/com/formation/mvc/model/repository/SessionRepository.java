package com.formation.mvc.model.repository;

import com.formation.mvc.model.metier.Session;
import java.util.ArrayList;
import java.util.List;

public class SessionRepository {
    private List<Session> sessions;
    private static SessionRepository instance;
    
    private SessionRepository() {
        this.sessions = new ArrayList<>();
    }
    
    public static SessionRepository getInstance() {
        if (instance == null) {
            instance = new SessionRepository();
        }
        return instance;
    }
    
    public void sauvegarder(Session session) {
        sessions.add(session);
        System.out.println("💾 Session sauvegardée: " + session.getCodeSession());
    }
    
    public Session trouverParCode(String codeSession) {
        for (Session session : sessions) {
            if (session.getCodeSession().equals(codeSession)) {
                return session;
            }
        }
        return null;
    }
    
    public List<Session> trouverToutes() {
        return new ArrayList<>(sessions);
    }
    
    public List<Session> trouverSessionsOuvertes() {
        List<Session> sessionsOuvertes = new ArrayList<>();
        for (Session session : sessions) {
            if (session.getEtat().peutInscrire()) {
                sessionsOuvertes.add(session);
            }
        }
        return sessionsOuvertes;
    }
    
    public int compter() {
        return sessions.size();
    }
}