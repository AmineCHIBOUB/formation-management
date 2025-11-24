package com.formation.mvc.view;

import com.formation.mvc.controller.FormationController;
import com.formation.mvc.model.metier.Formation;
import java.util.List;
import java.util.Scanner;

public class FormationView {
    private FormationController formationController;
    private Scanner scanner;
    
    public FormationView(FormationController formationController) {
        this.formationController = formationController;
        this.scanner = new Scanner(System.in);
    }
    
    public void afficherMenuFormations() {
        System.out.println("\n📚 GESTION DES FORMATIONS");
        System.out.println("1. Créer une formation");
        System.out.println("2. Créer une formation avec Factory");
        System.out.println("3. Lister les formations");
        System.out.println("4. Ajouter une option");
        System.out.println("5. Retour au menu principal");
        System.out.print("👉 Votre choix : ");
        
        int choix = scanner.nextInt();
        scanner.nextLine();
        
        switch (choix) {
            case 1:
                creerFormation();
                break;
            case 2:
                creerFormationFactory();
                break;
            case 3:
                listerFormations();
                break;
            case 4:
                ajouterOption();
                break;
            case 5:
                return;
            default:
                System.out.println("❌ Choix invalide");
        }
        afficherMenuFormations();
    }
    
    private void creerFormation() {
        System.out.println("\n➕ CRÉATION D'UNE FORMATION");
        System.out.print("Code de la formation: ");
        String code = scanner.nextLine();
        
        System.out.print("Titre de la formation: ");
        String titre = scanner.nextLine();
        
        System.out.print("Durée en heures: ");
        int duree = scanner.nextInt();
        scanner.nextLine();
        
        formationController.creerFormation(code, titre, duree);
    }
    
    private void creerFormationFactory() {
        System.out.println("\n🏭 CRÉATION AVEC FACTORY");
        System.out.print("Type de formation (JAVA/WEB/DATA): ");
        String type = scanner.nextLine();
        
        System.out.print("Code de la formation: ");
        String code = scanner.nextLine();
        
        System.out.print("Titre de la formation: ");
        String titre = scanner.nextLine();
        
        System.out.print("Durée en heures: ");
        int duree = scanner.nextInt();
        scanner.nextLine();
        
        formationController.creerFormationAvecFactory(type, code, titre, duree);
    }
    
    private void listerFormations() {
        List<Formation> formations = formationController.listerFormations();
        System.out.println("\n📚 LISTE DES FORMATIONS (" + formations.size() + "):");
        if (formations.isEmpty()) {
            System.out.println("Aucune formation disponible");
        } else {
            for (int i = 0; i < formations.size(); i++) {
                System.out.println((i + 1) + ". " + formations.get(i));
                List<String> options = formations.get(i).getOptions();
                if (!options.isEmpty()) {
                    System.out.println("   Options: " + String.join(", ", options));
                }
            }
        }
    }
    
    private void ajouterOption() {
        listerFormations();
        if (formationController.getNombreFormations() == 0) return;
        
        System.out.print("Code de la formation: ");
        String code = scanner.nextLine();
        
        System.out.print("Option à ajouter: ");
        String option = scanner.nextLine();
        
        formationController.ajouterOption(code, option);
    }
}