package com.cybersek.gestocks;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ResourceBundle;

import com.cybersek.gestocks.StructureDonnee.CommandeDetails;
import com.cybersek.gestocks.StructureDonnee.InfoEncaissement;
import com.cybersek.gestocks.StructureDonnee.InventaireStocks;
import com.cybersek.gestocks.StructureDonnee.SessionVente;
import com.cybersek.gestocks.StructureDonnee.TableauBilanAnnuel;
import com.cybersek.gestocks.StructureDonnee.TableauBilanJournalier;
import com.cybersek.gestocks.StructureDonnee.TableauBilanMensuel;
import com.cybersek.gestocks.ModuleStocks.Inventaire;
import com.cybersek.gestocks.ModuleVente.BilanVente;
import com.cybersek.gestocks.ModuleVente.Commande;
import com.cybersek.gestocks.ModuleVente.Encaissement;
import com.cybersek.gestocks.ModuleVente.Session;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.Duration;

public class FenetreVenteProduitControleur {

    public static long ID_VENTE = -1;

    public static void setID_VENTE(long id_vente) {
        ID_VENTE = id_vente;
    }

    public static long getID_VENTE() {
        return ID_VENTE;
    }

    @FXML
    private TextField champ_nom_produit;

    @FXML
    private Text label_montant_total;

    @FXML
    private Button bouton_session_vente;

    @FXML
    private Text label_session_vente;

    @FXML
    private TableColumn<InventaireStocks, Void> colonne_actions;

    @FXML
    private TableColumn<InventaireStocks, String> colonne_nom_produit;

    @FXML
    private TableColumn<InventaireStocks, Integer> colonne_quantite_dispo;

    @FXML
    private TableColumn<InventaireStocks, String> colonne_unite;

    @FXML
    private TableView<InventaireStocks> tableau_produit;

    /// Tableau Commande
    @FXML
    private TableColumn<CommandeDetails, BigDecimal> colonne_prix_total_commande;

    @FXML
    private TableColumn<CommandeDetails, BigDecimal> colonne_prix_unitaire_produit_commande;

    @FXML
    private TableColumn<CommandeDetails, Integer> colonne_quantite_commande;

    @FXML
    private TableColumn<CommandeDetails, String> colonnne_nom_commande;

    @FXML
    private TableColumn<CommandeDetails, Void> colonne_action_commande;

    @FXML
    private TableView<CommandeDetails> tableau_commande;

    // Tableau Encaissement à valider
    @FXML
    private TableView<InfoEncaissement> tableau_encaissement;
    @FXML
    private TableColumn<InfoEncaissement, Number> colonne_IdVente_encaissement;
    @FXML
    private TableColumn<InfoEncaissement, String> colonne_date_encaissement;
    @FXML
    private TableColumn<InfoEncaissement, String> colonne_heure_encaissement;
    @FXML
    private TableColumn<InfoEncaissement, String> colonne_commandeclient_encaissement;
    @FXML
    private TableColumn<InfoEncaissement, BigDecimal> colonne_montant_encaissement;
    @FXML
    private TableColumn<InfoEncaissement, String> colonne_statut_encaissement;
    @FXML
    private TableColumn<InfoEncaissement, Void> colonne_action_encaissement;

    ////////////////////////////// Section Bilan
    @FXML
    private TextField champ_date_bilan_journalier;
    @FXML
    private ChoiceBox<Integer> choicebox_annee_bilan_mensuel;

    @FXML
    private TableView<TableauBilanJournalier> tabbleau_bilan_journalier;
    @FXML
    private TableColumn<TableauBilanJournalier, LocalDate> colonne_date_bilan_journalier;
    @FXML
    private TableColumn<TableauBilanJournalier, BigDecimal> colonne_ca_bilan_journalier;
    @FXML
    private TableColumn<TableauBilanJournalier, Void> colonne_actions_bilan_journalier;

    @FXML
    private TableView<TableauBilanMensuel> tableau_bilan_mensuel;
    @FXML
    private TableColumn<TableauBilanMensuel, String> colonne_mois_bilan_mensuel;
    @FXML
    private TableColumn<TableauBilanMensuel, BigDecimal> colonne_ca_bilan_mensuel;
    @FXML
    private TableColumn<TableauBilanMensuel, Void> colonne_actions_bilan_mensuel;

    @FXML
    private TableView<TableauBilanAnnuel> tableau_bilan_annuel;
    @FXML
    private TableColumn<TableauBilanAnnuel, Integer> colonne_annee_bilan_annuel;
    @FXML
    private TableColumn<TableauBilanAnnuel, BigDecimal> colonne_ca_bilan_annuel;
    @FXML
    private TableColumn<TableauBilanAnnuel, Void> colonne_actions_bilan_annuel;

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    private ObservableList<InventaireStocks> listeStockComplet = FXCollections.observableArrayList();
    private FilteredList<InventaireStocks> listeFiltreeStock;

    private ObservableList<CommandeDetails> listeCommande = FXCollections.observableArrayList();
    private ObservableList<InfoEncaissement> listeEncaissement = FXCollections.observableArrayList();
    private ObservableList<InfoEncaissement> ReloadlisteEncaissement = FXCollections.observableArrayList();

    // Objet représentant la session active ou courante gérée à l'écran
    private SessionVente sessionCourante;

