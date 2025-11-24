package com.formation.mvc.model.patterns;

import com.formation.mvc.model.metier.Formation;

public class FactoryFormation {
    
    public Formation creerFormation(String type, String code, String titre, int duree) {
        Formation formation = new Formation(code, titre, duree);
        
        switch (type.toUpperCase()) {
            case "JAVA":
                formation.ajouterOption("Programmation Orientée Objet");
                formation.ajouterOption("Spring Framework");
                break;
            case "WEB":
                formation.ajouterOption("HTML/CSS");
                formation.ajouterOption("JavaScript");
                formation.ajouterOption("React");
                break;
            case "DATA":
                formation.ajouterOption("SQL");
                formation.ajouterOption("Analyse de données");
                break;
            default:
                formation.ajouterOption("Formation générale");
        }
        
        System.out.println("🏭 Formation " + type + " créée avec options");
        return formation;
    }
    
    public Formation creerSessionPresentielle(String code, String titre, int duree) {
        Formation formation = new Formation(code, titre, duree);
        formation.ajouterOption("Session présentielle");
        formation.ajouterOption("Support physique");
        System.out.println("🏭 Session présentielle créée");
        return formation;
    }
}