package com.formation.mvc.controller;

import com.formation.mvc.model.metier.Apprenant;
import com.formation.mvc.model.metier.Session;
import com.formation.mvc.model.repository.SessionRepository;
import java.util.List;

public class InscriptionController {
    private SessionRepository sessionRepository;
    
    public InscriptionController() {
        this.sessionRepository = SessionRepository.getInstance();
    }
    
    public boolean verifierEligibilite(Apprenant apprenant, String codeSession) {
        Session session = sessionRepository.trouverParCode(codeSession);
        return session != null && session.getEtat().peutInscrire();
    }
    
    public String inscrireApprenantSession(Apprenant apprenant, String codeSession) {
        Session session = sessionRepository.trouverParCode(codeSession);
        if (session == null) {
            return "❌ Session non trouvée";
        }
        
        if (!session.getEtat().peutInscrire()) {
            return "❌ Session " + session.getEtat().getNom() + " - inscriptions fermées";
        }
        
        if (session.getInscrits().size() >= session.getPlacesMax()) {
            return "❌ Session complète";
        }
        
        session.inscrire(apprenant);
        return "✅ Inscription réussie de " + apprenant.getNom();
    }
    
    public List<Session> getSessionsDisponibles() {
        return sessionRepository.trouverSessionsOuvertes();
    }
}