    @FXML
    public void initialize() {
        label_montant_total.setText("0.00 Franc CFA");
        action_bouton_vente_produit();
        action_bouton_commande_produit();
        action_bouton_encaissement_vente();
        action_bouton_bilan_journalier();
        InitTableauStock();
        InitTableauCommande();
        InitTableauEncaissement();
        InitTableauBilanJournalier();
        InitTableauBilanMensuel();
        InitTableauBilanAnnuel();
        configurerRechercheDynamique();
        InitSession();

        // --- Rafraîchissement automatique de l'encaissement toutes les 15 secondes ---
        Timeline timelineEncaissement = new Timeline(new KeyFrame(Duration.seconds(15), event -> {
            InitTableauEncaissement();
        }));
        timelineEncaissement.setCycleCount(Timeline.INDEFINITE); // Répétition infinie
        timelineEncaissement.play();

    }

    private void InitTableauStock() {
        colonne_nom_produit.setCellValueFactory(new PropertyValueFactory<>("nomProduit"));
        colonne_quantite_dispo.setCellValueFactory(new PropertyValueFactory<>("stockTotal"));
        colonne_unite.setCellValueFactory(new PropertyValueFactory<>("unite"));
        listeStockComplet = Inventaire.listerInventaire();
        listeFiltreeStock = new FilteredList<>(listeStockComplet, p -> true);
        tableau_produit.setItems(listeFiltreeStock);
        champ_nom_produit.textProperty().addListener((observable, oldValue, newValue) -> {
            listeFiltreeStock.setPredicate(item -> {
                if (newValue == null || newValue.trim().isEmpty()) {
                    return true;
                }
                String saisie = newValue.toLowerCase().trim();
                return item.getNomProduit() != null && item.getNomProduit().toLowerCase().contains(saisie);
            });
        });
    }

    private void InitTableauCommande() {
        colonnne_nom_commande.setCellValueFactory(new PropertyValueFactory<>("LibelleProduit"));
        colonne_quantite_commande.setCellValueFactory(new PropertyValueFactory<>("Quantite"));
        colonne_prix_unitaire_produit_commande.setCellValueFactory(new PropertyValueFactory<>("PrixUnitaire"));
        colonne_prix_total_commande.setCellValueFactory(new PropertyValueFactory<>("CoutTotal"));
        listeCommande = Commande.listerCommandesParVente(ID_VENTE);
        tableau_commande.setItems(listeCommande);
    }

    public void InitTableauEncaissement() {
        if (colonne_IdVente_encaissement.getCellValueFactory() == null) {
            colonne_IdVente_encaissement.setCellValueFactory(cellData -> cellData.getValue().idVenteProperty());
            colonne_date_encaissement.setCellValueFactory(cellData -> cellData.getValue().dateVenteProperty());
            colonne_heure_encaissement.setCellValueFactory(cellData -> cellData.getValue().heureVenteProperty());
            colonne_commandeclient_encaissement
                    .setCellValueFactory(cellData -> cellData.getValue().commandeClientProperty());
            colonne_montant_encaissement.setCellValueFactory(cellData -> cellData.getValue().montantTotalProperty());
            colonne_statut_encaissement
                    .setCellValueFactory(cellData -> cellData.getValue().statutTransactionProperty());

            listeEncaissement = Encaissement.ListerTableauPourEncaissement();
            tableau_encaissement.setItems(listeEncaissement);
        } else {
            ReloadlisteEncaissement = Encaissement.ListerTableauPourEncaissement();
            tableau_encaissement.setItems(ReloadlisteEncaissement);
        }
    }

