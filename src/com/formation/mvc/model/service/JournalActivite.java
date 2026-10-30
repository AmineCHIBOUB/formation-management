package com.formation.mvc.model.service;

import com.formation.mvc.model.patterns.Observer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pattern Observer : observateur concret qui garde l'historique
 * des événements des sessions (inscriptions, liste d'attente, changements d'état).
 */
public class JournalActivite implements Observer {
    private static final int TAILLE_MAX = 200;
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd/MM HH:mm");
    private static JournalActivite instance;

    private final List<String> evenements = new ArrayList<>();

    private JournalActivite() {
    }

    public static JournalActivite getInstance() {
        if (instance == null) {
            instance = new JournalActivite();
        }
        return instance;
    }

    @Override
    public void update(String message) {
        evenements.add(LocalDateTime.now().format(FORMAT) + "  " + message);
        if (evenements.size() > TAILLE_MAX) {
            evenements.remove(0);
        }
    }

    /** Les derniers événements, du plus récent au plus ancien. */
    public List<String> getDerniersEvenements(int nombre) {
        List<String> copie = new ArrayList<>(evenements);
        Collections.reverse(copie);
        return copie.subList(0, Math.min(nombre, copie.size()));
    }

    public int getNombreEvenements() {
        return evenements.size();
    }
}
