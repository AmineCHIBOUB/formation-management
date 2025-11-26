package com.formation.mvc.model.service;

import com.formation.mvc.model.metier.*;
import com.formation.mvc.model.repository.*;
import com.formation.mvc.model.patterns.Manager;
import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.time.format.*;
import java.util.*;

public class ExportService {
    private FormationRepository formationRepo;
    private SessionRepository sessionRepo;
    private Manager manager;
    private StatistiquesService statsService;
    
    public ExportService() {
        this.formationRepo = FormationRepository.getInstance();
        this.sessionRepo = SessionRepository.getInstance();
        this.manager = Manager.getInstance();
        this.statsService = new StatistiquesService();
    }
    
    // EXPORT FORMATIONS
    public boolean exporterFormations(String cheminFichier) {
        try {
            StringBuilder contenu = new StringBuilder();
            contenu.append("CATALOGUE DES FORMATIONS\n");
            contenu.append("========================\n\n");
            
            formationRepo.trouverToutes().forEach(formation -> {
                contenu.append(String.format("FORMATION: %s\n", formation.getTitre().toUpperCase()));
                contenu.append(String.format("Code: %s\n", formation.getCode()));
                contenu.append(String.format("Durée: %d heures\n", formation.getDureeHeures()));
                contenu.append(String.format("Options: %s\n", String.join(", ", formation.getOptions())));
                
                List<Session> sessions = sessionRepo.trouverParFormation(formation);
                contenu.append(String.format("Sessions associées: %d\n", sessions.size()));
                
                sessions.forEach(session -> {
                    contenu.append(String.format("  - %s: %s, %d/%d places (%.1f%%)\n",
                        session.getCodeSession(), session.getEtat().getNom(),
                        session.getInscrits().size(), session.getPlacesMax(),
                        session.getTauxRemplissage()));
                });
                
                contenu.append("\n");
            });
            
            Files.write(Paths.get(cheminFichier), contenu.toString().getBytes());
            return true;
            
        } catch (Exception e) {
            System.err.println("Erreur lors de l'export des formations: " + e.getMessage());
            return false;
        }
    }
    
    // EXPORT SESSIONS
    public boolean exporterSessions(String cheminFichier) {
        try {
            StringBuilder contenu = new StringBuilder();
            contenu.append("LISTE DES SESSIONS\n");
            contenu.append("==================\n\n");
            
            sessionRepo.trouverToutes().forEach(session -> {
                contenu.append(String.format("SESSION: %s\n", session.getCodeSession()));
                contenu.append(String.format("Formation: %s\n", session.getFormation().getTitre()));
                contenu.append(String.format("État: %s\n", session.getEtat().getNom()));
                contenu.append(String.format("Places: %d/%d (%.1f%%)\n", 
                    session.getInscrits().size(), session.getPlacesMax(),
                    session.getTauxRemplissage()));
                
                contenu.append("Apprenants inscrits:\n");
                if (session.getInscrits().isEmpty()) {
                    contenu.append("  Aucun apprenant inscrit\n");
                } else {
                    session.getInscrits().forEach(apprenant -> {
                        contenu.append(String.format("  - %s (%s)\n", 
                            apprenant.getNom(), apprenant.getEntreprise()));
                    });
                }
                contenu.append("\n");
            });
            
            Files.write(Paths.get(cheminFichier), contenu.toString().getBytes());
            return true;
            
        } catch (Exception e) {
            System.err.println("Erreur lors de l'export des sessions: " + e.getMessage());
            return false;
        }
    }
    
    // EXPORT STATISTIQUES
    public boolean exporterStatistiques(String cheminFichier) {
        try {
            String rapport = statsService.genererRapportDetaille();
            
            // Ajouter l'en-tête avec la date
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            String entete = String.format("Rapport généré le: %s\n\n", 
                LocalDateTime.now().format(formatter));
            
            Files.write(Paths.get(cheminFichier), (entete + rapport).getBytes());
            return true;
            
        } catch (Exception e) {
            System.err.println("Erreur lors de l'export des statistiques: " + e.getMessage());
            return false;
        }
    }
    
    // EXPORT COMPLET
    public boolean exporterComplet(String dossier) {
        try {
            // Créer le dossier s'il n'existe pas
            Path dossierPath = Paths.get(dossier);
            if (!Files.exists(dossierPath)) {
                Files.createDirectories(dossierPath);
            }
            
            // Générer un timestamp pour les fichiers
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            String timestamp = LocalDateTime.now().format(formatter);
            
            // Exporter tous les fichiers
            boolean success = true;
            success &= exporterFormations(dossier + "/formations_" + timestamp + ".txt");
            success &= exporterSessions(dossier + "/sessions_" + timestamp + ".txt");
            success &= exporterStatistiques(dossier + "/statistiques_" + timestamp + ".txt");
            
            return success;
            
        } catch (Exception e) {
            System.err.println("Erreur lors de l'export complet: " + e.getMessage());
            return false;
        }
    }
    
    // GÉNÉRER ATTESTATION POUR APPRENANT
    public boolean genererAttestation(Apprenant apprenant, Session session, String cheminFichier) {
        try {
            StringBuilder attestation = new StringBuilder();
            attestation.append("ATTESTATION DE PARTICIPATION\n");
            attestation.append("=============================\n\n");
            
            attestation.append("Je soussigné, responsable du centre de formation,\n");
            attestation.append("atteste que :\n\n");
            
            attestation.append(String.format("Monsieur/Madame: %s\n", apprenant.getNom()));
            attestation.append(String.format("Entreprise: %s\n\n", apprenant.getEntreprise()));
            
            attestation.append("a participé à la formation :\n");
            attestation.append(String.format("« %s »\n", session.getFormation().getTitre()));
            attestation.append(String.format("Session: %s\n", session.getCodeSession()));
            attestation.append(String.format("Durée: %d heures\n\n", session.getFormation().getDureeHeures()));
            
            attestation.append(String.format("Fait le: %s\n", 
                LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
            attestation.append("Signature:\n\n");
            attestation.append("_________________________\n");
            attestation.append("Responsable Formation\n");
            
            Files.write(Paths.get(cheminFichier), attestation.toString().getBytes());
            return true;
            
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération de l'attestation: " + e.getMessage());
            return false;
        }
    }
}