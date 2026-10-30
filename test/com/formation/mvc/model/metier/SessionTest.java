package com.formation.mvc.model.metier;

import static org.junit.jupiter.api.Assertions.*;

import com.formation.mvc.model.metier.Session.ResultatInscription;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SessionTest {

    private Session session;
    private Apprenant alice;
    private Apprenant bob;
    private Apprenant chloe;

    @BeforeEach
    void setUp() {
        Formation java = new Formation("JAVA01", "Java avancé", 35);
        session = new Session("S1", java, 2);
        alice = new Apprenant("Alice", "Acme");
        bob = new Apprenant("Bob", "Acme");
        chloe = new Apprenant("Chloé", "Globex");
    }

    // === INSCRIPTIONS ===

    @Test
    void inscriptionDiminueLesPlacesRestantes() {
        assertTrue(session.inscrire(alice));
        assertEquals(1, session.getPlacesRestantes());
        assertTrue(alice.getSessionsInscrites().contains(session));
    }

    @Test
    void unApprenantNePeutPasSInscrireDeuxFois() {
        session.inscrire(alice);
        assertFalse(session.inscrire(alice));
        assertEquals(1, session.getInscrits().size());
    }

    @Test
    void laSessionPassePleineQuandToutesLesPlacesSontPrises() {
        session.inscrire(alice);
        session.inscrire(bob);
        assertEquals(Etat.PLEINE, session.getEtat());
        assertEquals(0, session.getPlacesRestantes());
    }

    // === LISTE D'ATTENTE ===

    @Test
    void sessionPleineMetEnListeDAttente() {
        session.inscrire(alice);
        session.inscrire(bob);

        assertEquals(ResultatInscription.LISTE_ATTENTE, session.inscrireOuMettreEnAttente(chloe));
        assertEquals(List.of(chloe), session.getListeAttente());
        assertFalse(session.getInscrits().contains(chloe));
    }

    @Test
    void uneDesinscriptionInscritLePremierDeLaListeDAttente() {
        session.inscrire(alice);
        session.inscrire(bob);
        session.inscrireOuMettreEnAttente(chloe);

        assertTrue(session.desinscrire(alice));

        assertTrue(session.getInscrits().contains(chloe));
        assertTrue(session.getListeAttente().isEmpty());
        assertEquals(Etat.PLEINE, session.getEtat());
    }

    @Test
    void augmenterLesPlacesPromeutLaListeDAttente() {
        session.inscrire(alice);
        session.inscrire(bob);
        session.inscrireOuMettreEnAttente(chloe);

        session.setPlacesMax(3);

        assertTrue(session.getInscrits().contains(chloe));
        assertEquals(Etat.PLEINE, session.getEtat());
    }

    @Test
    void onPeutQuitterLaListeDAttente() {
        session.inscrire(alice);
        session.inscrire(bob);
        session.inscrireOuMettreEnAttente(chloe);

        assertTrue(session.desinscrire(chloe));
        assertTrue(session.getListeAttente().isEmpty());
    }

    // === PATTERN STATE ===

    @Test
    void onNePeutPasSInscrireAUneSessionTerminee() {
        session.commencer();
        session.terminer();

        assertEquals(ResultatInscription.REFUSE, session.inscrireOuMettreEnAttente(alice));
    }

    @Test
    void uneSessionTermineeNePeutPlusChangerDEtat() {
        assertTrue(session.commencer());
        assertTrue(session.terminer());

        assertFalse(session.ouvrir());
        assertFalse(session.annuler());
        assertEquals(Etat.TERMINEE, session.getEtat());
    }

    @Test
    void uneSessionPlanifieeNePeutPasCommencerDirectement() {
        assertTrue(session.changerEtat(Etat.PLANIFIEE));
        assertFalse(session.commencer());
        assertEquals(Etat.PLANIFIEE, session.getEtat());
    }

    @Test
    void uneSessionAnnuleeEstDefinitive() {
        assertTrue(session.annuler());
        assertTrue(Etat.ANNULEE.getTransitionsPossibles().isEmpty());
        assertFalse(session.ouvrir());
    }

    // === PATTERN OBSERVER ===

    @Test
    void lesObservateursSontNotifiesDesEvenements() {
        List<String> messages = new ArrayList<>();
        session.addObserver(messages::add);

        session.inscrire(alice);
        session.inscrire(bob);
        session.inscrireOuMettreEnAttente(chloe);

        assertTrue(messages.stream().anyMatch(m -> m.contains("Alice inscrit")));
        assertTrue(messages.stream().anyMatch(m -> m.contains("Ouverte -> Pleine")));
        assertTrue(messages.stream().anyMatch(m -> m.contains("Chloé en liste d'attente")));
    }

    @Test
    void unObservateurNEstPasAjouteDeuxFois() {
        List<String> messages = new ArrayList<>();
        com.formation.mvc.model.patterns.Observer observateur = messages::add;
        session.addObserver(observateur);
        session.addObserver(observateur);

        session.inscrire(alice);

        assertEquals(1, messages.size());
    }
}
