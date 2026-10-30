package com.formation.mvc.model.service;

import com.formation.mvc.model.metier.Apprenant;
import com.formation.mvc.model.metier.Formateur;
import com.formation.mvc.model.metier.Formation;
import com.formation.mvc.model.metier.Session;
import com.formation.mvc.model.patterns.Manager;
import com.formation.mvc.model.repository.FormationRepository;
import com.formation.mvc.model.repository.SessionRepository;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Sauvegarde et rechargement de toutes les données dans un seul fichier.
 * Tout est écrit d'un bloc pour conserver les liens entre objets :
 * un apprenant inscrit à une session reste le même objet après rechargement.
 */
public class PersistanceService {
    private static final String DOSSIER = "data";
    private static final String FICHIER = DOSSIER + File.separator + "donnees.dat";

    /** Contenu du fichier de sauvegarde. */
    private static class Donnees implements Serializable {
        private static final long serialVersionUID = 1L;
        List<Formation> formations;
        List<Session> sessions;
        List<Apprenant> apprenants;
        List<Formateur> formateurs;
    }

    private final FormationRepository formationRepo = FormationRepository.getInstance();
    private final SessionRepository sessionRepo = SessionRepository.getInstance();
    private final Manager manager = Manager.getInstance();

    public boolean sauvegarder() {
        Donnees donnees = new Donnees();
        donnees.formations = new ArrayList<>(formationRepo.trouverToutes());
        donnees.sessions = new ArrayList<>(sessionRepo.trouverToutes());
        donnees.apprenants = new ArrayList<>(manager.getApprenants());
        donnees.formateurs = new ArrayList<>(manager.getFormateurs());

        new File(DOSSIER).mkdirs();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FICHIER))) {
            oos.writeObject(donnees);
            System.out.println("💾 Données sauvegardées dans " + FICHIER);
            return true;
        } catch (IOException e) {
            System.out.println("❌ Erreur de sauvegarde : " + e.getMessage());
            return false;
        }
    }

    public boolean charger() {
        File fichier = new File(FICHIER);
        if (!fichier.exists()) {
            System.out.println("ℹ️ Aucune sauvegarde trouvée, démarrage à vide");
            return false;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fichier))) {
            Donnees donnees = (Donnees) ois.readObject();
            donnees.formations.forEach(formationRepo::sauvegarder);
            // le repository rattache le journal d'activité à chaque session
            donnees.sessions.forEach(sessionRepo::sauvegarder);
            donnees.apprenants.forEach(manager::ajouterApprenant);
            donnees.formateurs.forEach(manager::ajouterFormateur);
            System.out.println("📂 Données chargées depuis " + FICHIER);
            return true;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("❌ Erreur de chargement : " + e.getMessage());
            return false;
        }
    }
}
