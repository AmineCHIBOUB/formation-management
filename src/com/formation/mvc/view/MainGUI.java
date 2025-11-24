package com.formation.mvc.view;

import com.formation.mvc.model.metier.Formation;
import com.formation.mvc.model.metier.Session;
import com.formation.mvc.model.metier.Apprenant;
import com.formation.mvc.model.metier.Formateur;
import com.formation.mvc.model.patterns.Manager;
import com.formation.mvc.model.patterns.FactoryFormation;
import com.formation.mvc.model.repository.FormationRepository;
import com.formation.mvc.model.repository.SessionRepository;
import com.formation.mvc.model.service.FormationService;
import com.formation.mvc.model.service.SessionService;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.List;
import java.util.stream.Collectors;

// CORRECTION : Utiliser le ListView de JavaFX, pas de Swing
import javafx.scene.control.ListView;

public class MainGUI extends Application {
    private Manager manager = Manager.getInstance();
    private FormationRepository formationRepo = FormationRepository.getInstance();
    private SessionRepository sessionRepo = SessionRepository.getInstance();
    private FormationService formationService = new FormationService(formationRepo);
    private SessionService sessionService = new SessionService(sessionRepo);
    private FactoryFormation factory = new FactoryFormation();
    
    // CORRECTION : Utiliser ListView<String> de JavaFX
    private ListView<String> listeFormationsView = new ListView<>();
    private ListView<String> listeSessionsView = new ListView<>();
    private ListView<String> listeApprenantsView = new ListView<>();
    private TextArea detailsArea = new TextArea();
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        chargerDonnees();
        
        primaryStage.setTitle("🏫 Système de Gestion des Formations - Interface Graphique");
        
        TabPane tabPane = new TabPane();
        
        // Onglet Formations
        Tab tabFormations = new Tab("📚 Formations");
        tabFormations.setContent(creerOngletFormations());
        tabFormations.setClosable(false);
        
        // Onglet Sessions
        Tab tabSessions = new Tab("📅 Sessions");
        tabSessions.setContent(creerOngletSessions());
        tabSessions.setClosable(false);
        
        // Onglet Apprenants
        Tab tabApprenants = new Tab("👥 Apprenants");
        tabApprenants.setContent(creerOngletApprenants());
        tabApprenants.setClosable(false);
        
        // Onglet Statistiques
        Tab tabStats = new Tab("📊 Statistiques");
        tabStats.setContent(creerOngletStatistiques());
        tabStats.setClosable(false);
        
        tabPane.getTabs().addAll(tabFormations, tabSessions, tabApprenants, tabStats);
        
        Scene scene = new Scene(tabPane, 900, 700);
        primaryStage.setScene(scene);
        
        // Sauvegarde à la fermeture
        primaryStage.setOnCloseRequest(e -> sauvegarderDonnees());
        
