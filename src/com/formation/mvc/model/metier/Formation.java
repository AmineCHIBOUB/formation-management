package com.formation.mvc.model.metier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
    
    public void ajouterOption(String option) {
        options.add(option);
        System.out.println("✅ Option ajoutée: " + option);
    }
    
    public String getDescriptionComplete() {
        return String.format("Formation %s: %s (%d heures)", code, titre, dureeHeures);
    }
    
    // Getters
    public String getCode() { return code; }
    public String getTitre() { return titre; }
    public int getDureeHeures() { return dureeHeures; }
    public List<String> getOptions() { return options; }
    
    @Override
    public String toString() {
        return getDescriptionComplete();
    }
}