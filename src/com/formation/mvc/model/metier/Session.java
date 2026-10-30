package com.formation.mvc.model.metier;

import com.formation.mvc.model.patterns.Observable;
import java.io.Serializable;
import java.util.*;

/**
 * Session programmée d'une formation.
 *
 * - Pattern State : l'état courant (Etat) décide si l'on peut s'inscrire
 *   et vers quels états la session peut passer.
 * - Pattern Observer : chaque événement (inscription, liste d'attente,
 *   changement d'état) est notifié aux observateurs.
 * - Liste d'attente : quand la session est pleine, les apprenants sont mis
 *   en attente ; une désinscription inscrit automatiquement le premier.
 */
public class Session extends Observable implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Résultat d'une demande d'inscription. */
    public enum ResultatInscription { INSCRIT, LISTE_ATTENTE, REFUSE }

    private String codeSession;
    private Formation formation;
    private int placesMax;
    private Etat etat;
    private List<Apprenant> inscrits;
    private List<Apprenant> listeAttente;

    public Session(String codeSession, Formation formation, int placesMax) {
        this.codeSession = codeSession;
        this.formation = formation;
        this.placesMax = placesMax;
        this.etat = Etat.OUVERTE;
        this.inscrits = new ArrayList<>();
        this.listeAttente = new ArrayList<>();
    }

    // Getters
    public String getCodeSession() { return codeSession; }
    public Formation getFormation() { return formation; }
    public int getPlacesMax() { return placesMax; }
    public Etat getEtat() { return etat; }
    public List<Apprenant> getInscrits() { return new ArrayList<>(inscrits); }
    public List<Apprenant> getListeAttente() { return new ArrayList<>(listeAttente); }

    public void setPlacesMax(int placesMax) {
        this.placesMax = placesMax;
        mettreAJourRemplissage();
    }

    // === INSCRIPTIONS ===

    /** Inscription directe uniquement (sans liste d'attente). */
    public boolean inscrire(Apprenant apprenant) {
        if (!etat.peutInscrire() || estConcerne(apprenant) || getPlacesRestantes() <= 0) {
            return false;
        }
        inscrits.add(apprenant);
        apprenant.ajouterSession(this);
        notifyObservers(String.format("Session %s : %s inscrit (%d/%d)",
                codeSession, apprenant.getNom(), inscrits.size(), placesMax));
        mettreAJourRemplissage();
        return true;
    }

    /** Inscrit l'apprenant, ou le place en liste d'attente si la session est pleine. */
    public ResultatInscription inscrireOuMettreEnAttente(Apprenant apprenant) {
        if (inscrire(apprenant)) {
            return ResultatInscription.INSCRIT;
        }
        if (etat.peutMettreEnAttente() && !estConcerne(apprenant)) {
            listeAttente.add(apprenant);
            notifyObservers(String.format("Session %s : %s en liste d'attente (position %d)",
                    codeSession, apprenant.getNom(), listeAttente.size()));
            return ResultatInscription.LISTE_ATTENTE;
        }
        return ResultatInscription.REFUSE;
    }

    /**
     * Désinscrit l'apprenant (ou le retire de la liste d'attente).
     * Si une place se libère, le premier de la liste d'attente est inscrit.
     */
    public boolean desinscrire(Apprenant apprenant) {
        if (listeAttente.remove(apprenant)) {
            notifyObservers(String.format("Session %s : %s retiré de la liste d'attente",
                    codeSession, apprenant.getNom()));
            return true;
        }
        if (!inscrits.remove(apprenant)) {
            return false;
        }
        apprenant.retirerSession(this);
        notifyObservers(String.format("Session %s : %s désinscrit", codeSession, apprenant.getNom()));

        if (etat == Etat.PLEINE) {
            etat = Etat.OUVERTE;
        }
        promouvoirListeAttente();
        return true;
    }

    /** Inscrit le premier de la liste d'attente tant qu'il reste des places. */
    private void promouvoirListeAttente() {
        while (!listeAttente.isEmpty() && getPlacesRestantes() > 0 && etat.peutInscrire()) {
            Apprenant suivant = listeAttente.remove(0);
            notifyObservers(String.format("Session %s : %s passe de la liste d'attente aux inscrits",
                    codeSession, suivant.getNom()));
            inscrire(suivant);
        }
    }

    /** Passe la session en PLEINE quand il n'y a plus de place, et inversement. */
    private void mettreAJourRemplissage() {
        if (getPlacesRestantes() <= 0 && etat == Etat.OUVERTE) {
            changerEtat(Etat.PLEINE);
        } else if (getPlacesRestantes() > 0 && etat == Etat.PLEINE) {
            changerEtat(Etat.OUVERTE);
            promouvoirListeAttente();
        }
    }

    private boolean estConcerne(Apprenant apprenant) {
        return inscrits.contains(apprenant) || listeAttente.contains(apprenant);
    }

    public int getPlacesRestantes() {
        return Math.max(0, placesMax - inscrits.size());
    }

    public double getTauxRemplissage() {
        return placesMax == 0 ? 0 : (double) inscrits.size() / placesMax * 100;
    }

    // === ÉTATS (pattern State) ===

    /** Change l'état si la transition est autorisée par l'état courant. */
    public boolean changerEtat(Etat nouvelEtat) {
        if (nouvelEtat == etat || !etat.peutPasserA(nouvelEtat)) {
            return false;
        }
        Etat ancien = etat;
        etat = nouvelEtat;
        notifyObservers(String.format("Session %s : %s -> %s", codeSession, ancien, nouvelEtat));
        return true;
    }

    public boolean ouvrir() { return changerEtat(Etat.OUVERTE); }
    public boolean fermer() { return changerEtat(Etat.FERMEE); }
    public boolean commencer() { return changerEtat(Etat.EN_COURS); }
    public boolean terminer() { return changerEtat(Etat.TERMINEE); }
    public boolean annuler() { return changerEtat(Etat.ANNULEE); }

    /** Conservé pour compatibilité : applique les mêmes règles que changerEtat. */
    public boolean setEtat(Etat nouvelEtat) {
        return changerEtat(nouvelEtat);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Session session = (Session) obj;
        return Objects.equals(codeSession, session.codeSession);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codeSession);
    }

    @Override
    public String toString() {
        String attente = listeAttente.isEmpty() ? "" : String.format(" + %d en attente", listeAttente.size());
        return String.format("%s - %s [%s] %d/%d places (%.1f%%)%s",
            codeSession, formation.getTitre(), etat,
            inscrits.size(), placesMax, getTauxRemplissage(), attente);
    }
}
