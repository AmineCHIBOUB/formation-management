package com.formation.mvc.model.service;

import com.formation.mvc.model.metier.Session;
import com.formation.mvc.model.repository.SessionRepository;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SessionService {
    private static final String FICHIER_SESSIONS = "data/sessions.dat";
    private SessionRepository sessionRepository;
    
    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
        creerDossierData();
    }
    
    private void creerDossierData() {
        File dossier = new File("data");
        if (!dossier.exists()) {
            dossier.mkdirs();
        }
    }
    
    public void sauvegarderSessions() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FICHIER_SESSIONS))) {
            oos.writeObject(new ArrayList<>(sessionRepository.trouverToutes()));
            System.out.println("💾 Sessions sauvegardées dans " + FICHIER_SESSIONS);
        } catch (IOException e) {
            System.out.println("❌ Erreur sauvegarde sessions: " + e.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    public void chargerSessions() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FICHIER_SESSIONS))) {
            List<Session> sessions = (List<Session>) ois.readObject();
            for (Session session : sessions) {
                sessionRepository.sauvegarder(session);
            }
            System.out.println("📂 Sessions chargées depuis " + FICHIER_SESSIONS);
        } catch (FileNotFoundException e) {
            System.out.println("ℹ️ Aucune sauvegarde sessions trouvée");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("❌ Erreur chargement sessions: " + e.getMessage());
        }
    }
}