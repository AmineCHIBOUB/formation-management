package com.formation.mvc.model.patterns;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Pattern Observer : sujet observé.
 * La liste des observateurs n'est pas sauvegardée (transient) :
 * ils sont rattachés de nouveau au chargement des données.
 */
public abstract class Observable implements Serializable {
    private static final long serialVersionUID = 1L;

    private transient List<Observer> observers;

    private List<Observer> observers() {
        if (observers == null) {
            observers = new ArrayList<>();
        }
        return observers;
    }

    public void addObserver(Observer observer) {
        if (!observers().contains(observer)) {
            observers().add(observer);
        }
    }

    public void removeObserver(Observer observer) {
        observers().remove(observer);
    }

    public void notifyObservers(String message) {
        for (Observer observer : observers()) {
            observer.update(message);
        }
    }
}
