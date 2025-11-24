package com.formation.mvc.model.service;

import com.formation.mvc.model.metier.Formation;
import com.formation.mvc.model.repository.FormationRepository;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FormationService {
    private static final String FICHIER_FORMATIONS = "data/formations.dat";
    private FormationRepository formationRepository;
    
    public FormationService(FormationRepository formationRepository) {
        this.formationRepository = formationRepository;
        creerDossierData();
    }
    
    private void creerDossierData() {
        File dossier = new File("data");
        if (!dossier.exists()) {
            dossier.mkdirs();
        }
    }
    
    public void sauvegarderFormations() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FICHIER_FORMATIONS))) {
            oos.writeObject(new ArrayList<>(formationRepository.trouverToutes()));
            System.out.println("💾 Formations sauvegardées dans " + FICHIER_FORMATIONS);
        } catch (IOException e) {
            System.out.println("❌ Erreur sauvegarde formations: " + e.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    public void chargerFormations() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FICHIER_FORMATIONS))) {
            List<Formation> formations = (List<Formation>) ois.readObject();
            for (Formation formation : formations) {
                formationRepository.sauvegarder(formation);
            }
            System.out.println("📂 Formations chargées depuis " + FICHIER_FORMATIONS);
        } catch (FileNotFoundException e) {
            System.out.println("ℹ️ Aucune sauvegarde trouvée, démarrage avec données vides");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("❌ Erreur chargement formations: " + e.getMessage());
        }
    }
}