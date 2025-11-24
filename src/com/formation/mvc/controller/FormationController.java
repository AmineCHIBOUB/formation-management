package com.formation.mvc.controller;

import com.formation.mvc.model.metier.Formation;
import com.formation.mvc.model.repository.FormationRepository;
import com.formation.mvc.model.patterns.FactoryFormation;
import java.util.List;

public class FormationController {
    private FormationRepository formationRepository;
    private FactoryFormation factory;
    
    public FormationController() {
        this.formationRepository = FormationRepository.getInstance();
        this.factory = new FactoryFormation();
    }
    
    public Formation creerFormation(String code, String titre, int duree) {
        Formation formation = new Formation(code, titre, duree);
        formationRepository.sauvegarder(formation);
        return formation;
    }
    
    public Formation creerFormationAvecFactory(String type, String code, String titre, int duree) {
        Formation formation = factory.creerFormation(type, code, titre, duree);
        formationRepository.sauvegarder(formation);
        return formation;
    }
    
    public List<Formation> listerFormations() {
        return formationRepository.trouverToutes();
    }
    
    public Formation trouverFormationParCode(String code) {
        return formationRepository.trouverParCode(code);
    }
    
    public void ajouterOption(String codeFormation, String option) {
        Formation formation = formationRepository.trouverParCode(codeFormation);
        if (formation != null) {
            formation.ajouterOption(option);
            System.out.println("✅ Option ajoutée à " + formation.getTitre());
        } else {
            System.out.println("❌ Formation non trouvée");
        }
    }
    
    public int getNombreFormations() {
        return formationRepository.compter();
    }
}