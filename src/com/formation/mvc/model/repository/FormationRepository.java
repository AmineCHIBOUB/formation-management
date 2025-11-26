package com.formation.mvc.model.repository;

import com.formation.mvc.model.metier.Formation;
import java.util.*;
import java.util.stream.Collectors;

public class FormationRepository {
    private static FormationRepository instance;
    private List<Formation> formations;
    
    private FormationRepository() {
        formations = new ArrayList<>();
    }
    
    public static FormationRepository getInstance() {
        if (instance == null) {
            instance = new FormationRepository();
        }
        return instance;
    }
    
    public void sauvegarder(Formation formation) {
        formations.removeIf(f -> f.getCode().equals(formation.getCode()));
        formations.add(formation);
    }
    
    // Méthode pour supprimer une formation
    public boolean supprimer(String code) {
        return formations.removeIf(f -> f.getCode().equals(code));
    }
    
    public Formation trouverParCode(String code) {
        return formations.stream()
            .filter(f -> f.getCode().equals(code))
            .findFirst()
            .orElse(null);
    }
    
    public List<Formation> trouverToutes() {
        return new ArrayList<>(formations);
    }
    
    public int compter() {
        return formations.size();
    }
    
    // NOUVELLES MÉTHODES DE RECHERCHE AVANCÉE
    public List<Formation> rechercherParTitre(String titre) {
        return formations.stream()
            .filter(f -> f.getTitre().toLowerCase().contains(titre.toLowerCase()))
            .collect(Collectors.toList());
    }
    
    public List<Formation> rechercherParOption(String option) {
        return formations.stream()
            .filter(f -> f.getOptions().stream()
                .anyMatch(opt -> opt.toLowerCase().contains(option.toLowerCase())))
            .collect(Collectors.toList());
    }
    
    public List<Formation> filtrerParDuree(int dureeMin, int dureeMax) {
        return formations.stream()
            .filter(f -> f.getDureeHeures() >= dureeMin && f.getDureeHeures() <= dureeMax)
            .collect(Collectors.toList());
    }
    
    public List<Formation> rechercherTexte(String texte) {
        if (texte == null || texte.trim().isEmpty()) {
            return trouverToutes();
        }
        
        String recherche = texte.toLowerCase();
        return formations.stream()
            .filter(f -> 
                f.getCode().toLowerCase().contains(recherche) ||
                f.getTitre().toLowerCase().contains(recherche) ||
                f.getOptions().stream().anyMatch(opt -> opt.toLowerCase().contains(recherche))
            )
            .collect(Collectors.toList());
    }
}