        primaryStage.show();
    }
    
    private VBox creerOngletFormations() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));
        
        // === SECTION CRÉATION ===
        Label lblTitreCreation = new Label("Créer une nouvelle formation:");
        lblTitreCreation.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        
        HBox hboxCreation = new HBox(10);
        
        // Création normale
        VBox vboxNormale = new VBox(5);
        Label lblNormale = new Label("Création normale:");
        TextField txtCode = new TextField();
        txtCode.setPromptText("Code formation");
        TextField txtTitre = new TextField();
        txtTitre.setPromptText("Titre formation");
        TextField txtDuree = new TextField();
        txtDuree.setPromptText("Durée en heures");
        
        Button btnCreerNormale = new Button("Créer Formation");
        btnCreerNormale.setOnAction(e -> creerFormationNormale(txtCode, txtTitre, txtDuree));
        
        vboxNormale.getChildren().addAll(lblNormale, txtCode, txtTitre, txtDuree, btnCreerNormale);
        
        // Création Factory
        VBox vboxFactory = new VBox(5);
        Label lblFactory = new Label("Création avec Factory:");
        ComboBox<String> comboType = new ComboBox<>();
        comboType.setItems(FXCollections.observableArrayList("JAVA", "WEB", "DATA"));
        comboType.setPromptText("Type formation");
        TextField txtCodeFactory = new TextField();
        txtCodeFactory.setPromptText("Code formation");
        TextField txtTitreFactory = new TextField();
        txtTitreFactory.setPromptText("Titre formation");
        TextField txtDureeFactory = new TextField();
        txtDureeFactory.setPromptText("Durée en heures");
        
        Button btnCreerFactory = new Button("Créer avec Factory");
        btnCreerFactory.setOnAction(e -> creerFormationFactory(comboType, txtCodeFactory, txtTitreFactory, txtDureeFactory));
        
        vboxFactory.getChildren().addAll(lblFactory, comboType, txtCodeFactory, txtTitreFactory, txtDureeFactory, btnCreerFactory);
        
        hboxCreation.getChildren().addAll(vboxNormale, vboxFactory);
        
        // === SECTION LISTE ===
        Label lblListe = new Label("Liste des formations:");
        lblListe.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        
        // Boutons d'action
        HBox hboxActions = new HBox(10);
        Button btnActualiser = new Button("Actualiser liste");
        btnActualiser.setOnAction(e -> actualiserListeFormations());
        
        Button btnAjouterOption = new Button("Ajouter option");
        btnAjouterOption.setOnAction(e -> ajouterOptionFormation());
        
        hboxActions.getChildren().addAll(btnActualiser, btnAjouterOption);
        
        // Zone de détails
        detailsArea.setEditable(false);
        detailsArea.setPrefHeight(150);
        
        // Événement de sélection
        listeFormationsView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> afficherDetailsFormation(newValue)
        );
        
        vbox.getChildren().addAll(
            lblTitreCreation, hboxCreation, new Separator(),
            lblListe, hboxActions, listeFormationsView, 
            new Label("Détails:"), detailsArea
        );
        
        actualiserListeFormations();
        return vbox;
    }
    
    private VBox creerOngletSessions() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));
        
        // === SECTION CRÉATION SESSION ===
        Label lblCreationSession = new Label("Créer une nouvelle session:");
        lblCreationSession.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        
        GridPane gridSession = new GridPane();
        gridSession.setHgap(10);
        gridSession.setVgap(10);
        
        Label lblCodeSession = new Label("Code session:");
        TextField txtCodeSession = new TextField();
        txtCodeSession.setPromptText("SESSION001");
        
        Label lblFormation = new Label("Formation:");
        ComboBox<String> comboFormations = new ComboBox<>();
        actualiserComboFormations(comboFormations);
        
        Label lblPlaces = new Label("Places max:");
        TextField txtPlaces = new TextField();
        txtPlaces.setPromptText("20");
        
        Button btnCreerSession = new Button("Créer Session");
        btnCreerSession.setOnAction(e -> creerSession(txtCodeSession, comboFormations, txtPlaces));
        
        gridSession.add(lblCodeSession, 0, 0);
        gridSession.add(txtCodeSession, 1, 0);
        gridSession.add(lblFormation, 0, 1);
        gridSession.add(comboFormations, 1, 1);
        gridSession.add(lblPlaces, 0, 2);
        gridSession.add(txtPlaces, 1, 2);
        gridSession.add(btnCreerSession, 1, 3);
        
        // === SECTION LISTE SESSIONS ===
        Label lblListeSessions = new Label("Liste des sessions:");
        lblListeSessions.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        
        Button btnActualiserSessions = new Button("Actualiser sessions");
        btnActualiserSessions.setOnAction(e -> actualiserListeSessions());
        
        vbox.getChildren().addAll(
            lblCreationSession, gridSession, new Separator(),
            lblListeSessions, btnActualiserSessions, listeSessionsView
        );
        
        actualiserListeSessions();
        return vbox;
    }
    
    private VBox creerOngletApprenants() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));
        
        Label lblTitre = new Label("Gestion des Apprenants");
        lblTitre.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        
        // Formulaire ajout apprenant
        HBox hboxAjout = new HBox(10);
        TextField txtNomApprenant = new TextField();
        txtNomApprenant.setPromptText("Nom apprenant");
        TextField txtEntreprise = new TextField();
        txtEntreprise.setPromptText("Entreprise");
        
        Button btnAjouterApprenant = new Button("Ajouter Apprenant");
        btnAjouterApprenant.setOnAction(e -> {
            if (!txtNomApprenant.getText().isEmpty() && !txtEntreprise.getText().isEmpty()) {
                Apprenant apprenant = new Apprenant(txtNomApprenant.getText(), txtEntreprise.getText());
                manager.ajouterApprenant(apprenant);
                actualiserListeApprenants();
                txtNomApprenant.clear();
                txtEntreprise.clear();
                showAlert("✅ Apprenant ajouté avec succès!");
            }
        });
        
        hboxAjout.getChildren().addAll(txtNomApprenant, txtEntreprise, btnAjouterApprenant);
        
        // Bouton actualiser
        Button btnActualiserApprenants = new Button("Actualiser liste");
        btnActualiserApprenants.setOnAction(e -> actualiserListeApprenants());
        
        vbox.getChildren().addAll(lblTitre, hboxAjout, btnActualiserApprenants, listeApprenantsView);
        
        actualiserListeApprenants();
        return vbox;
    }
    
    private VBox creerOngletStatistiques() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        
        Label lblTitre = new Label("📊 Tableau de Bord - Statistiques");
        lblTitre.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");
        
        // Cartes statistiques
        VBox cartesStats = new VBox(10);
        
        Label lblFormations = new Label();
        Label lblSessions = new Label();
        Label lblApprenants = new Label();
        Label lblFormateurs = new Label();
        Label lblSessionsOuvertes = new Label();
        
        actualiserStatistiques(lblFormations, lblSessions, lblApprenants, lblFormateurs, lblSessionsOuvertes);
        
        // Style des cartes
        String styleCarte = "-fx-padding: 15; -fx-border-color: #cccccc; -fx-border-radius: 5; -fx-background-color: #f9f9f9;";
        lblFormations.setStyle(styleCarte);
        lblSessions.setStyle(styleCarte);
        lblApprenants.setStyle(styleCarte);
        lblFormateurs.setStyle(styleCarte);
        lblSessionsOuvertes.setStyle(styleCarte);
        
        cartesStats.getChildren().addAll(lblFormations, lblSessions, lblApprenants, lblFormateurs, lblSessionsOuvertes);
        
        Button btnActualiser = new Button("🔄 Actualiser les statistiques");
        btnActualiser.setOnAction(e -> actualiserStatistiques(lblFormations, lblSessions, lblApprenants, lblFormateurs, lblSessionsOuvertes));
        
        Button btnSauvegarder = new Button("💾 Sauvegarder maintenant");
        btnSauvegarder.setOnAction(e -> {
            sauvegarderDonnees();
            showAlert("✅ Données sauvegardées avec succès!");
        });
        
        vbox.getChildren().addAll(lblTitre, cartesStats, btnActualiser, btnSauvegarder);
        return vbox;
    }
    
    // === MÉTHODES DE GESTION ===
    
    private void creerFormationNormale(TextField txtCode, TextField txtTitre, TextField txtDuree) {
        try {
            Formation formation = new Formation(
                txtCode.getText(),
                txtTitre.getText(),
                Integer.parseInt(txtDuree.getText())
            );
            formationRepo.sauvegarder(formation);
            manager.ajouterFormation(formation);
            actualiserListeFormations();
            txtCode.clear(); txtTitre.clear(); txtDuree.clear();
            showAlert("✅ Formation créée avec succès!");
        } catch (NumberFormatException ex) {
            showAlert("❌ Durée invalide - entrez un nombre");
        }
    }
    
    private void creerFormationFactory(ComboBox<String> comboType, TextField txtCode, TextField txtTitre, TextField txtDuree) {
        try {
            String type = comboType.getValue();
            if (type == null) {
                showAlert("❌ Veuillez sélectionner un type de formation");
                return;
            }
            
            Formation formation = factory.creerFormation(
                type, txtCode.getText(), txtTitre.getText(), Integer.parseInt(txtDuree.getText())
            );
            formationRepo.sauvegarder(formation);
            manager.ajouterFormation(formation);
            actualiserListeFormations();
            comboType.setValue(null); txtCode.clear(); txtTitre.clear(); txtDuree.clear();
            showAlert("✅ Formation " + type + " créée avec Factory!");
        } catch (NumberFormatException ex) {
            showAlert("❌ Durée invalide - entrez un nombre");
        }
    }
    
    private void creerSession(TextField txtCodeSession, ComboBox<String> comboFormations, TextField txtPlaces) {
        try {
            String codeFormation = comboFormations.getValue();
            if (codeFormation == null) {
                showAlert("❌ Veuillez sélectionner une formation");
                return;
            }
            
            Formation formation = formationRepo.trouverParCode(codeFormation);
            if (formation != null) {
                Session session = new Session(
                    txtCodeSession.getText(),
                    formation,
                    Integer.parseInt(txtPlaces.getText())
                );
                sessionRepo.sauvegarder(session);
                manager.ajouterSession(session);
                actualiserListeSessions();
                txtCodeSession.clear(); txtPlaces.clear();
                showAlert("✅ Session créée avec succès!");
            }
        } catch (NumberFormatException ex) {
            showAlert("❌ Nombre de places invalide");
        }
    }
    
    private void ajouterOptionFormation() {
        String selection = listeFormationsView.getSelectionModel().getSelectedItem();
        if (selection != null) {
            String code = selection.split(":")[0].trim();
            Formation formation = formationRepo.trouverParCode(code);
            if (formation != null) {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Ajouter une option");
                dialog.setHeaderText("Formation: " + formation.getTitre());
                dialog.setContentText("Option à ajouter:");
                
                dialog.showAndWait().ifPresent(option -> {
                    formation.ajouterOption(option);
                    actualiserListeFormations();
                    showAlert("✅ Option '" + option + "' ajoutée!");
                });
            }
        } else {
            showAlert("❌ Veuillez sélectionner une formation");
        }
    }
    
    // === MÉTHODES D'ACTUALISATION ===
    
    private void actualiserListeFormations() {
        List<String> formations = formationRepo.trouverToutes().stream()
            .map(f -> String.format("%s: %s (%d heures) - Options: %s", 
                f.getCode(), f.getTitre(), f.getDureeHeures(), 
                String.join(", ", f.getOptions())))
            .collect(Collectors.toList());
        listeFormationsView.setItems(FXCollections.observableArrayList(formations));
    }
    
    private void actualiserListeSessions() {
        List<String> sessions = sessionRepo.trouverToutes().stream()
            .map(s -> String.format("%s - %s [%s] %d/%d places", 
                s.getCodeSession(), s.getFormation().getTitre(), 
                s.getEtat().getNom(), s.getInscrits().size(), s.getPlacesMax()))
            .collect(Collectors.toList());
        listeSessionsView.setItems(FXCollections.observableArrayList(sessions));
    }
    
    private void actualiserListeApprenants() {
        List<String> apprenants = manager.getApprenants().stream()
            .map(a -> String.format("%s - %s", a.getNom(), a.getEntreprise()))
            .collect(Collectors.toList());
        listeApprenantsView.setItems(FXCollections.observableArrayList(apprenants));
    }
    
    private void actualiserComboFormations(ComboBox<String> combo) {
        List<String> codes = formationRepo.trouverToutes().stream()
            .map(Formation::getCode)
            .collect(Collectors.toList());
        combo.setItems(FXCollections.observableArrayList(codes));
    }
    
    private void afficherDetailsFormation(String formationStr) {
        if (formationStr != null) {
            String code = formationStr.split(":")[0].trim();
            Formation formation = formationRepo.trouverParCode(code);
            if (formation != null) {
                String details = String.format(
                    "DÉTAILS DE LA FORMATION:\n" +
                    "Code: %s\nTitre: %s\nDurée: %d heures\n" +
                    "Options: %s",
                    formation.getCode(), formation.getTitre(), formation.getDureeHeures(),
                    String.join(", ", formation.getOptions())
                );
                detailsArea.setText(details);
            }
        }
    }
    
    private void actualiserStatistiques(Label formations, Label sessions, Label apprenants, Label formateurs, Label sessionsOuvertes) {
        formations.setText("📚 Formations: " + formationRepo.compter());
        sessions.setText("📅 Sessions: " + sessionRepo.compter());
        apprenants.setText("👥 Apprenants: " + manager.getApprenants().size());
        formateurs.setText("👨‍🏫 Formateurs: " + manager.getFormateurs().size());
        
        long ouvertes = sessionRepo.trouverSessionsOuvertes().size();
        sessionsOuvertes.setText("✅ Sessions ouvertes: " + ouvertes);
    }
    
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void chargerDonnees() {
        formationService.chargerFormations();
        sessionService.chargerSessions();
    }
    
    private void sauvegarderDonnees() {
        formationService.sauvegarderFormations();
        sessionService.sauvegarderSessions();
    }
}