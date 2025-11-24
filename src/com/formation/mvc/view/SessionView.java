package com.formation.mvc.view;

import com.formation.mvc.controller.SessionController;
import com.formation.mvc.model.metier.Session;
import java.util.List;
import java.util.Scanner;

public class SessionView {
    private SessionController sessionController;
    private Scanner scanner;
    
    public SessionView(SessionController sessionController) {
        this.sessionController = sessionController;
        this.scanner = new Scanner(System.in);
    }
    
    public void afficherMenuSessions() {
        System.out.println("\n📅 GESTION DES SESSIONS");
        System.out.println("1. Créer une session");
        System.out.println("2. Lister toutes les sessions");
        System.out.println("3. Lister sessions ouvertes");
        System.out.println("4. Voir les inscrits d'une session");
        System.out.println("5. Retour au menu principal");
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
                listerSessionsOuvertes();
                break;
            case 4:
                listerInscritsSession();
                break;
            case 5:
                return;
            default:
                System.out.println("❌ Choix invalide");
        }
        afficherMenuSessions();
    }
    
    private void creerSession() {
        System.out.println("\n➕ CRÉATION D'UNE SESSION");
        System.out.print("Code de la session: ");
        String codeSession = scanner.nextLine();
        
        System.out.print("Code de la formation: ");
        String codeFormation = scanner.nextLine();
        
        System.out.print("Nombre de places max: ");
        int placesMax = scanner.nextInt();
        scanner.nextLine();
        
        Session session = sessionController.creerSession(codeSession, codeFormation, placesMax);
        if (session != null) {
            System.out.println("✅ Session créée: " + session.getCodeSession());
        }
    }
    
    private void listerSessions() {
        List<Session> sessions = sessionController.listerSessions();
        System.out.println("\n📅 LISTE DES SESSIONS (" + sessions.size() + "):");
        if (sessions.isEmpty()) {
            System.out.println("Aucune session disponible");
        } else {
            for (int i = 0; i < sessions.size(); i++) {
                Session session = sessions.get(i);
                System.out.println((i + 1) + ". " + session);
            }
        }
    }
    
    private void listerSessionsOuvertes() {
        List<Session> sessions = sessionController.listerSessionsOuvertes();
        System.out.println("\n✅ SESSIONS OUVERTES (" + sessions.size() + "):");
        if (sessions.isEmpty()) {
            System.out.println("Aucune session ouverte");
        } else {
            for (int i = 0; i < sessions.size(); i++) {
                Session session = sessions.get(i);
                System.out.println((i + 1) + ". " + session);
            }
        }
    }
    
    private void listerInscritsSession() {
        listerSessions();
        if (sessionController.getNombreSessions() == 0) return;
        
        System.out.print("Code de la session: ");
        String codeSession = scanner.nextLine();
        
        Session session = sessionController.trouverSessionParCode(codeSession);
        if (session != null) {
            session.listerInscrits();
        } else {
            System.out.println("❌ Session non trouvée");
        }
    }
}