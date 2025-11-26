package com.formation.mvc.model.service;

import com.formation.mvc.model.metier.*;
import com.formation.mvc.model.repository.*;
import com.formation.mvc.model.patterns.Manager;
import java.util.*;
import java.util.stream.Collectors;

public class StatistiquesService {
    private FormationRepository formationRepo;
    private SessionRepository sessionRepo;
    private Manager manager;
    
    public StatistiquesService() {
        this.formationRepo = FormationRepository.getInstance();
        this.sessionRepo = SessionRepository.getInstance();
        this.manager = Manager.getInstance();
    }
    
    // STATISTIQUES GLOBALES
    public Map<String, Object> getStatistiquesGlobales() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("total_formations", formationRepo.compter());
        stats.put("total_sessions", sessionRepo.compter());
        stats.put("total_apprenants", manager.getApprenants().size());
        stats.put("total_formateurs", manager.getFormateurs().size());
        
        // Sessions par état
        stats.put("sessions_ouvertes", sessionRepo.trouverSessionsOuvertes().size());
        stats.put("sessions_pleines", sessionRepo.trouverSessionsPleine().size());
        stats.put("sessions_en_cours", 
            sessionRepo.filtrerParEtat(com.formation.mvc.model.metier.Etat.EN_COURS).size());
        stats.put("sessions_terminees", 
            sessionRepo.filtrerParEtat(com.formation.mvc.model.metier.Etat.TERMINEE).size());
        
        // Taux de remplissage
        stats.put("taux_remplissage_moyen", sessionRepo.tauxRemplissageMoyen());
        
        return stats;
    }
    
    // STATISTIQUES PAR FORMATION
    public List<Map<String, Object>> getStatistiquesParFormation() {
        return formationRepo.trouverToutes().stream()
            .map(formation -> {
                Map<String, Object> statsFormation = new HashMap<>();
                List<Session> sessionsFormation = sessionRepo.trouverParFormation(formation);
                
                statsFormation.put("formation", formation);
                statsFormation.put("nombre_sessions", sessionsFormation.size());
                statsFormation.put("total_inscrits", 
                    sessionsFormation.stream().mapToInt(s -> s.getInscrits().size()).sum());
                statsFormation.put("taux_remplissage_moyen",
                    sessionsFormation.stream()
                        .mapToDouble(Session::getTauxRemplissage)
                        .average()
                        .orElse(0.0));
                statsFormation.put("sessions_ouvertes",
                    sessionsFormation.stream()
                        .filter(s -> s.getEtat() == Etat.OUVERTE)
                        .count());
                
                return statsFormation;
            })
            .collect(Collectors.toList());
    }
    
    // TOP FORMATIONS
    public List<Map<String, Object>> getTopFormations() {
        return formationRepo.trouverToutes().stream()
            .map(formation -> {
                Map<String, Object> stats = new HashMap<>();
                List<Session> sessions = sessionRepo.trouverParFormation(formation);
                int totalInscrits = sessions.stream().mapToInt(s -> s.getInscrits().size()).sum();
                
                stats.put("formation", formation);
                stats.put("total_inscrits", totalInscrits);
                stats.put("nombre_sessions", sessions.size());
                stats.put("popularite", totalInscrits);
                
                return stats;
            })
            .sorted((s1, s2) -> Integer.compare(
                (Integer) s2.get("popularite"), 
                (Integer) s1.get("popularite")))
            .limit(5)
            .collect(Collectors.toList());
    }
    
    // STATISTIQUES TEMPORELLES
    public Map<String, Object> getStatistiquesTemporelles() {
        Map<String, Object> stats = new HashMap<>();
        
        // Répartition par entreprise
        Map<String, Long> repartitionEntreprise = manager.getApprenants().stream()
            .collect(Collectors.groupingBy(Apprenant::getEntreprise, Collectors.counting()));
        stats.put("repartition_entreprise", repartitionEntreprise);
        
        // Répartition par type de formation (simulé)
        Map<String, Long> repartitionType = formationRepo.trouverToutes().stream()
            .collect(Collectors.groupingBy(
                f -> f.getTitre().contains("JAVA") ? "JAVA" : 
                      f.getTitre().contains("WEB") ? "WEB" : 
                      f.getTitre().contains("DATA") ? "DATA" : "AUTRE",
                Collectors.counting()
            ));
        stats.put("repartition_type", repartitionType);
        
        return stats;
    }
    
    // RAPPORT DÉTAILLÉ
    public String genererRapportDetaille() {
        StringBuilder rapport = new StringBuilder();
        rapport.append("RAPPORT DÉTAILLÉ DU SYSTÈME DE FORMATION\n");
        rapport.append("========================================\n\n");
        
        // Statistiques globales
        Map<String, Object> globales = getStatistiquesGlobales();
        rapport.append("STATISTIQUES GLOBALES:\n");
        rapport.append(String.format("- Formations: %d\n", globales.get("total_formations")));
        rapport.append(String.format("- Sessions: %d\n", globales.get("total_sessions")));
        rapport.append(String.format("- Apprenants: %d\n", globales.get("total_apprenants")));
        rapport.append(String.format("- Formateurs: %d\n", globales.get("total_formateurs")));
        rapport.append(String.format("- Taux de remplissage moyen: %.1f%%\n\n", globales.get("taux_remplissage_moyen")));
        
        // Détails par formation
        rapport.append("DÉTAILS PAR FORMATION:\n");
        getStatistiquesParFormation().forEach(stats -> {
            Formation formation = (Formation) stats.get("formation");
            rapport.append(String.format("- %s: %d sessions, %d inscrits, %.1f%% remplissage\n",
                formation.getTitre(), stats.get("nombre_sessions"), 
                stats.get("total_inscrits"), stats.get("taux_remplissage_moyen")));
        });
        
        return rapport.toString();
    }
}