package com.formation.mvc.controller;

import com.formation.mvc.model.metier.Session;
import com.formation.mvc.model.metier.Formation;
import com.formation.mvc.model.metier.Apprenant;
import com.formation.mvc.model.repository.SessionRepository;
import com.formation.mvc.model.repository.FormationRepository;
import java.util.List;

public class SessionController {
    private SessionRepository sessionRepository;
    private FormationRepository formationRepository;
    
    public SessionController() {
        this.sessionRepository = SessionRepository.getInstance();
        this.formationRepository = FormationRepository.getInstance();
    }
    
    public Session creerSession(String codeSession, String codeFormation, int placesMax) {
        Formation formation = formationRepository.trouverParCode(codeFormation);
        if (formation != null) {
            Session session = new Session(codeSession, formation, placesMax);
            sessionRepository.sauvegarder(session);
            return session;
        } else {
            System.out.println("❌ Formation non trouvée: " + codeFormation);
            return null;
        }
    }
    
    public List<Session> listerSessions() {
        return sessionRepository.trouverToutes();
    }
    
    public List<Session> listerSessionsOuvertes() {
        return sessionRepository.trouverSessionsOuvertes();
    }
    
    public boolean inscrireApprenant(String codeSession, Apprenant apprenant) {
        Session session = sessionRepository.trouverParCode(codeSession);
        if (session != null && session.getEtat().peutInscrire()) {
            session.inscrire(apprenant);
            return true;
        }
        return false;
    }
    
    public Session trouverSessionParCode(String codeSession) {
        return sessionRepository.trouverParCode(codeSession);
    }
    
    public int getNombreSessions() {
        return sessionRepository.compter();
    }
}