package com.formation.mvc;

import com.formation.mvc.model.metier.Formation;
import com.formation.mvc.model.metier.Session;
import com.formation.mvc.model.metier.Apprenant;
import com.formation.mvc.model.metier.Formateur;
import com.formation.mvc.model.patterns.Manager;
import com.formation.mvc.model.patterns.FactoryFormation;
import com.formation.mvc.model.repository.FormationRepository;
import com.formation.mvc.model.repository.SessionRepository;
import com.formation.mvc.model.service.FormationService;
import com.formation.mvc.model.service.SessionService;
import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static Manager manager = Manager.getInstance();
    private static FactoryFormation factory = new FactoryFormation();
    private static FormationRepository formationRepo = FormationRepository.getInstance();
    private static SessionRepository sessionRepo = SessionRepository.getInstance();
    private static FormationService formationService = new FormationService(formationRepo);
    private static SessionService sessionService = new SessionService(sessionRepo);
    
    public static void main(String[] args) {
        System.out.println("🚀 Système de Gestion des Formations MVC - Démarrage");
        System.out.println("===================================================");
        
        // CHARGEMENT DES DONNÉES
        chargerDonnees();
        
        // MENU PRINCIPAL
        afficherMenuPrincipal();
        
        // SAUVEGARDE DES DONNÉES
        sauvegarderDonnees();
        
        System.out.println("👋 Fermeture du système de formation MVC");
    }
    
    public static void chargerDonnees() {
        System.out.println("📂 Chargement des données...");
        formationService.chargerFormations();
        sessionService.chargerSessions();
        System.out.println("✅ Données chargées avec succès");
    }
    
    public static void sauvegarderDonnees() {
        System.out.println("💾 Sauvegarde des données...");
        formationService.sauvegarderFormations();
        sessionService.sauvegarderSessions();
        System.out.println("✅ Données sauvegardées avec succès");
    }
    
    public static void afficherMenuPrincipal() {
        boolean running = true;
        while (running) {
            System.out.println("\n🏠 MENU PRINCIPAL MVC AVEC PERSISTANCE");
            System.out.println("1. Gérer les formations");
            System.out.println("2. Gérer les sessions");
            System.out.println("3. Gérer les apprenants");
            System.out.println("4. Gérer les formateurs");
            System.out.println("5. Inscrire un apprenant à une session");
            System.out.println("6. Créer formation avec Factory");
            System.out.println("7. Statistiques");
            System.out.println("8. Sauvegarder maintenant");
            System.out.println("9. Quitter");
            System.out.print("👉 Votre choix : ");
            
            int choix = scanner.nextInt();
            scanner.nextLine();
            
            switch (choix) {
                case 1:
                    gererFormations();
                    break;
                case 2:
                    gererSessions();
                    break;
                case 3:
                    gererApprenants();
                    break;
                case 4:
                    gererFormateurs();
                    break;
                case 5:
                    inscrireApprenantSession();
                    break;
                case 6:
                    creerFormationFactory();
                    break;
                case 7:
                    afficherStatistiques();
                    break;
                case 8:
                    sauvegarderDonnees();
                    break;
                case 9:
                    running = false;
                    break;
                default:
                    System.out.println("❌ Choix invalide");
            }
        }
    }
    
    public static void gererFormations() {
        System.out.println("\n📚 GESTION DES FORMATIONS");
        System.out.println("1. Créer une formation");
        System.out.println("2. Lister les formations");
        System.out.println("3. Ajouter une option");
        System.out.print("👉 Votre choix : ");
        
        int choix = scanner.nextInt();
        scanner.nextLine();
        
        switch (choix) {
            case 1:
                creerFormation();
                break;
            case 2:
                listerFormations();
                break;
            case 3:
                ajouterOptionFormation();
                break;
            default:
                System.out.println("❌ Choix invalide");
        }
    }
    
    public static void gererSessions() {
        System.out.println("\n📅 GESTION DES SESSIONS");
        System.out.println("1. Créer une session");
        System.out.println("2. Lister les sessions");
        System.out.println("3. Voir les inscrits d'une session");
        System.out.print("👉 Votre choix : ");
        
        int choix = scanner.nextInt();
        scanner.nextLine();
        
        switch (choix) {
            case 1:
                creerSession();
                break;
            case 2:
                listerSessions();
                break;
            case 3:
                listerInscritsSession();
                break;
            default:
                System.out.println("❌ Choix invalide");
        }
    }
    
    public static void gererApprenants() {
        System.out.println("\n👥 GESTION DES APPRENANTS");
        System.out.println("1. Ajouter un apprenant");
        System.out.println("2. Lister les apprenants");
        System.out.print("👉 Votre choix : ");
        
        int choix = scanner.nextInt();
        scanner.nextLine();
        
        switch (choix) {
            case 1:
                creerApprenant();
                break;
            case 2:
                manager.listerApprenants();
                break;
            default:
                System.out.println("❌ Choix invalide");
        }
    }
    
    public static void gererFormateurs() {
        System.out.println("\n👨‍🏫 GESTION DES FORMATEURS");
        System.out.println("1. Ajouter un formateur");
        System.out.println("2. Lister les formateurs");
        System.out.print("👉 Votre choix : ");
        
        int choix = scanner.nextInt();
        scanner.nextLine();
        
        switch (choix) {
            case 1:
                creerFormateur();
                break;
            case 2:
                manager.listerFormateurs();
                break;
            default:
                System.out.println("❌ Choix invalide");
        }
    }
    
    public static void creerFormation() {
        System.out.println("\n➕ CRÉATION D'UNE FORMATION");
        System.out.print("Code de la formation: ");
        String code = scanner.nextLine();
        
        System.out.print("Titre de la formation: ");
        String titre = scanner.nextLine();
        
        System.out.print("Durée en heures: ");
        int duree = scanner.nextInt();
        scanner.nextLine();
        
        Formation formation = new Formation(code, titre, duree);
        formationRepo.sauvegarder(formation);
        manager.ajouterFormation(formation);
    }
    
    public static void creerFormationFactory() {
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
        
        Formation formation = factory.creerFormation(type, code, titre, duree);
        formationRepo.sauvegarder(formation);
        manager.ajouterFormation(formation);
    }
    
    public static void creerSession() {
        listerFormations();
        if (formationRepo.compter() == 0) {
            System.out.println("❌ Créez d'abord une formation");
            return;
        }
        
        System.out.println("\n➕ CRÉATION D'UNE SESSION");
        System.out.print("Code de la session: ");
        String codeSession = scanner.nextLine();
        
        System.out.print("Code de la formation: ");
        String codeFormation = scanner.nextLine();
        
        Formation formation = formationRepo.trouverParCode(codeFormation);
        if (formation == null) {
            System.out.println("❌ Formation non trouvée");
            return;
        }
        
        System.out.print("Nombre de places max: ");
        int placesMax = scanner.nextInt();
        scanner.nextLine();
        
        Session session = new Session(codeSession, formation, placesMax);
        sessionRepo.sauvegarder(session);
        manager.ajouterSession(session);
    }
    
    public static void creerApprenant() {
        System.out.println("\n➕ AJOUT D'UN APPRENANT");
        System.out.print("Nom de l'apprenant: ");
        String nom = scanner.nextLine();
        
        System.out.print("Entreprise: ");
        String entreprise = scanner.nextLine();
        
        Apprenant apprenant = new Apprenant(nom, entreprise);
        manager.ajouterApprenant(apprenant);
    }
    
    public static void creerFormateur() {
        System.out.println("\n➕ AJOUT D'UN FORMATEUR");
        System.out.print("Nom du formateur: ");
        String nom = scanner.nextLine();
        
        System.out.print("Spécialité: ");
        String specialite = scanner.nextLine();
        
        Formateur formateur = new Formateur(nom, specialite);
        manager.ajouterFormateur(formateur);
    }
    
    public static void listerFormations() {
        var formations = formationRepo.trouverToutes();
        System.out.println("\n📚 LISTE DES FORMATIONS:");
        if (formations.isEmpty()) {
            System.out.println("Aucune formation disponible");
        } else {
            for (int i = 0; i < formations.size(); i++) {
                System.out.println((i + 1) + ". " + formations.get(i));
            }
        }
    }
    
    public static void listerSessions() {
        var sessions = sessionRepo.trouverToutes();
        System.out.println("\n📅 LISTE DES SESSIONS:");
        if (sessions.isEmpty()) {
            System.out.println("Aucune session disponible");
        } else {
            for (int i = 0; i < sessions.size(); i++) {
                System.out.println((i + 1) + ". " + sessions.get(i));
            }
        }
    }
    
    public static void inscrireApprenantSession() {
        manager.listerApprenants();
        if (manager.getApprenants().isEmpty()) {
            System.out.println("❌ Aucun apprenant disponible");
            return;
        }
        
        listerSessions();
        if (sessionRepo.compter() == 0) {
            System.out.println("❌ Aucune session disponible");
            return;
        }
        
        System.out.print("Numéro de l'apprenant: ");
        int indexApprenant = scanner.nextInt() - 1;
        
        System.out.print("Code de la session: ");
        scanner.nextLine();
        String codeSession = scanner.nextLine();
        
        if (indexApprenant >= 0 && indexApprenant < manager.getApprenants().size()) {
            Apprenant apprenant = manager.getApprenants().get(indexApprenant);
            Session session = sessionRepo.trouverParCode(codeSession);
            
            if (session != null) {
                session.inscrire(apprenant);
            } else {
                System.out.println("❌ Session non trouvée");
            }
        } else {
            System.out.println("❌ Numéro d'apprenant invalide");
        }
    }
    
    public static void ajouterOptionFormation() {
        listerFormations();
        if (formationRepo.compter() == 0) return;
        
        System.out.print("Code de la formation: ");
        String code = scanner.nextLine();
        
        Formation formation = formationRepo.trouverParCode(code);
        if (formation != null) {
            System.out.print("Option à ajouter: ");
            String option = scanner.nextLine();
            formation.ajouterOption(option);
        } else {
            System.out.println("❌ Formation non trouvée");
        }
    }
    
    public static void listerInscritsSession() {
        listerSessions();
        if (sessionRepo.compter() == 0) return;
        
        System.out.print("Code de la session: ");
        String codeSession = scanner.nextLine();
        
        Session session = sessionRepo.trouverParCode(codeSession);
        if (session != null) {
            session.listerInscrits();
        } else {
            System.out.println("❌ Session non trouvée");
        }
    }
    
    public static void afficherStatistiques() {
        System.out.println("\n📊 STATISTIQUES MVC:");
        System.out.println("Formations: " + formationRepo.compter());
        System.out.println("Sessions: " + sessionRepo.compter());
        System.out.println("Apprenants: " + manager.getApprenants().size());
        System.out.println("Formateurs: " + manager.getFormateurs().size());
        
        var sessionsOuvertes = sessionRepo.trouverSessionsOuvertes();
        System.out.println("Sessions ouvertes: " + sessionsOuvertes.size());
    }
}