    public void InitTableauBilanJournalier() {
        // Formatage de la colonne date en jj/mm/aaaa
        colonne_date_bilan_journalier.setCellValueFactory(new PropertyValueFactory<>("date"));
        colonne_date_bilan_journalier
                .setCellFactory(column -> new TableCell<TableauBilanJournalier, LocalDate>() {
                    private final java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter
                            .ofPattern("dd/MM/yyyy");

                    @Override
                    protected void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);
                        if (empty || date == null) {
                            setText(null);
                        } else {
                            setText(formatter.format(date));
                        }
                    }
                });

        colonne_ca_bilan_journalier.setCellValueFactory(new PropertyValueFactory<>("chiffreAffaires"));

        // Chargement initial
        tabbleau_bilan_journalier.setItems(BilanVente.listerBilanJournalier(null));

        // Filtrage dynamique lors de la saisie
        champ_date_bilan_journalier.textProperty().addListener((observable, oldValue, newValue) -> {
            tabbleau_bilan_journalier.setItems(BilanVente.listerBilanJournalier(newValue));
        });
    }

    public void InitTableauBilanMensuel() {
        colonne_mois_bilan_mensuel.setCellValueFactory(new PropertyValueFactory<>("mois"));
        colonne_ca_bilan_mensuel.setCellValueFactory(new PropertyValueFactory<>("chiffreAffaires"));
        // 4. Initialisation de la ChoiceBox des années pour le Bilan Mensuel
        ObservableList<Integer> anneesDisponibles = BilanVente.listerAnneesDisponibles();
        choicebox_annee_bilan_mensuel.setItems(anneesDisponibles);
        int anneeCourante = LocalDate.now().getYear();
        if (anneesDisponibles.contains(anneeCourante)) {
            choicebox_annee_bilan_mensuel.setValue(anneeCourante);
        } else if (!anneesDisponibles.isEmpty()) {
            choicebox_annee_bilan_mensuel.setValue(anneesDisponibles.get(0)); // Première année dispo par défaut
        }

    }

    public void InitTableauBilanAnnuel() {
        colonne_annee_bilan_annuel.setCellValueFactory(new PropertyValueFactory<>("annee"));
        colonne_ca_bilan_annuel.setCellValueFactory(new PropertyValueFactory<>("chiffreAffaires"));
        tableau_bilan_annuel.setItems(BilanVente.listerBilanAnnuel());
        // Charger le bilan mensuel de l'année sélectionnée par défaut
        if (choicebox_annee_bilan_mensuel.getValue() != null) {
            tableau_bilan_mensuel.setItems(BilanVente.listerBilanMensuel(choicebox_annee_bilan_mensuel.getValue()));
        }

    }

    private void configurerRechercheDynamique() {
        // Écoute les changements de texte dans le TextField en temps réel
        // 5. Écouteur pour recharger le bilan mensuel si l'utilisateur change l'année
        // dans la ChoiceBox
        choicebox_annee_bilan_mensuel.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                tableau_bilan_mensuel.setItems(BilanVente.listerBilanMensuel(newVal));
            }
        });
    }

    public void InitSession() {
        // 1. Récupérer la session en cours (IN_PROGRESS) ou la prochaine session
        // (UNUSED)
        sessionCourante = Session.recupererSessionActuelleOuSuivante();

        if (sessionCourante != null) {
            if ("IN_PROGRESS".equals(sessionCourante.getEtat())) {
                // Cas : Une session est active
                label_session_vente.setText("En Cours : " + sessionCourante.getDescriptionSession());
                bouton_session_vente.setText("Arrêter Session");
                bouton_session_vente.setDisable(false);
            } else if ("UNUSED".equals(sessionCourante.getEtat())) {
                // Cas : Session prête à être lancée
                label_session_vente.setText(sessionCourante.getDescriptionSession());
                bouton_session_vente.setText("Lancer Session");
                bouton_session_vente.setDisable(false);
            }
        } else {
            // Cas : Aucune session disponible (ex: fin de journée ou oubli de génération)
            label_session_vente.setText("Aucune session disponible pour le moment.");
            bouton_session_vente.setText("Lancer Session");
            bouton_session_vente.setDisable(true);
        }
    }

    @FXML
    void Annuler_Commande_CLient(ActionEvent event) {
        boolean statut = Commande.annulerVenteEntiere(ID_VENTE);
        if (statut) {
            Functions.Alerte_succes("Vente Annulé");
            ID_VENTE = -1;
            initialize();
        } else {
            Functions.Alerte_erreur("Erreur lors de l'annnulation de la vente");
        }
    }

    @FXML
    void Valider_Commande_Client(ActionEvent event) {
        boolean statut = Commande.ValiderCommandeVente(ID_VENTE);
        if (statut) {
            Functions.Alerte_succes(" Commande " + ID_VENTE + " Validé");
            ID_VENTE = -1;
        } else {
            Functions.Alerte_erreur("Commande" + ID_VENTE + " non validé");
        }
        initialize();
    }

    @FXML
    void GestionSession(ActionEvent event) {
        if (sessionCourante == null)
            return;

        String etatActuel = sessionCourante.getEtat();

        if ("UNUSED".equals(etatActuel)) {
            // ACTION : Lancer la session
            // TODO : Récupérer l'ID de l'utilisateur connecte (ex:
            // App.utilisateurConnecteId)
            int userId = NomUser.getIdUtilisateur();
            boolean success = Session.changerEtatSession(sessionCourante.getId(), "IN_PROGRESS", userId);

            if (success) {
                // Mettre à jour l'interface dynamiquement
                sessionCourante.setEtat("IN_PROGRESS");
                label_session_vente.setText("Session En Cours : " + sessionCourante.getDescriptionSession());
                bouton_session_vente.setText("Arrêter Session");
            }

        } else if ("IN_PROGRESS".equals(etatActuel)) {
            // ACTION : Arrêter / Clôturer la session
            int userId = NomUser.getIdUtilisateur();
            boolean success = Session.changerEtatSession(sessionCourante.getId(), "CLOSED", userId);

            if (success) {
                // Mettre à jour l'interface dynamiquement
                sessionCourante.setEtat("CLOSED");
                label_session_vente.setText("Session terminée. Prêt pour la suivante.");
                bouton_session_vente.setText("Lancer Session");
                // Relancer l'initialisation pour charger la session suivante (UNUSED)
                InitSession();
            }
        }
    }

    /// bouton action tableau stocks
    private static Stage BoiteDialogueAjouterProduitCommande;

    public static void FermerBoiteDialogueAjouterProduitCommande() {
        BoiteDialogueAjouterProduitCommande.close();
    }

    private void action_bouton_vente_produit() {
        Callback<TableColumn<InventaireStocks, Void>, TableCell<InventaireStocks, Void>> cellFactory = new Callback<TableColumn<InventaireStocks, Void>, TableCell<InventaireStocks, Void>>() {
            @Override
            public TableCell<InventaireStocks, Void> call(final TableColumn<InventaireStocks, Void> param) {
                final TableCell<InventaireStocks, Void> cell = new TableCell<InventaireStocks, Void>() {
                    // Initialisation des boutons
                    private final Button boutonAjouterProduit = new Button("Ajouter à la commande");
                    private final Button boutonModifierProduit = new Button("Modifier");
                    private final Button boutonInfosProduit = new Button("Informations");

                    // Styles CSS pour les boutons (Harmonisés avec votre charte)
                    private final String baseStyle = "-fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 6 12; -fx-cursor: hand; -fx-font-size: 12px;";
                    private final String styleVert = baseStyle + " -fx-background-color: #4CAF50;"; // Appro
                    private final String styleOrange = baseStyle + " -fx-background-color: #FF9800;"; // Modifier
                    private final String styleCyan = baseStyle + " -fx-background-color: #00BCD4;"; // Infos

                    {
                        // Application des styles de couleur initiaux
                        boutonAjouterProduit.setStyle(styleVert);
                        boutonModifierProduit.setStyle(styleOrange);
                        boutonInfosProduit.setStyle(styleCyan);

                        // --- Événements au survol (Effet Hover) ---
                        boutonAjouterProduit.setOnMouseEntered(e -> boutonAjouterProduit
                                .setStyle(baseStyle + " -fx-background-color: #43A047;"));
                        boutonAjouterProduit
                                .setOnMouseExited(e -> boutonAjouterProduit.setStyle(styleVert));

                        // --- Action Ajouter Produit ---
                        boutonAjouterProduit.setOnAction(event -> {
                            InventaireStocks data = getTableView().getItems().get(getIndex());
                            if (data.getStockTotal() == 0) {
                                Functions.Alerte_erreur("Stocks Vide");
                                return;
                            }
                            if (data.getStockTotal() <= 10) {
                                Functions.Alerte_erreur("Penser à Approvisionner le Stock de " + data.getNomProduit());
                            }
                            try {
                                FXMLLoader loader = new FXMLLoader(
                                        getClass().getResource("BoiteDialogueAjouterProduitCommande.fxml"));
                                Parent root = loader.load();
                                root.setStyle("-fx-background-color: white; -fx-background-radius: 15;");
                                root.setEffect(Functions.ArrierePlan());

                                StackPane contenaire = new StackPane(root);
                                contenaire.setStyle("-fx-background-color: white; -fx-background-radius: 20;");
                                contenaire.setPadding(new Insets(20));

                                BoiteDialogueAjouterProduitCommandeControleur controleur = loader.getController();
                                controleur.chargerDonnees(data.getIdProduit());

                                BoiteDialogueAjouterProduitCommande = new Stage();
                                BoiteDialogueAjouterProduitCommande.setResizable(false);
                                BoiteDialogueAjouterProduitCommande.initModality(Modality.APPLICATION_MODAL);
                                BoiteDialogueAjouterProduitCommande
                                        .initOwner(((javafx.scene.Node) event.getSource()).getScene().getWindow());
                                BoiteDialogueAjouterProduitCommande.initStyle(StageStyle.TRANSPARENT);

                                Scene scene = new Scene(contenaire);
                                scene.setFill(Color.TRANSPARENT);
                                BoiteDialogueAjouterProduitCommande.setScene(scene);
                                BoiteDialogueAjouterProduitCommande.showAndWait();
                            } catch (IOException erreur) {
                                System.err.println(erreur.getMessage());
                            }
                            InitTableauStock();
                            tableau_commande.setItems(Commande.listerCommandesParVente(ID_VENTE));
                            label_montant_total.setText(
                                    String.valueOf(Commande.calculerMontantTotalCommande(ID_VENTE)) + " Franc CFA");
                        });

                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                            setGraphic(null);
                        } else {
                            HBox box = new HBox(6, boutonAjouterProduit);
                            box.setAlignment(Pos.CENTER);
                            setGraphic(box);
                        }
                    }
                };
                return cell;
            }
        };

        colonne_actions.setCellFactory(cellFactory);
    }

    // bouton actions tableau commande
    private void action_bouton_commande_produit() {
        Callback<TableColumn<CommandeDetails, Void>, TableCell<CommandeDetails, Void>> cellFactory = new Callback<TableColumn<CommandeDetails, Void>, TableCell<CommandeDetails, Void>>() {
            @Override
            public TableCell<CommandeDetails, Void> call(final TableColumn<CommandeDetails, Void> param) {
                final TableCell<CommandeDetails, Void> cell = new TableCell<CommandeDetails, Void>() {
                    // Initialisation des boutons
                    private final Button boutonRetirerProduit = new Button("Retirer");

                    // Styles CSS pour les boutons (Harmonisés avec votre charte)
                    private final String baseStyle = "-fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 6 12; -fx-cursor: hand; -fx-font-size: 12px;";
                    private final String styleVert = baseStyle + " -fx-background-color: #f44336 ;";
                    {
                        // Application des styles de couleur initiaux
                        boutonRetirerProduit.setStyle(styleVert);

                        // --- Événements au survol (Effet Hover) ---
                        boutonRetirerProduit.setOnMouseEntered(e -> boutonRetirerProduit
                                .setStyle(baseStyle + " -fx-background-color: #F40B0B;"));
                        boutonRetirerProduit
                                .setOnMouseExited(e -> boutonRetirerProduit.setStyle(styleVert));

                        // --- Action Ajouter Produit ---
                        boutonRetirerProduit.setOnAction(event -> {
                            CommandeDetails data = getTableView().getItems().get(getIndex());
                            Commande.retirerProduitCommande(data.getIdCommande());
                            initialize();
                            label_montant_total.setText(
                                    String.valueOf(Commande.calculerMontantTotalCommande(ID_VENTE)) + " Franc CFA");
                        });

                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                            setGraphic(null);
                        } else {
                            HBox box = new HBox(6, boutonRetirerProduit);
                            box.setAlignment(Pos.CENTER);
                            setGraphic(box);
                        }
                    }
                };
                return cell;
            }
        };

        colonne_action_commande.setCellFactory(cellFactory);
    }

    // boutons action tableau encaissement

    private static Stage BoiteDialogueEncaissementVente;

    public static void FermerBoiteDialogueEncaissementVente() {
        BoiteDialogueEncaissementVente.close();
    }

    private void action_bouton_encaissement_vente() {
        Callback<TableColumn<InfoEncaissement, Void>, TableCell<InfoEncaissement, Void>> cellFactory = new Callback<TableColumn<InfoEncaissement, Void>, TableCell<InfoEncaissement, Void>>() {
            @Override
            public TableCell<InfoEncaissement, Void> call(final TableColumn<InfoEncaissement, Void> param) {
                final TableCell<InfoEncaissement, Void> cell = new TableCell<InfoEncaissement, Void>() {
                    // Initialisation des boutons
                    private final Button boutonEncaissement = new Button("Faire Encaissement");
                    private final Button boutonAnnulerEncaissement = new Button("Annuler Encaissement");

                    // Styles CSS pour les boutons (Harmonisés avec votre charte)
                    private final String baseStyle = "-fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 6 12; -fx-cursor: hand; -fx-font-size: 12px;";
                    private final String styleRouge = baseStyle + " -fx-background-color: #f44336 ;";
                    private final String styleVert = baseStyle + " -fx-background-color: #4CAF50;";

                    {
                        // Centrer la cellule dans la colonne du TableView
                        setAlignment(Pos.CENTER); // <--- LIGNE AJOUTÉE ICI

                        // Application des styles de couleur initiaux
                        boutonAnnulerEncaissement.setStyle(styleRouge);
                        boutonEncaissement.setStyle(styleVert);

                        // --- Événements au survol (Effet Hover) ---
                        boutonAnnulerEncaissement.setOnMouseEntered(e -> boutonAnnulerEncaissement
                                .setStyle(baseStyle + " -fx-background-color: #F40B0B;"));
                        boutonAnnulerEncaissement
                                .setOnMouseExited(e -> boutonAnnulerEncaissement.setStyle(styleRouge));

                        boutonEncaissement.setOnMouseEntered(e -> boutonEncaissement
                                .setStyle(baseStyle + " -fx-background-color: #43A047;"));
                        boutonEncaissement
                                .setOnMouseExited(e -> boutonEncaissement.setStyle(styleVert));

                        // --- Action Annuler Encaissement ---
                        boutonAnnulerEncaissement.setOnAction(event -> {
                            InfoEncaissement data = getTableView().getItems().get(getIndex());
                            boolean statut = Encaissement.AnnulerEncaissement(data.getIdVente());
                            if (statut) {
                                Functions.Alerte_succes("Vente " + data.getIdVente() + " annulée avec succès");
                            } else {
                                Functions.Alerte_erreur("Erreur rencontrée lors de l'annulation");
                            }
                            initialize();
                        });

                        // --- Action Faire Encaissement ---
                        boutonEncaissement.setOnAction(event -> {
                            InfoEncaissement data = getTableView().getItems().get(getIndex());
                            try {
                                FXMLLoader loader = new FXMLLoader(
                                        getClass().getResource("BoiteDialogueEncaissementVente.fxml"));
                                Parent root = loader.load();
                                root.setStyle("-fx-background-color: white; -fx-background-radius: 15;");
                                root.setEffect(Functions.ArrierePlan());

                                StackPane contenaire = new StackPane(root);
                                contenaire.setStyle("-fx-background-color: white; -fx-background-radius: 20;");
                                contenaire.setPadding(new Insets(20));

                                BoiteDialogueEncaissementVenteControleur controleur = loader.getController();
                                controleur.chargerDonnees(data.getIdVente());

                                // FIX : Affectation directe à la variable globale (sans le type "Stage")
                                BoiteDialogueEncaissementVente = new Stage();
                                BoiteDialogueEncaissementVente.setResizable(false);
                                BoiteDialogueEncaissementVente.initModality(Modality.APPLICATION_MODAL);
                                BoiteDialogueEncaissementVente
                                        .initOwner(((javafx.scene.Node) event.getSource()).getScene().getWindow());
                                BoiteDialogueEncaissementVente.initStyle(StageStyle.TRANSPARENT);

                                Scene scene = new Scene(contenaire);
                                scene.setFill(Color.TRANSPARENT);
                                BoiteDialogueEncaissementVente.setScene(scene);
                                BoiteDialogueEncaissementVente.showAndWait();
                            } catch (IOException erreur) {
                                System.err.println(erreur.getMessage());
                            }
                            initialize();
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                            setGraphic(null);
                        } else {
                            // Espacement de 8 pixels entre les deux boutons et centrage du HBox
                            HBox box = new HBox(8, boutonEncaissement, boutonAnnulerEncaissement);
                            box.setAlignment(Pos.CENTER);
                            setGraphic(box);
                        }
                    }
                };
                return cell;
            }
        };

        colonne_action_encaissement.setCellFactory(cellFactory);
    }

    private static Stage BoiteDialogueBilanJournalierS1;

    public static void FermeBoiteDialogueBilanJournalierS1() {
        BoiteDialogueBilanJournalierS1.close();
    }

    private static Stage BoiteDialogueBilanJournalierS2;

    public static void FermerBoiteDialogueBilanJournalierS2() {
        BoiteDialogueBilanJournalierS2.close();
    }

    private static Stage BoiteDialogueBilanJournalierS3;

    public static void FermerBoiteDialogueBilanJournalierS3() {
        BoiteDialogueBilanJournalierS3.close();
    }

    private void action_bouton_bilan_journalier() {
        Callback<TableColumn<TableauBilanJournalier, Void>, TableCell<TableauBilanJournalier, Void>> cellFactory = new Callback<TableColumn<TableauBilanJournalier, Void>, TableCell<TableauBilanJournalier, Void>>() {
            @Override
            public TableCell<TableauBilanJournalier, Void> call(final TableColumn<TableauBilanJournalier, Void> param) {
                final TableCell<TableauBilanJournalier, Void> cell = new TableCell<TableauBilanJournalier, Void>() {
                    // Initialisation des boutons
                    private final Button boutonSession1 = new Button("Bilan Session 08-14");
                    private final Button boutonSession2 = new Button("Bilan Session 14-20");
                    private final Button boutonSession3 = new Button("Bilan Session 20-08");
                    private final Button boutonBilanComplet = new Button("Bilan Journalier Complet");

                    // Styles CSS pour les boutons (Harmonisés avec votre charte)
                    private final String baseStyle = "-fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 6 12; -fx-cursor: hand; -fx-font-size: 12px;";
                    private final String styleBleue = baseStyle + " -fx-background-color: #2d7dd2 ;";

                    {
                        // Centrer la cellule dans la colonne du TableView
                        setAlignment(Pos.CENTER); // <--- LIGNE AJOUTÉE ICI

                        // Application des styles de couleur initiaux
                        boutonSession1.setStyle(styleBleue);
                        boutonSession2.setStyle(styleBleue);
                        boutonSession3.setStyle(styleBleue);
                        boutonBilanComplet.setStyle(styleBleue);

                        // --- Événements au survol (Effet Hover) ---
                        boutonSession1.setOnMouseEntered(e -> boutonSession1
                                .setStyle(baseStyle + " -fx-background-color: #1a5bb8;"));
                        boutonSession1
                                .setOnMouseExited(e -> boutonSession1.setStyle(styleBleue));

                        boutonSession2.setOnMouseEntered(e -> boutonSession2
                                .setStyle(baseStyle + " -fx-background-color: #1a5bb8;"));
                        boutonSession2
                                .setOnMouseExited(e -> boutonSession2.setStyle(styleBleue));

                        boutonSession3.setOnMouseEntered(e -> boutonSession3
                                .setStyle(baseStyle + " -fx-background-color: #1a5bb8;"));
                        boutonSession3
                                .setOnMouseExited(e -> boutonSession3.setStyle(styleBleue));

                        boutonBilanComplet.setOnMouseEntered(e -> boutonBilanComplet
                                .setStyle(baseStyle + " -fx-background-color: #1a5bb8;"));
                        boutonBilanComplet
                                .setOnMouseExited(e -> boutonBilanComplet.setStyle(styleBleue));

                        // --- Actions
                        boutonSession1.setOnAction(event -> {
                            TableauBilanJournalier data = getTableView().getItems().get(getIndex());
                            try {
                                FXMLLoader loader = new FXMLLoader(
                                        getClass().getResource("BoiteDialogueApprovisionnementProduit.fxml"));
                                Parent root = loader.load();
                                root.setStyle("-fx-background-color: white; -fx-background-radius: 15;");
                                root.setEffect(Functions.ArrierePlan());

                                StackPane contenaire = new StackPane(root);
                                contenaire.setStyle("-fx-background-color: white; -fx-background-radius: 20;");
                                contenaire.setPadding(new Insets(20));

                                BoiteDialogueBilanJournalierS1Controleur controleur = loader.getController();
                                // controleur.chargerDonnees(data.getId());

                                BoiteDialogueBilanJournalierS1 = new Stage();
                                BoiteDialogueBilanJournalierS1.setResizable(false);
                                BoiteDialogueBilanJournalierS1.initModality(Modality.APPLICATION_MODAL);
                                BoiteDialogueBilanJournalierS1
                                        .initOwner(((javafx.scene.Node) event.getSource()).getScene().getWindow());
                                BoiteDialogueBilanJournalierS1.initStyle(StageStyle.TRANSPARENT);

                                Scene scene = new Scene(contenaire);
                                scene.setFill(Color.TRANSPARENT);
                                BoiteDialogueBilanJournalierS1.setScene(scene);
                                BoiteDialogueBilanJournalierS1.showAndWait();
                            } catch (IOException erreur) {
                                System.err.println(erreur.getMessage());
                            }

                        });

                        boutonSession2.setOnAction(event -> {
                            TableauBilanJournalier data = getTableView().getItems().get(getIndex());
                            try {
                                FXMLLoader loader = new FXMLLoader(
                                        getClass().getResource("BoiteDialogueBilanJournalierS2.fxml"));
                                Parent root = loader.load();
                                root.setStyle("-fx-background-color: white; -fx-background-radius: 15;");
                                root.setEffect(Functions.ArrierePlan());

                                StackPane contenaire = new StackPane(root);
                                contenaire.setStyle("-fx-background-color: white; -fx-background-radius: 20;");
                                contenaire.setPadding(new Insets(20));

                                BoiteDialogueBilanJournalierS2Controleur controleur = loader.getController();
                                // controleur.chargerDonnees(data.getIdProduit());

                                BoiteDialogueBilanJournalierS2 = new Stage();
                                BoiteDialogueBilanJournalierS2.setResizable(false);
                                BoiteDialogueBilanJournalierS2.initModality(Modality.APPLICATION_MODAL);
                                BoiteDialogueBilanJournalierS2
                                        .initOwner(((javafx.scene.Node) event.getSource()).getScene().getWindow());
                                BoiteDialogueBilanJournalierS1.initStyle(StageStyle.TRANSPARENT);

                                Scene scene = new Scene(contenaire);
                                scene.setFill(Color.TRANSPARENT);
                                BoiteDialogueBilanJournalierS2.setScene(scene);
                                BoiteDialogueBilanJournalierS2.showAndWait();
                            } catch (IOException erreur) {
                                System.err.println(erreur.getMessage());
                            }

                        });

                        boutonSession3.setOnAction(event -> {
                            TableauBilanJournalier data = getTableView().getItems().get(getIndex());
                            try {
                                FXMLLoader loader = new FXMLLoader(
                                        getClass().getResource("BoiteDialogueBilanJournalierS3.fxml"));
                                Parent root = loader.load();
                                root.setStyle("-fx-background-color: white; -fx-background-radius: 15;");
                                root.setEffect(Functions.ArrierePlan());

                                StackPane contenaire = new StackPane(root);
                                contenaire.setStyle("-fx-background-color: white; -fx-background-radius: 20;");
                                contenaire.setPadding(new Insets(20));

                                BoiteDialogueBilanJournalierS3Controleur controleur = loader.getController();
                                // controleur.chargerDonnees(data.getIdProduit());

                                BoiteDialogueBilanJournalierS3 = new Stage();
                                BoiteDialogueBilanJournalierS3.setResizable(false);
                                BoiteDialogueBilanJournalierS3.initModality(Modality.APPLICATION_MODAL);
                                BoiteDialogueBilanJournalierS3
                                        .initOwner(((javafx.scene.Node) event.getSource()).getScene().getWindow());
                                BoiteDialogueBilanJournalierS1.initStyle(StageStyle.TRANSPARENT);

                                Scene scene = new Scene(contenaire);
                                scene.setFill(Color.TRANSPARENT);
                                BoiteDialogueBilanJournalierS3.setScene(scene);
                                BoiteDialogueBilanJournalierS3.showAndWait();
                            } catch (IOException erreur) {
                                System.err.println(erreur.getMessage());
                            }

                        });

                        boutonBilanComplet.setOnAction(event -> {
                            TableauBilanJournalier data = getTableView().getItems().get(getIndex());

                        });

                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                            setGraphic(null);
                        } else {
                            // Espacement de 8 pixels entre les deux boutons et centrage du HBox
                            HBox box = new HBox(8, boutonSession1, boutonSession2, boutonSession3, boutonBilanComplet);
                            box.setAlignment(Pos.CENTER);
                            setGraphic(box);
                        }
                    }
                };
                return cell;
            }
        };

        colonne_actions_bilan_journalier.setCellFactory(cellFactory);
    }

    /// Section bilan mensuel
    ///
    private static Stage BoiteDialogueBilanMensuel;

    public static void FermerBoiteDialogueBilanMensuel() {
        BoiteDialogueBilanMensuel.close();
    }

    private void action_bouton_bilan_mensuel() {
        Callback<TableColumn<TableauBilanMensuel, Void>, TableCell<TableauBilanMensuel, Void>> cellFactory = new Callback<TableColumn<TableauBilanMensuel, Void>, TableCell<TableauBilanMensuel, Void>>() {
            @Override
            public TableCell<TableauBilanMensuel, Void> call(final TableColumn<TableauBilanMensuel, Void> param) {
                final TableCell<TableauBilanMensuel, Void> cell = new TableCell<TableauBilanMensuel, Void>() {
                    // Initialisation des boutons
                    private final Button boutonDetail = new Button("Detail");
                    private final Button boutonImpressBilanComplet = new Button("Imprimmer Bilan");

                    // Styles CSS pour les boutons (Harmonisés avec votre charte)
                    private final String baseStyle = "-fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 6 12; -fx-cursor: hand; -fx-font-size: 12px;";
                    private final String styleBleue = baseStyle + " -fx-background-color: #2d7dd2 ;";

                    {
                        // Centrer la cellule dans la colonne du TableView
                        setAlignment(Pos.CENTER); // <--- LIGNE AJOUTÉE ICI

                        // Application des styles de couleur initiaux
                        boutonDetail.setStyle(styleBleue);
                        boutonImpressBilanComplet.setStyle(styleBleue);

                        // --- Événements au survol (Effet Hover) ---
                        boutonDetail.setOnMouseEntered(e -> boutonDetail
                                .setStyle(baseStyle + " -fx-background-color: #1a5bb8;"));
                        boutonDetail
                                .setOnMouseExited(e -> boutonDetail.setStyle(styleBleue));

                        boutonImpressBilanComplet.setOnMouseEntered(e -> boutonImpressBilanComplet
                                .setStyle(baseStyle + " -fx-background-color: #1a5bb8;"));
                        boutonImpressBilanComplet
                                .setOnMouseExited(e -> boutonImpressBilanComplet.setStyle(styleBleue));

                        // --- Actions
                        boutonDetail.setOnAction(event -> {
                            TableauBilanMensuel data = getTableView().getItems().get(getIndex());
                            try {
                                FXMLLoader loader = new FXMLLoader(
                                        getClass().getResource("BoiteDialogueBilanMensuel.fxml"));
                                Parent root = loader.load();
                                root.setStyle("-fx-background-color: white; -fx-background-radius: 15;");
                                root.setEffect(Functions.ArrierePlan());

                                StackPane contenaire = new StackPane(root);
                                contenaire.setStyle("-fx-background-color: white; -fx-background-radius: 20;");
                                contenaire.setPadding(new Insets(20));

                                BoiteDialogueBilanMensuelControleur controleur = loader.getController();
                                // controleur.chargerDonnees(data.getId());

                                BoiteDialogueBilanMensuel = new Stage();
                                BoiteDialogueBilanMensuel.setResizable(false);
                                BoiteDialogueBilanMensuel.initModality(Modality.APPLICATION_MODAL);
                                BoiteDialogueBilanMensuel
                                        .initOwner(((javafx.scene.Node) event.getSource()).getScene().getWindow());
                                BoiteDialogueBilanMensuel.initStyle(StageStyle.TRANSPARENT);

                                Scene scene = new Scene(contenaire);
                                scene.setFill(Color.TRANSPARENT);
                                BoiteDialogueBilanMensuel.setScene(scene);
                                BoiteDialogueBilanMensuel.showAndWait();
                            } catch (IOException erreur) {
                                System.err.println(erreur.getMessage());
                            }

                        });

                        boutonImpressBilanComplet.setOnAction(event -> {
                            TableauBilanMensuel data = getTableView().getItems().get(getIndex());

                        });

                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                            setGraphic(null);
                        } else {
                            // Espacement de 8 pixels entre les deux boutons et centrage du HBox
                            HBox box = new HBox(8, boutonDetail, boutonImpressBilanComplet);
                            box.setAlignment(Pos.CENTER);
                            setGraphic(box);
                        }
                    }
                };
                return cell;
            }
        };

        colonne_actions_bilan_mensuel.setCellFactory(cellFactory);
    }

    // Section bilan annuel
    //
    private static Stage BoiteDialogueBilanAnnuel;

    public static void FermerBoiteDialogueBilanAnnuel() {
        BoiteDialogueBilanAnnuel.close();
    }

    private void action_bouton_bilan_annuel() {
        Callback<TableColumn<TableauBilanAnnuel, Void>, TableCell<TableauBilanAnnuel, Void>> cellFactory = new Callback<TableColumn<TableauBilanAnnuel, Void>, TableCell<TableauBilanAnnuel, Void>>() {
            @Override
            public TableCell<TableauBilanAnnuel, Void> call(final TableColumn<TableauBilanAnnuel, Void> param) {
                final TableCell<TableauBilanAnnuel, Void> cell = new TableCell<TableauBilanAnnuel, Void>() {
                    // Initialisation des boutons
                    private final Button boutonDetail = new Button("Detail");
                    private final Button boutonImpressBilanComplet = new Button("Imprimmer Bilan");

                    // Styles CSS pour les boutons (Harmonisés avec votre charte)
                    private final String baseStyle = "-fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 6 12; -fx-cursor: hand; -fx-font-size: 12px;";
                    private final String styleBleue = baseStyle + " -fx-background-color: #2d7dd2 ;";

                    {
                        // Centrer la cellule dans la colonne du TableView
                        setAlignment(Pos.CENTER); // <--- LIGNE AJOUTÉE ICI

                        // Application des styles de couleur initiaux
                        boutonDetail.setStyle(styleBleue);
                        boutonImpressBilanComplet.setStyle(styleBleue);

                        // --- Événements au survol (Effet Hover) ---
                        boutonDetail.setOnMouseEntered(e -> boutonDetail
                                .setStyle(baseStyle + " -fx-background-color: #1a5bb8;"));
                        boutonDetail
                                .setOnMouseExited(e -> boutonDetail.setStyle(styleBleue));

                        boutonImpressBilanComplet.setOnMouseEntered(e -> boutonImpressBilanComplet
                                .setStyle(baseStyle + " -fx-background-color: #1a5bb8;"));
                        boutonImpressBilanComplet
                                .setOnMouseExited(e -> boutonImpressBilanComplet.setStyle(styleBleue));

                        // --- Actions
                        boutonDetail.setOnAction(event -> {
                            TableauBilanAnnuel data = getTableView().getItems().get(getIndex());
                            try {
                                FXMLLoader loader = new FXMLLoader(
                                        getClass().getResource("BoiteDialogueBilanAnnuel.fxml"));
                                Parent root = loader.load();
                                root.setStyle("-fx-background-color: white; -fx-background-radius: 15;");
                                root.setEffect(Functions.ArrierePlan());

                                StackPane contenaire = new StackPane(root);
                                contenaire.setStyle("-fx-background-color: white; -fx-background-radius: 20;");
                                contenaire.setPadding(new Insets(20));

                                BoiteDialogueBilanAnnuelControleur controleur = loader.getController();
                                // controleur.chargerDonnees(data.getId());

                                BoiteDialogueBilanAnnuel = new Stage();
                                BoiteDialogueBilanAnnuel.setResizable(false);
                                BoiteDialogueBilanAnnuel.initModality(Modality.APPLICATION_MODAL);
                                BoiteDialogueBilanAnnuel
                                        .initOwner(((javafx.scene.Node) event.getSource()).getScene().getWindow());
                                BoiteDialogueBilanAnnuel.initStyle(StageStyle.TRANSPARENT);

                                Scene scene = new Scene(contenaire);
                                scene.setFill(Color.TRANSPARENT);
                                BoiteDialogueBilanAnnuel.setScene(scene);
                                BoiteDialogueBilanAnnuel.showAndWait();
                            } catch (IOException erreur) {
                                System.err.println(erreur.getMessage());
                            }

                        });

                        boutonImpressBilanComplet.setOnAction(event -> {
                            TableauBilanAnnuel data = getTableView().getItems().get(getIndex());

                        });

                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                            setGraphic(null);
                        } else {
                            // Espacement de 8 pixels entre les deux boutons et centrage du HBox
                            HBox box = new HBox(8, boutonDetail, boutonImpressBilanComplet);
                            box.setAlignment(Pos.CENTER);
                            setGraphic(box);
                        }
                    }
                };
                return cell;
            }
        };

        colonne_actions_bilan_annuel.setCellFactory(cellFactory);
    }
}
