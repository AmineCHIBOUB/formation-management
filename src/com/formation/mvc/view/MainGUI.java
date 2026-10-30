package com.formation.mvc.view;

import com.formation.mvc.model.metier.Formation;
import com.formation.mvc.model.metier.Session;
import com.formation.mvc.model.metier.Apprenant;
import com.formation.mvc.model.metier.Formateur;
import com.formation.mvc.model.metier.Etat;
import com.formation.mvc.model.patterns.Manager;
import com.formation.mvc.model.patterns.FactoryFormation;
import com.formation.mvc.model.repository.FormationRepository;
import com.formation.mvc.model.repository.SessionRepository;
import com.formation.mvc.model.service.FormationService;
import com.formation.mvc.model.service.SessionService;
import com.formation.mvc.model.service.StatistiquesService;
import com.formation.mvc.model.service.ExportService;
import com.formation.mvc.model.service.NotificationService;
import com.formation.mvc.model.service.JournalActivite;
import com.formation.mvc.model.service.PersistanceService;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MainGUI extends Application {
    private Manager manager = Manager.getInstance();
    private FormationRepository formationRepo = FormationRepository.getInstance();
    private SessionRepository sessionRepo = SessionRepository.getInstance();
    private FormationService formationService = new FormationService(formationRepo);
    private SessionService sessionService = new SessionService(sessionRepo);
    private StatistiquesService statsService = new StatistiquesService();
    private ExportService exportService = new ExportService();
    private NotificationService notificationService = new NotificationService();
    private PersistanceService persistanceService = new PersistanceService();
    private FactoryFormation factory = new FactoryFormation();
    
    // ListViews
    private ListView<String> listeFormationsView = new ListView<>();
    private ListView<String> listeSessionsView = new ListView<>();
    private ListView<String> listeApprenantsView = new ListView<>();
    private ListView<String> listeFormateursView = new ListView<>();
    private TextArea detailsArea = new TextArea();
    
    // Champs de recherche
    private TextField searchFormationsField = new TextField();
    private TextField searchSessionsField = new TextField();
    private TextField searchApprenantsField = new TextField();
    private TextField searchFormateursField = new TextField();
    private ComboBox<String> filterComboBox = new ComboBox<>();
    
    // Données observables pour le filtrage
    private ObservableList<String> formationsData = FXCollections.observableArrayList();
    private ObservableList<String> sessionsData = FXCollections.observableArrayList();
    private ObservableList<String> apprenantsData = FXCollections.observableArrayList();
    private ObservableList<String> formateursData = FXCollections.observableArrayList();
    
    // Compteurs
    private Label lblCompteurFormateurs = new Label();

    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        chargerDonnees();
        
        primaryStage.setTitle("🏫 FORMATION PRO - Système de Gestion");
        
        BorderPane mainLayout = new BorderPane();
        
        // Navbar supérieure
        HBox navbar = createNavbar();
        mainLayout.setTop(navbar);
        
        // Contenu principal avec tabs
        TabPane tabPane = createTabPane();
        mainLayout.setCenter(tabPane);
        
        // Appliquer le style professionnel
        Scene scene = new Scene(mainLayout, 1400, 900);
        scene.getStylesheets().add("data:text/css," + getProfessionalStyleCSS());
        
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> sauvegarderDonnees());
        primaryStage.show();
    }
    
    private HBox createNavbar() {
        HBox navbar = new HBox();
        navbar.setStyle("-fx-background-color: linear-gradient(to right, #3b82f6, #1d4ed8); -fx-padding: 15px 30px; -fx-alignment: center;");
        navbar.setSpacing(20);
        
        Label logo = new Label("🏫 FORMATION PRO");
        logo.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");
        logo.setFont(Font.font("System", FontWeight.BOLD, 24));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label userInfo = new Label("👤 Administrateur");
        userInfo.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: 600;");
        
        navbar.getChildren().addAll(logo, spacer, userInfo);
        return navbar;
    }
    
    private TabPane createTabPane() {
        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-background-color: transparent; -fx-tab-min-width: 120px;");
        
        Tab tabDashboard = new Tab("📊 Dashboard");
        tabDashboard.setContent(creerOngletDashboard());
        tabDashboard.setClosable(false);
        
        Tab tabFormations = new Tab("📚 Formations");
        tabFormations.setContent(creerOngletFormations());
        tabFormations.setClosable(false);
        
        Tab tabSessions = new Tab("📅 Sessions");
        tabSessions.setContent(creerOngletSessions());
        tabSessions.setClosable(false);
        
        Tab tabApprenants = new Tab("👥 Apprenants");
        tabApprenants.setContent(creerOngletApprenants());
        tabApprenants.setClosable(false);
        
        Tab tabFormateurs = new Tab("👨‍🏫 Formateurs");
        tabFormateurs.setContent(creerOngletFormateurs());
        tabFormateurs.setClosable(false);
        
        tabPane.getTabs().addAll(tabDashboard, tabFormations, tabSessions, tabApprenants, tabFormateurs);
        return tabPane;
    }
    
    private VBox creerOngletDashboard() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(25));
        vbox.setStyle("-fx-background-color: #f8fafc;");
        
        // CARTES DE STATISTIQUES - TOUTES SUR LA MÊME LIGNE
        HBox hboxStats = new HBox(20);
        hboxStats.setPadding(new Insets(20));
        hboxStats.setAlignment(Pos.CENTER);
        
        VBox cardFormations = createStatCard("📚", "Formations", formationRepo.compter() + " formations", "#3b82f6");
        VBox cardSessions = createStatCard("📅", "Sessions", sessionRepo.compter() + " sessions", "#8b5cf6");
        VBox cardApprenants = createStatCard("👥", "Apprenants", manager.getApprenants().size() + " apprenants", "#10b981");
        VBox cardFormateurs = createStatCard("👨‍🏫", "Formateurs", manager.getFormateurs().size() + " formateurs", "#f59e0b");
        
        long ouvertes = sessionRepo.trouverSessionsOuvertes().size();
        VBox cardSessionsOuvertes = createStatCard("✅", "Sessions ouvertes", ouvertes + " sessions", "#06b6d4");
        
        double tauxMoyen = sessionRepo.tauxRemplissageMoyen();
        VBox cardTauxRemplissage = createStatCard("📈", "Taux de remplissage", String.format("%.1f%%", tauxMoyen), "#ef4444");
        
        // Ajouter toutes les cartes sur la même ligne
        hboxStats.getChildren().addAll(
            cardFormations, cardSessions, cardApprenants, 
            cardFormateurs, cardSessionsOuvertes, cardTauxRemplissage
        );
        
        // GRAPHIQUES - CÔTE À CÔTE (plus petits)
        HBox hboxGraphiques = new HBox(20);
        hboxGraphiques.setPadding(new Insets(20, 0, 20, 0));
        hboxGraphiques.setAlignment(Pos.CENTER);
        
        // Graphique 1: Tableau de bord des formations (plus petit)
        VBox graphique1Container = new VBox(10);
        graphique1Container.setStyle("-fx-background-color: white; -fx-background-radius: 12px; -fx-padding: 15px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);");
        graphique1Container.setPrefWidth(450);
        graphique1Container.setPrefHeight(280);

        Label lblGraph1 = new Label("📈 SESSIONS PAR FORMATION");
        lblGraph1.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        BarChart<String, Number> chartFormations = creerGraphiqueFormations();
        chartFormations.setPrefSize(400, 200);
        chartFormations.setLegendVisible(false);

        graphique1Container.getChildren().addAll(lblGraph1, chartFormations);
        
        // Graphique 2: Répartition des sessions (plus petit)
        VBox graphique2Container = new VBox(10);
        graphique2Container.setStyle("-fx-background-color: white; -fx-background-radius: 12px; -fx-padding: 15px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);");
        graphique2Container.setPrefWidth(450);
        graphique2Container.setPrefHeight(280);

        Label lblGraph2 = new Label("🎯 RÉPARTITION DES SESSIONS PAR ÉTAT");
        lblGraph2.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        PieChart chartRepartition = creerGraphiqueRepartition();
        chartRepartition.setPrefSize(400, 200);
        chartRepartition.setLegendVisible(false);

        graphique2Container.getChildren().addAll(lblGraph2, chartRepartition);
        
        hboxGraphiques.getChildren().addAll(graphique1Container, graphique2Container);
        
        // Section actions
        VBox actionSection = new VBox(20);
        actionSection.setPadding(new Insets(20));
        actionSection.setStyle("-fx-background-color: white; -fx-background-radius: 16px; -fx-border-radius: 16px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5);");
        actionSection.setAlignment(Pos.CENTER);
        
        Label lblActions = new Label("🚀 ACTIONS RAPIDES");
        lblActions.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        
        HBox hboxActions = new HBox(20);
        hboxActions.setAlignment(Pos.CENTER);
        
        Button btnActualiser = createActionButton("🔄 Actualiser les statistiques", "#3b82f6");
        Button btnSauvegarder = createActionButton("💾 Sauvegarder maintenant", "#10b981");
        Button btnExporterStats = createActionButton("📈 Exporter rapport complet", "#f59e0b");
        Button btnNotifications = createActionButton("🔔 Vérifier notifications", "#8b5cf6");
        Button btnRapportDetail = createActionButton("📊 Rapport détaillé", "#06b6d4");
        Button btnBackup = createActionButton("💾 Sauvegarde backup", "#ef4444");
        
        btnActualiser.setOnAction(e -> actualiserDashboard());
        btnSauvegarder.setOnAction(e -> {
            sauvegarderDonnees();
            showAlert("✅ Données sauvegardées avec succès!");
        });
        btnExporterStats.setOnAction(e -> exporterRapportStats());
        btnNotifications.setOnAction(e -> verifierNotifications());
        btnRapportDetail.setOnAction(e -> genererRapportDetail());
        btnBackup.setOnAction(e -> sauvegarderBackup());
        
        hboxActions.getChildren().addAll(btnActualiser, btnSauvegarder, btnExporterStats, btnNotifications, btnRapportDetail, btnBackup);
        actionSection.getChildren().addAll(lblActions, hboxActions);
        
        vbox.getChildren().addAll(hboxStats, hboxGraphiques, actionSection);
        return vbox;
    }
    
    private BarChart<String, Number> creerGraphiqueFormations() {
        // Axes
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Formations");
        xAxis.setStyle("-fx-tick-label-fill: #475569; -fx-font-size: 10px;");
        
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Nombre de sessions");
        yAxis.setStyle("-fx-tick-label-fill: #475569; -fx-font-size: 10px;");
        
        // Création du graphique
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Sessions par Formation");
        barChart.setLegendVisible(false);
        barChart.setStyle("-fx-background-color: transparent;");
        
        // Données des formations (top 5)
        List<Formation> topFormations = formationRepo.trouverToutes().stream()
            .sorted((f1, f2) -> Integer.compare(
                sessionRepo.trouverParFormation(f2).size(),
                sessionRepo.trouverParFormation(f1).size()
            ))
            .limit(5)
            .collect(Collectors.toList());
        
        // Série pour les sessions totales
        XYChart.Series<String, Number> seriesSessions = new XYChart.Series<>();
        seriesSessions.setName("Sessions totales");
        
        // Série pour les sessions actives
        XYChart.Series<String, Number> seriesActives = new XYChart.Series<>();
        seriesActives.setName("Sessions actives");
        
        for (Formation formation : topFormations) {
            String nomCourt = formation.getTitre().length() > 15 ? 
                formation.getTitre().substring(0, 15) + "..." : formation.getTitre();
            
            List<Session> sessions = sessionRepo.trouverParFormation(formation);
            long sessionsActives = sessions.stream()
                .filter(s -> s.getEtat() == Etat.OUVERTE)
                .count();
            
            seriesSessions.getData().add(new XYChart.Data<>(nomCourt, sessions.size()));
            seriesActives.getData().add(new XYChart.Data<>(nomCourt, sessionsActives));
        }
        
        barChart.getData().addAll(seriesSessions, seriesActives);
        
        return barChart;
    }
    
    private PieChart creerGraphiqueRepartition() {
        PieChart pieChart = new PieChart();
        pieChart.setTitle("Répartition des Sessions par État");
        pieChart.setStyle("-fx-background-color: transparent;");
        pieChart.setLabelsVisible(true);
        pieChart.setLegendVisible(false);
        
        // Calcul des données
        List<Session> toutesSessions = sessionRepo.trouverToutes();
        long sessionsOuvertes = toutesSessions.stream()
            .filter(s -> s.getEtat() == Etat.OUVERTE)
            .count();
        long sessionsPlanifiees = toutesSessions.stream()
            .filter(s -> s.getEtat() == Etat.PLANIFIEE)
            .count();
        long sessionsTerminees = toutesSessions.stream()
            .filter(s -> s.getEtat() == Etat.TERMINEE)
            .count();
        long sessionsAnnulees = toutesSessions.stream()
            .filter(s -> s.getEtat() == Etat.ANNULEE)
            .count();
        
        // Création des segments
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
            new PieChart.Data("Sessions Ouvertes", sessionsOuvertes),
            new PieChart.Data("Sessions Planifiées", sessionsPlanifiees),
            new PieChart.Data("Sessions Terminées", sessionsTerminees),
            new PieChart.Data("Sessions Annulées", sessionsAnnulees)
        );
        
        pieChart.setData(pieChartData);
        
        // Application des couleurs
        int colorIndex = 0;
        String[] colors = {"#3b82f6", "#10b981", "#f59e0b", "#ef4444"};
        
        for (PieChart.Data data : pieChartData) {
            final String color = colors[colorIndex % colors.length];
            data.getNode().setStyle("-fx-pie-color: " + color + ";");
            colorIndex++;
        }
        
        return pieChart;
    }
    
    private VBox createStatCard(String emoji, String title, String value, String color) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setStyle(String.format(
            "-fx-background-color: %s; -fx-background-radius: 12px; -fx-border-radius: 12px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 3);",
            color
        ));
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(180, 120);
        card.setMinSize(180, 120);
        
        Label emojiLabel = new Label(emoji);
        emojiLabel.setStyle("-fx-font-size: 24px;");
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: 600; -fx-alignment: center;");
        titleLabel.setWrapText(true);
        titleLabel.setAlignment(Pos.CENTER);
        
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-alignment: center;");
        valueLabel.setWrapText(true);
        valueLabel.setAlignment(Pos.CENTER);
        
        card.getChildren().addAll(emojiLabel, titleLabel, valueLabel);
        return card;
    }
    
    private Button createActionButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle(String.format(
            "-fx-background-color: %s; -fx-text-fill: white; -fx-background-radius: 10px; -fx-padding: 15px 25px; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand;",
            color
        ));
        
        button.setOnMouseEntered(e -> button.setStyle(String.format(
            "-fx-background-color: derive(%s, -20%%); -fx-text-fill: white; -fx-background-radius: 10px; -fx-padding: 15px 25px; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand;",
            color
        )));
        button.setOnMouseExited(e -> button.setStyle(String.format(
            "-fx-background-color: %s; -fx-text-fill: white; -fx-background-radius: 10px; -fx-padding: 15px 25px; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand;",
            color
        )));
        
        return button;
    }
    
    private void actualiserDashboard() {
        TabPane tabPane = (TabPane) ((BorderPane) listeFormationsView.getScene().getRoot()).getCenter();
        Tab dashboardTab = tabPane.getTabs().get(0);
        dashboardTab.setContent(creerOngletDashboard());
        tabPane.getSelectionModel().select(dashboardTab);
        showAlert("🔄 Dashboard actualisé avec les dernières données!");
    }

    private VBox creerOngletFormations() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(25));
        vbox.setStyle("-fx-background-color: #f8fafc;");
        
        Label lblRecherche = new Label("🔍 RECHERCHE ET FILTRES AVANCÉS");
        lblRecherche.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        
        HBox hboxRecherche = new HBox(15);
        hboxRecherche.setPadding(new Insets(20));
        hboxRecherche.setStyle("-fx-background-color: white; -fx-background-radius: 12px; -fx-padding: 20px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);");
        hboxRecherche.setAlignment(Pos.CENTER_LEFT);
        
        searchFormationsField.setPromptText("🔎 Rechercher par code, titre, options...");
        searchFormationsField.setPrefWidth(300);
        searchFormationsField.setStyle("-fx-background-radius: 8px; -fx-padding: 10px; -fx-border-color: #e2e8f0;");
        
        filterComboBox.setPromptText("📂 Filtrer par type");
        filterComboBox.setItems(FXCollections.observableArrayList("Tous", "JAVA", "WEB", "DATA"));
        filterComboBox.setPrefWidth(150);
        filterComboBox.setStyle("-fx-background-radius: 8px;");
        
        Button btnResetFiltres = new Button("🔄 Réinitialiser");
        btnResetFiltres.setStyle("-fx-background-color: #64748b; -fx-text-fill: white; -fx-background-radius: 8px; -fx-padding: 10px 20px; -fx-font-weight: bold;");
        
        btnResetFiltres.setOnAction(e -> {
            searchFormationsField.clear();
            filterComboBox.setValue("Tous");
        });
        
        hboxRecherche.getChildren().addAll(
            new Label("Recherche:"), searchFormationsField,
            new Label("Filtre:"), filterComboBox, btnResetFiltres
        );
        
        configurerRechercheFormations();
        
        Label lblTitreCreation = new Label("➕ CRÉER UNE NOUVELLE FORMATION");
        lblTitreCreation.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        
        // PANEL DE CRÉATION SUR UNE SEULE LIGNE
        HBox hboxCreation = new HBox(15);
        hboxCreation.setPadding(new Insets(20));
        hboxCreation.setStyle("-fx-background-color: white; -fx-background-radius: 12px; -fx-padding: 20px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);");
        hboxCreation.setAlignment(Pos.CENTER_LEFT);
        
        // Label Création Normale
        Label lblCreationNormale = new Label("🎓 Création Normale:");
        lblCreationNormale.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #475569;");
        
        // Champs sur une seule ligne
        TextField txtCode = new TextField();
        txtCode.setPromptText("🏷️ Code formation");
        txtCode.setPrefWidth(120);
        txtCode.setStyle("-fx-background-radius: 8px; -fx-padding: 10px; -fx-border-color: #e2e8f0;");
        
        TextField txtTitre = new TextField();
        txtTitre.setPromptText("📝 Titre formation");
        txtTitre.setPrefWidth(200);
        txtTitre.setStyle("-fx-background-radius: 8px; -fx-padding: 10px; -fx-border-color: #e2e8f0;");
        
        TextField txtDuree = new TextField();
        txtDuree.setPromptText("⏱️ Durée heures");
        txtDuree.setPrefWidth(100);
        txtDuree.setStyle("-fx-background-radius: 8px; -fx-padding: 10px; -fx-border-color: #e2e8f0;");
        
        Button btnCreerFormation = new Button("✅ Créer Formation");
        btnCreerFormation.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-background-radius: 8px; -fx-padding: 10px 20px; -fx-font-weight: bold;");
        btnCreerFormation.setOnAction(e -> creerFormationNormale(txtCode, txtTitre, txtDuree));
        
        // Ajout des composants sur une seule ligne
        hboxCreation.getChildren().addAll(
            lblCreationNormale, txtCode, txtTitre, txtDuree, btnCreerFormation
        );
        
        Label lblListe = new Label("📋 LISTE DES FORMATIONS");
        lblListe.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        
        HBox hboxActions = new HBox(15);
        hboxActions.setAlignment(Pos.CENTER_LEFT);
        
        Button btnActualiser = new Button("🔄 Actualiser liste");
        btnActualiser.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-background-radius: 8px; -fx-padding: 10px 20px; -fx-font-weight: bold;");
        btnActualiser.setOnAction(e -> actualiserListeFormations());
        
        Button btnAjouterOption = new Button("➕ Ajouter option");
        btnAjouterOption.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-background-radius: 8px; -fx-padding: 10px 20px; -fx-font-weight: bold;");
        btnAjouterOption.setOnAction(e -> ajouterOptionFormation());
        
        Button btnModifierFormation = new Button("✏️ Modifier formation");
        btnModifierFormation.setStyle("-fx-background-color: #f59e0b; -fx-text-fill: white; -fx-background-radius: 8px; -fx-padding: 10px 20px; -fx-font-weight: bold;");
        btnModifierFormation.setOnAction(e -> modifierFormation());
        
        Button btnExporter = new Button("📄 Exporter PDF");
        btnExporter.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-background-radius: 8px; -fx-padding: 10px 20px; -fx-font-weight: bold;");
        btnExporter.setOnAction(e -> exporterFormationsPDF());
        
        hboxActions.getChildren().addAll(btnActualiser, btnAjouterOption, btnModifierFormation, btnExporter);
        
        listeFormationsView.setStyle("-fx-background-color: white; -fx-background-radius: 8px; -fx-border-radius: 8px; -fx-border-color: #e2e8f0;");
        listeFormationsView.setPrefHeight(200);
        
        VBox vboxDetails = new VBox(10);
        vboxDetails.setPadding(new Insets(15));
        vboxDetails.setStyle("-fx-background-color: white; -fx-background-radius: 12px; -fx-padding: 20px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);");
        
        Label lblDetails = new Label("📖 DÉTAILS DE LA FORMATION");
        lblDetails.setStyle("-fx-font-size: 16px; -fx-font-weight: 600; -fx-text-fill: #475569;");
        
        detailsArea.setEditable(false);
        detailsArea.setPrefHeight(180);
        detailsArea.setWrapText(true);
        detailsArea.setStyle("-fx-background-radius: 8px; -fx-padding: 10px; -fx-border-color: #e2e8f0;");
        
        vboxDetails.getChildren().addAll(lblDetails, detailsArea);
        
        listeFormationsView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> afficherDetailsFormation(newValue)
        );
        
        vbox.getChildren().addAll(
            lblRecherche, hboxRecherche,
            lblTitreCreation, hboxCreation,
            lblListe, hboxActions, listeFormationsView, 
            vboxDetails
        );
        
        actualiserListeFormations();
        return vbox;
    }
    
    private VBox creerOngletSessions() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(25));
        vbox.setStyle("-fx-background-color: #f8fafc;");
        
        Label lblRechercheSession = new Label("🔍 RECHERCHE DE SESSIONS");
        lblRechercheSession.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        
        HBox hboxRechercheSession = new HBox(15);
        hboxRechercheSession.setPadding(new Insets(20));
        hboxRechercheSession.setStyle("-fx-background-color: white; -fx-background-radius: 12px; -fx-padding: 20px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);");
        hboxRechercheSession.setAlignment(Pos.CENTER_LEFT);
        
        searchSessionsField.setPromptText("🔎 Rechercher par code, formation, état...");
        searchSessionsField.setPrefWidth(300);
        searchSessionsField.setStyle("-fx-background-radius: 8px; -fx-padding: 10px; -fx-border-color: #e2e8f0;");
        
        Button btnResetSession = new Button("🔄 Réinitialiser");
        btnResetSession.setStyle("-fx-background-color: #64748b; -fx-text-fill: white; -fx-background-radius: 8px; -fx-padding: 10px 20px; -fx-font-weight: bold;");
        btnResetSession.setOnAction(e -> searchSessionsField.clear());
        
        hboxRechercheSession.getChildren().addAll(
            new Label("Recherche:"), searchSessionsField, btnResetSession
        );
        
        configurerRechercheSessions();
        
        Label lblCreationSession = new Label("➕ CRÉER UNE NOUVELLE SESSION");
        lblCreationSession.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        
        // HBox pour aligner les inputs horizontalement
        HBox hboxCreationSession = new HBox(15);
        hboxCreationSession.setPadding(new Insets(20));
        hboxCreationSession.setStyle("-fx-background-color: white; -fx-background-radius: 12px; -fx-padding: 20px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);");
        hboxCreationSession.setAlignment(Pos.CENTER_LEFT);
        
        Label lblCodeSession = new Label("🏷️ Code session:");
        lblCodeSession.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #475569;");
        TextField txtCodeSession = new TextField();
        txtCodeSession.setPromptText("SESSION001");
        txtCodeSession.setPrefWidth(120);
        txtCodeSession.setStyle("-fx-background-radius: 8px; -fx-padding: 10px; -fx-border-color: #e2e8f0;");
        
        Label lblFormation = new Label("📚 Formation:");
        lblFormation.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #475569;");
        ComboBox<String> comboFormations = new ComboBox<>();
        actualiserComboFormations(comboFormations);
        comboFormations.setPromptText("Sélectionner une formation");
        comboFormations.setPrefWidth(180);
        comboFormations.setStyle("-fx-background-radius: 8px;");
        
        Label lblPlaces = new Label("👥 Places max:");
        lblPlaces.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #475569;");
        TextField txtPlaces = new TextField();
        txtPlaces.setPromptText("20");
        txtPlaces.setPrefWidth(80);
        txtPlaces.setStyle("-fx-background-radius: 8px; -fx-padding: 10px; -fx-border-color: #e2e8f0;");
        
        Button btnCreerSession = new Button("✅ Créer Session");
        btnCreerSession.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-background-radius: 8px; -fx-padding: 10px 20px; -fx-font-weight: bold;");
        
        // Ajouter tous les éléments sur la même ligne
        hboxCreationSession.getChildren().addAll(
            lblCodeSession, txtCodeSession,
            lblFormation, comboFormations,
            lblPlaces, txtPlaces,
            btnCreerSession
        );
        
        btnCreerSession.setOnAction(e -> creerSession(txtCodeSession, comboFormations, txtPlaces));
        
        Label lblListeSessions = new Label("📋 LISTE DES SESSIONS");
        lblListeSessions.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        
        HBox hboxActionsSession = new HBox(15);
        hboxActionsSession.setAlignment(Pos.CENTER_LEFT);
        
        Button btnActualiserSessions = new Button("🔄 Actualiser sessions");
        btnActualiserSessions.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-background-radius: 8px; -fx-padding: 10px 20px; -fx-font-weight: bold;");
        btnActualiserSessions.setOnAction(e -> actualiserListeSessions());
        
        Button btnInscrireApprenant = new Button("👥 Inscrire apprenant");
        btnInscrireApprenant.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-background-radius: 8px; -fx-padding: 10px 20px; -fx-font-weight: bold;");
        btnInscrireApprenant.setOnAction(e -> inscrireApprenantSession());
        
        Button btnModifierSession = new Button("✏️ Modifier session");
        btnModifierSession.setStyle("-fx-background-color: #f59e0b; -fx-text-fill: white; -fx-background-radius: 8px; -fx-padding: 10px 20px; -fx-font-weight: bold;");
        btnModifierSession.setOnAction(e -> modifierSession());
        
        Button btnChangerEtat = new Button("🔄 Changer état");
        btnChangerEtat.setStyle("-fx-background-color: #8b5cf6; -fx-text-fill: white; -fx-background-radius: 8px; -fx-padding: 10px 20px; -fx-font-weight: bold;");
        btnChangerEtat.setOnAction(e -> changerEtatSession());
        
        Button btnDesinscrire = new Button("❌ Désinscrire");
        btnDesinscrire.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-background-radius: 8px; -fx-padding: 10px 20px; -fx-font-weight: bold;");
        btnDesinscrire.setOnAction(e -> desinscrireApprenant());
        
        hboxActionsSession.getChildren().addAll(btnActualiserSessions, btnInscrireApprenant, btnModifierSession, btnChangerEtat, btnDesinscrire);
        
        listeSessionsView.setStyle("-fx-background-color: white; -fx-background-radius: 8px; -fx-border-radius: 8px; -fx-border-color: #e2e8f0;");
        listeSessionsView.setPrefHeight(300);
        
        vbox.getChildren().addAll(
            lblRechercheSession, hboxRechercheSession,
            lblCreationSession, hboxCreationSession,
            lblListeSessions, hboxActionsSession, listeSessionsView
        );
        
        actualiserListeSessions();
        return vbox;
    }
    
    private VBox creerOngletApprenants() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(25));
        vbox.setStyle("-fx-background-color: #f8fafc;");
        
        Label lblRechercheApprenant = new Label("🔍 RECHERCHE D'APPRENANTS");
        lblRechercheApprenant.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        
        HBox hboxRechercheApprenant = new HBox(15);
        hboxRechercheApprenant.setPadding(new Insets(20));
        hboxRechercheApprenant.setStyle("-fx-background-color: white; -fx-background-radius: 12px; -fx-padding: 20px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);");
        hboxRechercheApprenant.setAlignment(Pos.CENTER_LEFT);
        
        searchApprenantsField.setPromptText("🔎 Rechercher par nom, entreprise...");
        searchApprenantsField.setPrefWidth(300);
        searchApprenantsField.setStyle("-fx-background-radius: 8px; -fx-padding: 10px; -fx-border-color: #e2e8f0;");
        
        Button btnResetApprenant = new Button("🔄 Réinitialiser");
        btnResetApprenant.setStyle("-fx-background-color: #64748b; -fx-text-fill: white; -fx-background-radius: 8px; -fx-padding: 10px 20px; -fx-font-weight: bold;");
        btnResetApprenant.setOnAction(e -> searchApprenantsField.clear());
        
        hboxRechercheApprenant.getChildren().addAll(
            new Label("Recherche:"), searchApprenantsField, btnResetApprenant
        );
        
        configurerRechercheApprenants();
        
        Label lblTitre = new Label("👥 GESTION DES APPRENANTS");
        lblTitre.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        
        HBox hboxAjout = new HBox(15);
        hboxAjout.setPadding(new Insets(20));
        hboxAjout.setStyle("-fx-background-color: white; -fx-background-radius: 12px; -fx-padding: 20px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);");
        hboxAjout.setAlignment(Pos.CENTER_LEFT);
        
        TextField txtNomApprenant = new TextField();
        txtNomApprenant.setPromptText("👤 Nom apprenant");
        txtNomApprenant.setPrefWidth(200);
        txtNomApprenant.setStyle("-fx-background-radius: 8px; -fx-padding: 10px; -fx-border-color: #e2e8f0;");
        
        TextField txtEntreprise = new TextField();
        txtEntreprise.setPromptText("🏢 Entreprise");
        txtEntreprise.setPrefWidth(200);
        txtEntreprise.setStyle("-fx-background-radius: 8px; -fx-padding: 10px; -fx-border-color: #e2e8f0;");
        
        Button btnAjouterApprenant = new Button("✅ Ajouter Apprenant");
        btnAjouterApprenant.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-background-radius: 8px; -fx-padding: 10px 20px; -fx-font-weight: bold;");
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
        
        hboxAjout.getChildren().addAll(
            new Label("Nom:"), txtNomApprenant,
            new Label("Entreprise:"), txtEntreprise, 
            btnAjouterApprenant
        );
        
        HBox hboxActions = new HBox(15);
        hboxActions.setAlignment(Pos.CENTER_LEFT);
        
        Button btnActualiserApprenants = new Button("🔄 Actualiser liste");
        btnActualiserApprenants.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-background-radius: 8px; -fx-padding: 10px 20px; -fx-font-weight: bold;");
        btnActualiserApprenants.setOnAction(e -> actualiserListeApprenants());
        
        Button btnRechercheAvancee = new Button("🔍 Recherche avancée");
        btnRechercheAvancee.setStyle("-fx-background-color: #8b5cf6; -fx-text-fill: white; -fx-background-radius: 8px; -fx-padding: 10px 20px; -fx-font-weight: bold;");
        btnRechercheAvancee.setOnAction(e -> rechercheAvanceeApprenants());
        
        hboxActions.getChildren().addAll(btnActualiserApprenants, btnRechercheAvancee);
        
        listeApprenantsView.setStyle("-fx-background-color: white; -fx-background-radius: 8px; -fx-border-radius: 8px; -fx-border-color: #e2e8f0;");
        listeApprenantsView.setPrefHeight(300);
        
        vbox.getChildren().addAll(
            lblRechercheApprenant, hboxRechercheApprenant,
            lblTitre, hboxAjout, hboxActions, listeApprenantsView
        );
        
        actualiserListeApprenants();
        return vbox;
    }
    
    private VBox creerOngletFormateurs() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(25));
        vbox.setStyle("-fx-background-color: #f8fafc;");
        
        Label lblRechercheFormateur = new Label("🔍 RECHERCHE DE FORMATEURS");
        lblRechercheFormateur.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-filtre: #1e293b;");
        
        HBox hboxRechercheFormateur = new HBox(15);
        hboxRechercheFormateur.setPadding(new Insets(20));
        hboxRechercheFormateur.setStyle("-fx-background-color: white; -fx-background-radius: 12px; -fx-padding: 20px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);");
        hboxRechercheFormateur.setAlignment(Pos.CENTER_LEFT);
        
        searchFormateursField = new TextField();
        searchFormateursField.setPromptText("🔎 Rechercher par nom, spécialité...");
        searchFormateursField.setPrefWidth(300);
        searchFormateursField.setStyle("-fx-background-radius: 8px; -fx-padding: 10px; -fx-border-color: #e2e8f0;");
        
        Button btnResetFormateur = new Button("🔄 Réinitialiser");
        btnResetFormateur.setStyle("-fx-background-color: #64748b; -fx-text-fill: white; -fx-background-radius: 8px; -fx-padding: 10px 20px; -fx-font-weight: bold;");
        btnResetFormateur.setOnAction(e -> searchFormateursField.clear());
        
        hboxRechercheFormateur.getChildren().addAll(
            new Label("Recherche:"), searchFormateursField, btnResetFormateur
        );
        
        Label lblTitre = new Label("👨‍🏫 GESTION DES FORMATEURS");
        lblTitre.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        
        lblCompteurFormateurs.setText(manager.getFormateurs().size() + " formateurs");
        lblCompteurFormateurs.setStyle("-fx-font-size: 16px; -fx-font-weight: 600; -fx-text-fill: #3b82f6;");
        
        HBox hboxAjout = new HBox(15);
        hboxAjout.setPadding(new Insets(20));
        hboxAjout.setStyle("-fx-background-color: white; -fx-background-radius: 12px; -fx-padding: 20px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);");
        hboxAjout.setAlignment(Pos.CENTER_LEFT);
        
        TextField txtNomFormateur = new TextField();
        txtNomFormateur.setPromptText("👤 Nom formateur");
        txtNomFormateur.setPrefWidth(200);
        txtNomFormateur.setStyle("-fx-background-radius: 8px; -fx-padding: 10px; -fx-border-color: #e2e8f0;");
        
        TextField txtSpecialite = new TextField();
        txtSpecialite.setPromptText("🎯 Spécialité");
        txtSpecialite.setPrefWidth(200);
        txtSpecialite.setStyle("-fx-background-radius: 8px; -fx-padding: 10px; -fx-border-color: #e2e8f0;");
        
        TextField txtAnneeExperience = new TextField();
        txtAnneeExperience.setPromptText("📅 Années d'expérience");
        txtAnneeExperience.setPrefWidth(200);
        txtAnneeExperience.setStyle("-fx-background-radius: 8px; -fx-padding: 10px; -fx-border-color: #e2e8f0;");
        
        Button btnAjouterFormateur = new Button("✅ Ajouter Formateur");
        btnAjouterFormateur.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-background-radius: 8px; -fx-padding: 10px 20px; -fx-font-weight: bold;");
        
        btnAjouterFormateur.setOnAction(e -> {
            if (!txtNomFormateur.getText().isEmpty() && !txtSpecialite.getText().isEmpty()) {
                try {
                    int anneesExp = txtAnneeExperience.getText().isEmpty() ? 0 : Integer.parseInt(txtAnneeExperience.getText());
                    Formateur formateur = new Formateur(txtNomFormateur.getText(), txtSpecialite.getText(), anneesExp);
                    manager.ajouterFormateur(formateur);
                    
                    actualiserListeFormateurs();
                    lblCompteurFormateurs.setText(manager.getFormateurs().size() + " formateurs");
                    
                    txtNomFormateur.clear();
                    txtSpecialite.clear();
                    txtAnneeExperience.clear();
                    showAlert("✅ Formateur ajouté avec succès!");
                } catch (NumberFormatException ex) {
                    showAlert("❌ Années d'expérience invalides - entrez un nombre");
                }
            } else {
                showAlert("❌ Veuillez remplir le nom et la spécialité");
            }
        });
        
        hboxAjout.getChildren().addAll(
            new Label("Nom:"), txtNomFormateur,
            new Label("Spécialité:"), txtSpecialite,
            new Label("Expérience:"), txtAnneeExperience,
            btnAjouterFormateur
        );
        
        Label lblListeFormateurs = new Label("📋 LISTE DES FORMATEURS");
        lblListeFormateurs.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        
        HBox hboxActions = new HBox(15);
        hboxActions.setAlignment(Pos.CENTER_LEFT);
        
        Button btnActualiserFormateurs = new Button("🔄 Actualiser liste");
        btnActualiserFormateurs.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-background-radius: 8px; -fx-padding: 10px 20px; -fx-font-weight: bold;");
        btnActualiserFormateurs.setOnAction(e -> {
            actualiserListeFormateurs();
            lblCompteurFormateurs.setText(manager.getFormateurs().size() + " formateurs");
        });
        
        Button btnAnalyserTendances = new Button("📊 Analyser tendances");
        btnAnalyserTendances.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-background-radius: 8px; -fx-padding: 10px 20px; -fx-font-weight: bold;");
        btnAnalyserTendances.setOnAction(e -> analyserTendances());
        
        hboxActions.getChildren().addAll(btnActualiserFormateurs, lblCompteurFormateurs, btnAnalyserTendances);
        
        listeFormateursView.setStyle("-fx-background-color: white; -fx-background-radius: 8px; -fx-border-radius: 8px; -fx-border-color: #e2e8f0;");
        listeFormateursView.setPrefHeight(300);
        
        configurerRechercheFormateurs();
        
        vbox.getChildren().addAll(
            lblRechercheFormateur, hboxRechercheFormateur,
            lblTitre, hboxAjout,
            lblListeFormateurs, hboxActions, listeFormateursView
        );
        
        actualiserListeFormateurs();
        return vbox;
    }
    
    // === MÉTHODES DE CONFIGURATION DES FILTRES ===
    
    private void configurerRechercheFormations() {
        actualiserDonneesFormations();
        
        FilteredList<String> filteredData = new FilteredList<>(formationsData, p -> true);
        
        searchFormationsField.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrerFormations(newValue, filteredData);
        });
        
        filterComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            filtrerFormations(searchFormationsField.getText(), filteredData);
        });
        
        SortedList<String> sortedData = new SortedList<>(filteredData);
        listeFormationsView.setItems(sortedData);
    }
    
    private void actualiserDonneesFormations() {
        formationsData.clear();
        List<String> formations = formationRepo.trouverToutes().stream()
            .map(f -> String.format("%s: %s (%d heures) - Options: %s", f.getCode(), f.getTitre(), f.getDureeHeures(), String.join(", ", f.getOptions())))
            .collect(Collectors.toList());
        formationsData.addAll(formations);
    }
    
    private void filtrerFormations(String recherche, FilteredList<String> filteredData) {
        filteredData.setPredicate(formationStr -> {
            if (recherche == null || recherche.isEmpty()) {
                return appliquerFiltreType(formationStr, filterComboBox.getValue());
            }
            
            String lowerCaseFilter = recherche.toLowerCase();
            String filtreType = filterComboBox.getValue();
            
            if (formationStr.toLowerCase().contains(lowerCaseFilter)) {
                return appliquerFiltreType(formationStr, filtreType);
            }
            return false;
        });
    }
    
    private boolean appliquerFiltreType(String formationStr, String filtreType) {
        if (filtreType == null || "Tous".equals(filtreType)) {
            return true;
        }
        return formationStr.toUpperCase().contains(filtreType);
    }
    
    private void configurerRechercheSessions() {
        actualiserDonneesSessions();
        
        FilteredList<String> filteredData = new FilteredList<>(sessionsData, p -> true);
        
        searchSessionsField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(sessionStr -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return sessionStr.toLowerCase().contains(lowerCaseFilter);
            });
        });
        
        SortedList<String> sortedData = new SortedList<>(filteredData);
        listeSessionsView.setItems(sortedData);
    }
    
    private void actualiserDonneesSessions() {
        sessionsData.clear();
        List<String> sessions = sessionRepo.trouverToutes().stream()
            .map(s -> String.format("%s - %s [%s] %d/%d places", s.getCodeSession(), s.getFormation().getTitre(), s.getEtat(), s.getInscrits().size(), s.getPlacesMax()))
            .collect(Collectors.toList());
        sessionsData.addAll(sessions);
    }
    
    private void configurerRechercheApprenants() {
        actualiserDonneesApprenants();
        
        FilteredList<String> filteredData = new FilteredList<>(apprenantsData, p -> true);
        
        searchApprenantsField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(apprenantStr -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return apprenantStr.toLowerCase().contains(lowerCaseFilter);
            });
        });
        
        SortedList<String> sortedData = new SortedList<>(filteredData);
        listeApprenantsView.setItems(sortedData);
    }
    
    private void actualiserDonneesApprenants() {
        apprenantsData.clear();
        List<String> apprenants = manager.getApprenants().stream()
            .map(a -> String.format("%s - %s", a.getNom(), a.getEntreprise()))
            .collect(Collectors.toList());
        apprenantsData.addAll(apprenants);
    }
    
    private void configurerRechercheFormateurs() {
        actualiserDonneesFormateurs();
        
        FilteredList<String> filteredData = new FilteredList<>(formateursData, p -> true);
        
        searchFormateursField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(formateurStr -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return formateurStr.toLowerCase().contains(lowerCaseFilter);
            });
        });
        
        SortedList<String> sortedData = new SortedList<>(filteredData);
        listeFormateursView.setItems(sortedData);
    }
    
    private void actualiserDonneesFormateurs() {
        formateursData.clear();
        List<String> formateurs = manager.getFormateurs().stream()
            .map(f -> String.format("%s - %s (%d années exp.)", f.getNom(), f.getSpecialite(), f.getAnneeExperience()))
            .collect(Collectors.toList());
        formateursData.addAll(formateurs);
    }
    
    // === MÉTHODES DE GESTION EXISTANTES ===
    
    private void creerFormationNormale(TextField txtCode, TextField txtTitre, TextField txtDuree) {
        try {
            String code = txtCode.getText().trim();
            String titre = txtTitre.getText().trim();
            String dureeText = txtDuree.getText().trim();
            
            if (code.isEmpty() || titre.isEmpty() || dureeText.isEmpty()) {
                showAlert("❌ Veuillez remplir tous les champs");
                return;
            }
            
            Formation formation = new Formation(code, titre, Integer.parseInt(dureeText));
            formationRepo.sauvegarder(formation);
            actualiserListeFormations();
            txtCode.clear(); txtTitre.clear(); txtDuree.clear();
            showAlert("✅ Formation créée avec succès!");
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
                Session session = new Session(txtCodeSession.getText(), formation, Integer.parseInt(txtPlaces.getText()));
                sessionRepo.sauvegarder(session);
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
                    formationRepo.sauvegarder(formation);
                    actualiserListeFormations();
                    showAlert("✅ Option '" + option + "' ajoutée!");
                });
            }
        } else {
            showAlert("❌ Veuillez sélectionner une formation");
        }
    }
    
    private void inscrireApprenantSession() {
        String selection = listeSessionsView.getSelectionModel().getSelectedItem();
        if (selection != null) {
            String codeSession = selection.split(" - ")[0].trim();
            Session session = sessionRepo.trouverParCode(codeSession);
            
            if (session != null) {
                ChoiceDialog<Apprenant> dialog = new ChoiceDialog<>();
                dialog.setTitle("Inscription Apprenant");
                dialog.setHeaderText("Session: " + session.getCodeSession());
                dialog.setContentText("Choisir un apprenant:");
                dialog.getItems().addAll(manager.getApprenants());
                
                dialog.showAndWait().ifPresent(apprenant -> {
                    switch (session.inscrireOuMettreEnAttente(apprenant)) {
                        case INSCRIT:
                            showAlert("✅ " + apprenant.getNom() + " inscrit à la session!");
                            break;
                        case LISTE_ATTENTE:
                            showAlert("⏳ Session pleine : " + apprenant.getNom()
                                + " est en liste d'attente (position " + session.getListeAttente().size() + ").\n"
                                + "Il sera inscrit automatiquement si une place se libère.");
                            break;
                        default:
                            showAlert("❌ Inscription impossible : apprenant déjà inscrit ou en attente, "
                                + "ou session « " + session.getEtat() + " ».");
                    }
                    sessionRepo.sauvegarder(session);
                    actualiserListeSessions();
                });
            }
        } else {
            showAlert("❌ Veuillez sélectionner une session");
        }
    }
    
    private void actualiserListeFormations() {
        actualiserDonneesFormations();
        showAlert("✅ Liste des formations actualisée!");
    }
    
    private void actualiserListeSessions() {
        actualiserDonneesSessions();
        showAlert("✅ Liste des sessions actualisée!");
    }
    
    private void actualiserListeApprenants() {
        actualiserDonneesApprenants();
        showAlert("✅ Liste des apprenants actualisée!");
    }
    
    private void actualiserListeFormateurs() {
        actualiserDonneesFormateurs();
        showAlert("✅ Liste des formateurs actualisée!");
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
                String details = String.format("DÉTAILS DE LA FORMATION:\nCode: %s\nTitre: %s\nDurée: %d heures\nOptions: %s\nNombre de sessions: %d",
                    formation.getCode(), formation.getTitre(), formation.getDureeHeures(),
                    String.join(", ", formation.getOptions()),
                    sessionRepo.trouverParFormation(formation).size());
                detailsArea.setText(details);
            }
        }
    }
    
    private void exporterFormationsPDF() {
        boolean success = exportService.exporterFormations("export_formations.txt");
        if (success) {
            showAlert("✅ Export réalisé: export_formations.txt");
        } else {
            showAlert("❌ Erreur lors de l'export");
        }
    }
    
    private void exporterRapportStats() {
        boolean success = exportService.exporterStatistiques("rapport_statistiques.txt");
        if (success) {
            showAlert("✅ Rapport exporté: rapport_statistiques.txt");
        } else {
            showAlert("❌ Erreur lors de l'export du rapport");
        }
    }
    
    // === NOUVELLES FONCTIONS CORRIGÉES ===
    
    private void modifierFormation() {
        String selection = listeFormationsView.getSelectionModel().getSelectedItem();
        if (selection != null) {
            String code = selection.split(":")[0].trim();
            Formation formation = formationRepo.trouverParCode(code);
            if (formation != null) {
                TextInputDialog dialogTitre = new TextInputDialog(formation.getTitre());
                dialogTitre.setTitle("Modifier Formation");
                dialogTitre.setHeaderText("Modifier le titre de la formation");
                dialogTitre.setContentText("Nouveau titre:");
                
                dialogTitre.showAndWait().ifPresent(nouveauTitre -> {
                    formation.setTitre(nouveauTitre);
                    formationRepo.sauvegarder(formation);
                    actualiserListeFormations();
                    showAlert("✅ Formation modifiée avec succès!");
                });
            }
        } else {
            showAlert("❌ Veuillez sélectionner une formation");
        }
    }
    
    private void modifierSession() {
        String selection = listeSessionsView.getSelectionModel().getSelectedItem();
        if (selection != null) {
            String codeSession = selection.split(" - ")[0].trim();
            Session session = sessionRepo.trouverParCode(codeSession);
            
            if (session != null) {
                TextInputDialog dialogPlaces = new TextInputDialog(String.valueOf(session.getPlacesMax()));
                dialogPlaces.setTitle("Modifier Session");
                dialogPlaces.setHeaderText("Modifier le nombre de places");
                dialogPlaces.setContentText("Nouveau nombre de places:");
                
                dialogPlaces.showAndWait().ifPresent(nouvellesPlaces -> {
                    try {
                        session.setPlacesMax(Integer.parseInt(nouvellesPlaces));
                        sessionRepo.sauvegarder(session);
                        actualiserListeSessions();
                        showAlert("✅ Session modifiée avec succès!");
                    } catch (NumberFormatException ex) {
                        showAlert("❌ Nombre de places invalide");
                    }
                });
            }
        } else {
            showAlert("❌ Veuillez sélectionner une session");
        }
    }
    
    private void changerEtatSession() {
        String selection = listeSessionsView.getSelectionModel().getSelectedItem();
        if (selection != null) {
            String codeSession = selection.split(" - ")[0].trim();
            Session session = sessionRepo.trouverParCode(codeSession);
            
            if (session != null) {
                // pattern State : seuls les états autorisés depuis l'état courant sont proposés
                // (PLEINE est géré automatiquement selon le remplissage)
                List<Etat> etatsPossibles = new ArrayList<>(session.getEtat().getTransitionsPossibles());
                etatsPossibles.remove(Etat.PLEINE);
                if (etatsPossibles.isEmpty()) {
                    showAlert("ℹ️ La session est « " + session.getEtat() + " » : son état ne peut plus changer.");
                    return;
                }

                ChoiceDialog<Etat> dialog = new ChoiceDialog<>(etatsPossibles.get(0), etatsPossibles);
                dialog.setTitle("Changer État Session");
                dialog.setHeaderText("Session: " + session.getCodeSession() + " (actuellement : " + session.getEtat() + ")");
                dialog.setContentText("Choisir le nouvel état:");

                dialog.showAndWait().ifPresent(nouvelEtat -> {
                    if (session.changerEtat(nouvelEtat)) {
                        sessionRepo.sauvegarder(session);
                        actualiserListeSessions();
                        showAlert("✅ Session passée à l'état « " + nouvelEtat + " »");
                    } else {
                        showAlert("❌ Transition non autorisée");
                    }
                });
            }
        } else {
            showAlert("❌ Veuillez sélectionner une session");
        }
    }
    
    private void desinscrireApprenant() {
        String selection = listeSessionsView.getSelectionModel().getSelectedItem();
        if (selection != null) {
            String codeSession = selection.split(" - ")[0].trim();
            Session session = sessionRepo.trouverParCode(codeSession);
            
            if (session != null && (!session.getInscrits().isEmpty() || !session.getListeAttente().isEmpty())) {
                ChoiceDialog<Apprenant> dialog = new ChoiceDialog<>();
                dialog.setTitle("Désinscrire Apprenant");
                dialog.setHeaderText("Session: " + session.getCodeSession());
                dialog.setContentText("Choisir un apprenant à désinscrire:");
                dialog.getItems().addAll(session.getInscrits());
                dialog.getItems().addAll(session.getListeAttente());

                dialog.showAndWait().ifPresent(apprenant -> {
                    List<Apprenant> attenteAvant = session.getListeAttente();
                    if (session.desinscrire(apprenant)) {
                        sessionRepo.sauvegarder(session);
                        actualiserListeSessions();
                        StringBuilder message = new StringBuilder("✅ " + apprenant.getNom() + " désinscrit de la session!");
                        // signale les apprenants passés automatiquement de la liste d'attente aux inscrits
                        for (Apprenant a : attenteAvant) {
                            if (session.getInscrits().contains(a)) {
                                message.append("\n➡️ ").append(a.getNom()).append(" a été inscrit depuis la liste d'attente.");
                            }
                        }
                        showAlert(message.toString());
                    } else {
                        showAlert("❌ Erreur lors de la désinscription");
                    }
                });
            } else {
                showAlert("❌ Aucun apprenant inscrit à cette session");
            }
        } else {
            showAlert("❌ Veuillez sélectionner une session");
        }
    }
    
    // === AUTRES FONCTIONS ===
    
    private void rechercheAvanceeApprenants() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Recherche Avancée Apprenants");
        dialog.setHeaderText("Recherche multi-critères");
        dialog.setContentText("Entrez votre recherche:");
        
        dialog.showAndWait().ifPresent(recherche -> {
            List<Apprenant> resultats = manager.getApprenants().stream()
                .filter(a -> a.getNom().toLowerCase().contains(recherche.toLowerCase()) ||
                           a.getEntreprise().toLowerCase().contains(recherche.toLowerCase()))
                .collect(Collectors.toList());
            
            if (resultats.isEmpty()) {
                showAlert("🔍 Aucun résultat trouvé pour: " + recherche);
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("🔍 RÉSULTATS DE RECHERCHE:\n\n");
                for (Apprenant apprenant : resultats) {
                    sb.append("• ").append(apprenant.getNom())
                      .append(" - ").append(apprenant.getEntreprise()).append("\n");
                }
                showAlert(sb.toString());
            }
        });
    }
    
    private void sauvegarderBackup() {
        String timestamp = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        try {
            if (!persistanceService.sauvegarder()) {
                showAlert("❌ La sauvegarde a échoué (voir la console)");
                return;
            }

            StringBuilder backupContent = new StringBuilder();
            backupContent.append("=== BACKUP FORMATION PRO ===\n");
            backupContent.append("Date: ").append(timestamp).append("\n\n");
            backupContent.append("Formations: ").append(formationRepo.compter()).append("\n");
            backupContent.append("Sessions: ").append(sessionRepo.compter()).append("\n");
            backupContent.append("Apprenants: ").append(manager.getApprenants().size()).append("\n");
            backupContent.append("Formateurs: ").append(manager.getFormateurs().size()).append("\n");
            
            showAlert("💾 Sauvegarde effectuée avec succès!\nFichier : data/donnees.dat");
            System.out.println("=== CONTENU DU BACKUP ===\n" + backupContent.toString());
            
        } catch (Exception e) {
            showAlert("❌ Erreur lors de la création du backup: " + e.getMessage());
        }
    }
    
    private void genererRapportPerformance() {
        try {
            StringBuilder rapport = new StringBuilder();
            rapport.append("=== RAPPORT DE PERFORMANCE ===\n\n");
            
            rapport.append("📊 STATISTIQUES AVANCÉES:\n");
            rapport.append("Taux de remplissage moyen: ").append(String.format("%.1f%%", sessionRepo.tauxRemplissageMoyen())).append("\n");
            rapport.append("Sessions ouvertes: ").append(sessionRepo.trouverSessionsOuvertes().size()).append("\n");
            rapport.append("Sessions planifiées: ").append(sessionRepo.trouverToutes().stream()
                .filter(s -> s.getEtat() == Etat.PLANIFIEE).count()).append("\n");
            
            rapport.append("\n🏆 TOP FORMATIONS:\n");
            formationRepo.trouverToutes().stream()
                .sorted((f1, f2) -> Integer.compare(
                    sessionRepo.trouverParFormation(f2).size(),
                    sessionRepo.trouverParFormation(f1).size()
                ))
                .limit(3)
                .forEach(f -> rapport.append("- ").append(f.getTitre())
                    .append(" (").append(sessionRepo.trouverParFormation(f).size()).append(" sessions)\n"));
            
            showAlert("📈 Rapport de performance généré dans la console!");
            System.out.println(rapport.toString());
            
        } catch (Exception e) {
            showAlert("❌ Erreur lors de la génération du rapport: " + e.getMessage());
        }
    }
    
    private void analyserTendances() {
        try {
            StringBuilder analyse = new StringBuilder();
            analyse.append("📈 ANALYSE DES TENDANCES\n\n");
            
            long totalSessions = sessionRepo.compter();
            long sessionsActives = sessionRepo.trouverSessionsOuvertes().size();
            double pourcentageActives = totalSessions > 0 ? (sessionsActives * 100.0 / totalSessions) : 0;
            
            analyse.append("Sessions actives: ").append(sessionsActives).append("/").append(totalSessions)
                  .append(" (").append(String.format("%.1f%%", pourcentageActives)).append(")\n");
            
            List<Session> sessions = sessionRepo.trouverToutes();
            long sessionsPleines = sessions.stream().filter(s -> s.getInscrits().size() >= s.getPlacesMax()).count();
            long sessionsQuasiPleines = sessions.stream()
                .filter(s -> s.getInscrits().size() >= s.getPlacesMax() * 0.8)
                .count();
            
            analyse.append("Sessions pleines: ").append(sessionsPleines).append("\n");
            analyse.append("Sessions quasi-pleines (>80%): ").append(sessionsQuasiPleines).append("\n");
            
            analyse.append("\n💡 RECOMMANDATIONS:\n");
            if (sessionsQuasiPleines > 0) {
                analyse.append("- ").append(sessionsQuasiPleines).append(" session(s) nécessite(nt) une attention (presque pleine)\n");
            }
            if (pourcentageActives < 30) {
                analyse.append("- Faible taux de sessions actives, envisagez de nouvelles promotions\n");
            }
            
            showAlert(analyse.toString());
            
        } catch (Exception e) {
            showAlert("❌ Erreur lors de l'analyse: " + e.getMessage());
        }
    }
    
    private void verifierNotifications() {
        int sessionsPresquePleines = 0;
        int sessionsSansApprenants = 0;
        List<Session> sessions = sessionRepo.trouverToutes();
        
        for (Session session : sessions) {
            double tauxRemplissage = (double) session.getInscrits().size() / session.getPlacesMax() * 100;
            if (tauxRemplissage >= 80) {
                sessionsPresquePleines++;
            }
            if (session.getInscrits().isEmpty()) {
                sessionsSansApprenants++;
            }
        }
        
        StringBuilder notifications = new StringBuilder();
        notifications.append("🔔 NOTIFICATIONS\n\n");
        
        if (sessionsPresquePleines > 0) {
            notifications.append("• ").append(sessionsPresquePleines).append(" session(s) presque pleine(s)\n");
        }
        if (sessionsSansApprenants > 0) {
            notifications.append("• ").append(sessionsSansApprenants).append(" session(s) sans apprenants\n");
        }
        
        if (sessionsPresquePleines == 0 && sessionsSansApprenants == 0) {
            notifications.append("✅ Aucune notification urgente");
        }

        // journal alimenté par le pattern Observer
        List<String> evenements = JournalActivite.getInstance().getDerniersEvenements(10);
        if (!evenements.isEmpty()) {
            notifications.append("\n\n📝 DERNIÈRES ACTIVITÉS\n");
            for (String evenement : evenements) {
                notifications.append("• ").append(evenement).append("\n");
            }
        }
        
        showAlert(notifications.toString());
    }
    
    private void genererRapportDetail() {
        try {
            StringBuilder rapport = new StringBuilder();
            rapport.append("=== RAPPORT DÉTAILLÉ FORMATION PRO ===\n\n");
            
            rapport.append("STATISTIQUES GÉNÉRALES:\n");
            rapport.append("- Formations: ").append(formationRepo.compter()).append("\n");
            rapport.append("- Sessions: ").append(sessionRepo.compter()).append("\n");
            rapport.append("- Apprenants: ").append(manager.getApprenants().size()).append("\n");
            rapport.append("- Formateurs: ").append(manager.getFormateurs().size()).append("\n\n");
            
            rapport.append("DÉTAILS DES FORMATIONS:\n");
            for (Formation formation : formationRepo.trouverToutes()) {
                rapport.append("- ").append(formation.getCode()).append(": ").append(formation.getTitre())
                      .append(" (").append(formation.getDureeHeures()).append("h) - Options: ")
                      .append(String.join(", ", formation.getOptions())).append("\n");
            }
            
            rapport.append("\nSESSIONS PAR FORMATION:\n");
            for (Formation formation : formationRepo.trouverToutes()) {
                List<Session> sessionsFormation = sessionRepo.trouverParFormation(formation);
                rapport.append("- ").append(formation.getTitre()).append(": ").append(sessionsFormation.size()).append(" session(s)\n");
            }
            
            showAlert("📊 Rapport détaillé généré dans la console!");
            System.out.println(rapport.toString());
            
        } catch (Exception e) {
            showAlert("❌ Erreur lors de la génération du rapport: " + e.getMessage());
        }
    }
    
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void chargerDonnees() {
        persistanceService.charger();
    }

    private void sauvegarderDonnees() {
        persistanceService.sauvegarder();
    }
    
    private String getProfessionalStyleCSS() {
        return """
            .root {
                -fx-font-family: 'Segoe UI', 'System', sans-serif;
                -fx-background-color: #f8fafc;
            }
            .tab-pane {
                -fx-background-color: transparent;
                -fx-tab-min-width: 120px;
                -fx-tab-min-height: 50px;
            }
            .tab-pane .tab-header-area {
                -fx-background-color: transparent;
                -fx-padding: 0 0 0 20px;
            }
            .tab-pane .tab-header-background {
                -fx-background-color: #f1f5f9;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 2);
            }
            .tab {
                -fx-background-color: transparent;
                -fx-background-insets: 0;
                -fx-background-radius: 8px 8px 0 0;
                -fx-padding: 10px 20px;
                -fx-border-width: 0;
            }
            .tab:selected {
                -fx-background-color: #3b82f6;
                -fx-text-fill: white;
                -fx-font-weight: bold;
            }
            .tab .tab-label {
                -fx-text-fill: #64748b;
                -fx-font-size: 14px;
                -fx-font-weight: 600;
            }
            .tab:selected .tab-label {
                -fx-text-fill: white;
            }
            .button {
                -fx-background-radius: 8px;
                -fx-padding: 10px 20px;
                -fx-font-weight: 600;
                -fx-cursor: hand;
                -fx-border-width: 0;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);
            }
            .button:hover {
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 3);
            }
            .text-field, .combo-box, .text-area {
                -fx-background-radius: 8px;
                -fx-border-radius: 8px;
                -fx-border-width: 1px;
                -fx-border-color: #cbd5e1;
                -fx-padding: 10px;
                -fx-font-size: 14px;
            }
            .text-field:focused, .combo-box:focused, .text-area:focused {
                -fx-border-color: #3b82f6;
                -fx-effect: dropshadow(gaussian, rgba(59,130,246,0.2), 5, 0, 0, 0);
            }
            .list-view {
                -fx-background-color: white;
                -fx-background-radius: 8px;
                -fx-border-radius: 8px;
                -fx-border-width: 1px;
                -fx-border-color: #e2e8f0;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 1);
            }
            .list-view .list-cell {
                -fx-padding: 12px;
                -fx-border-width: 0;
                -fx-background-color: transparent;
            }
            .list-view .list-cell:even {
                -fx-background-color: #f8fafc;
            }
            .list-view .list-cell:odd {
                -fx-background-color: white;
            }
            .list-view .list-cell:selected {
                -fx-background-color: #3b82f6;
                -fx-text-fill: white;
            }
            .list-view .list-cell:hover {
                -fx-background-color: #f1f5f9;
            }
            .list-view .list-cell:selected:hover {
                -fx-background-color: #2563eb;
            }
            .label {
                -fx-text-fill: #1e293b;
            }
            .scroll-bar:horizontal, .scroll-bar:vertical {
                -fx-background-color: transparent;
            }
            .scroll-bar:horizontal .track, .scroll-bar:vertical .track {
                -fx-background-color: #f1f5f9;
                -fx-border-color: transparent;
                -fx-background-radius: 4px;
            }
            .scroll-bar:horizontal .thumb, .scroll-bar:vertical .thumb {
                -fx-background-color: #cbd5e1;
                -fx-background-radius: 4px;
            }
            .scroll-bar:horizontal .thumb:hover, .scroll-bar:vertical .thumb:hover {
                -fx-background-color: #94a3b8;
            }
            .chart {
                -fx-background-color: transparent;
            }
            .chart-plot-background {
                -fx-background-color: transparent;
            }
            .chart-title {
                -fx-text-fill: #1e293b;
                -fx-font-size: 16px;
                -fx-font-weight: bold;
            }
            .axis-label {
                -fx-text-fill: #475569;
                -fx-font-size: 12px;
                -fx-font-weight: 600;
            }
            .axis {
                -fx-tick-label-fill: #64748b;
                -fx-tick-length: 5px;
                -fx-minor-tick-length: 3px;
            }
            .chart-legend {
                -fx-background-color: transparent;
                -fx-border-color: #e2e8f0;
                -fx-border-radius: 8px;
            }
            .chart-legend-item {
                -fx-text-fill: #475569;
                -fx-font-size: 12px;
            }
            .default-color0.chart-bar { -fx-bar-fill: #3b82f6; }
            .default-color1.chart-bar { -fx-bar-fill: #10b981; }
            .default-color2.chart-bar { -fx-bar-fill: #f59e0b; }
            .default-color0.chart-pie { -fx-pie-color: #3b82f6; }
            .default-color1.chart-pie { -fx-pie-color: #10b981; }
            .default-color2.chart-pie { -fx-pie-color: #f59e0b; }
            .default-color3.chart-pie { -fx-pie-color: #ef4444; }
            .dialog-pane {
                -fx-background-color: white;
                -fx-background-radius: 12px;
            }
            .dialog-pane .header-panel {
                -fx-background-color: #f8fafc;
            }
            .dialog-pane .button {
                -fx-background-color: #3b82f6;
                -fx-text-fill: white;
            }
            """;
    }
} 