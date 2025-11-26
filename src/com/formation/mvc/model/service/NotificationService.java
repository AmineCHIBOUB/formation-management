package com.formation.mvc.model.service;
import com.formation.mvc.model.metier.*;
import com.formation.mvc.model.patterns.Manager;
import com.formation.mvc.model.repository.*;
import java.util.*;
import java.util.stream.Collectors;

public class NotificationService {
    private SessionRepository sessionRepo;
    private Manager manager;
    
    public NotificationService() {
        this.sessionRepo = SessionRepository.getInstance();
        this.manager = Manager.getInstance();
    }
    
    // VÉRIFIER LES SESSIONS BIENTÔT PLEINES
    public List<Session> getSessionsBientotPleines() {
        return sessionRepo.trouverSessionsAvecPlaces().stream()
            .filter(session -> {
                double taux = session.getTauxRemplissage();
                return taux >= 80.0 && taux < 100.0;
            })
            .collect(Collectors.toList());
    }
    
    // VÉRIFIER LES SESSIONS AVEC PEU DE PLACES
    public List<Session> getSessionsAvecPeuDePlaces() {
        return sessionRepo.trouverSessionsAvecPlaces().stream()
            .filter(session -> session.getPlacesRestantes() <= 5)
            .collect(Collectors.toList());
    }
    
    // SESSIONS SANS INSCRITS
    public List<Session> getSessionsSansInscrits() {
        return sessionRepo.trouverToutes().stream()
            .filter(session -> session.getInscrits().isEmpty())
            .collect(Collectors.toList());
    }
    
    // GÉNÉRER RAPPORT D'ALERTES
    public Map<String, List<Session>> getRapportAlertes() {
        Map<String, List<Session>> alertes = new HashMap<>();
        
        alertes.put("sessions_bientot_pleines", getSessionsBientotPleines());
        alertes.put("sessions_peu_de_places", getSessionsAvecPeuDePlaces());
        alertes.put("sessions_sans_inscrits", getSessionsSansInscrits());
        
        return alertes;
    }
    
    // VÉRIFIER SI UNE SESSION PEUT ÊTRE OUVERTE
    public boolean peutOuvrirSession(Session session) {
        return session.getInscrits().size() >= 3; // Minimum 3 inscrits pour ouvrir
    }
    
    // SUGGÉRER DES ACTIONS
    public List<String> getSuggestions() {
        List<String> suggestions = new ArrayList<>();
        
        Map<String, List<Session>> alertes = getRapportAlertes();
        
        if (!alertes.get("sessions_bientot_pleines").isEmpty()) {
            suggestions.add("📢 Sessions bientôt pleines: envisager de créer de nouvelles sessions");
        }
        
        if (!alertes.get("sessions_sans_inscrits").isEmpty()) {
            suggestions.add("⚠️ Sessions sans inscrits: vérifier la communication");
        }
        
        if (sessionRepo.trouverSessionsPleine().size() > 5) {
            suggestions.add("🎯 Forte demande: développer l'offre de formation");
        }
        
        return suggestions;
    }
}