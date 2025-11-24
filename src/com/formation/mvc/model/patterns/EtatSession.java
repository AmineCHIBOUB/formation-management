package com.formation.mvc.model.patterns;

public interface EtatSession {
    boolean peutInscrire();
    String getNom();
    void inscrireApprenant();
}