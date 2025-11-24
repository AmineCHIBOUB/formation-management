package com.formation.mvc.model.repository;

import com.formation.mvc.model.metier.Formation;
import java.util.ArrayList;
import java.util.List;

public class FormationRepository {
    private List<Formation> formations;
    private static FormationRepository instance;
    
    private FormationRepository() {
        this.formations = new ArrayList<>();
    }
    
    public static FormationRepository getInstance() {
        if (instance == null) {
            instance = new FormationRepository();
        }
        return instance;
    }
    
    public void sauvegarder(Formation formation) {
        formations.add(formation);
        System.out.println("💾 Formation sauvegardée: " + formation.getTitre());
    }
    
    public Formation trouverParCode(String code) {
        for (Formation formation : formations) {
            if (formation.getCode().equals(code)) {
                return formation;
            }
        }
        return null;
    }
    
    public List<Formation> trouverToutes() {
        return new ArrayList<>(formations);
    }
    
    public int compter() {
        return formations.size();
    }
    
    public void supprimer(Formation formation) {
        formations.remove(formation);
        System.out.println("🗑️ Formation supprimée: " + formation.getTitre());
    }
}