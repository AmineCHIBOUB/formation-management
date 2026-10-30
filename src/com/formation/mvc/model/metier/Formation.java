package com.formation.mvc.model.metier;

import java.io.Serializable;
import java.util.*;

public class Formation implements Serializable {
    private static final long serialVersionUID = 1L;

    private String code;
    private String titre;
    private int dureeHeures;
    private List<String> options;
    
    public Formation(String code, String titre, int dureeHeures) {
        this.code = code;
        this.titre = titre;
        this.dureeHeures = dureeHeures;
        this.options = new ArrayList<>();
    }
    
    // Getters
    public String getCode() { return code; }
    public String getTitre() { return titre; }
    public int getDureeHeures() { return dureeHeures; }
    public List<String> getOptions() { return new ArrayList<>(options); }
    
    // Setters manquants
    public void setTitre(String titre) {
        this.titre = titre;
    }
    
    public void setDureeHeures(int dureeHeures) {
        this.dureeHeures = dureeHeures;
    }
    
    // Méthodes de gestion des options
    public void ajouterOption(String option) {
        if (!options.contains(option)) {
            options.add(option);
        }
    }
    
    public boolean supprimerOption(String option) {
        return options.remove(option);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Formation formation = (Formation) obj;
        return Objects.equals(code, formation.code);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
    
    @Override
    public String toString() {
        return String.format("%s: %s (%d heures)", code, titre, dureeHeures);
    }
}