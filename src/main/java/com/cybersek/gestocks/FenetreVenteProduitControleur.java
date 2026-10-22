package com.cybersek.gestocks;

import java.io.IOException;
import java.math.BigDecimal;

import com.cybersek.gestocks.StructureDonnee.CommandeDetails;
import com.cybersek.gestocks.StructureDonnee.InfoEncaissement;
import com.cybersek.gestocks.StructureDonnee.InventaireStocks;
import com.cybersek.gestocks.ModuleStocks.Inventaire;
import com.cybersek.gestocks.ModuleVente.Commande;
import com.cybersek.gestocks.ModuleVente.Encaissement;
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

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    private ObservableList<InventaireStocks> listeStockComplet = FXCollections.observableArrayList();
    private FilteredList<InventaireStocks> listeFiltreeStock;

    private ObservableList<CommandeDetails> listeCommande = FXCollections.observableArrayList();
    private ObservableList<InfoEncaissement> listeEncaissement = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        action_bouton_vente_produit();
        action_bouton_commande_produit();
        action_bouton_encaissement_vente();
        InitTableauStock();
        InitTableauCommande();
        InitTableauEncaissement();
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
        colonne_IdVente_encaissement.setCellValueFactory(cellData -> cellData.getValue().idVenteProperty());
        colonne_date_encaissement.setCellValueFactory(cellData -> cellData.getValue().dateVenteProperty());
        colonne_heure_encaissement.setCellValueFactory(cellData -> cellData.getValue().heureVenteProperty());
        colonne_commandeclient_encaissement
                .setCellValueFactory(cellData -> cellData.getValue().commandeClientProperty());
        colonne_montant_encaissement.setCellValueFactory(cellData -> cellData.getValue().montantTotalProperty());
        colonne_statut_encaissement.setCellValueFactory(cellData -> cellData.getValue().statutTransactionProperty());
        listeEncaissement = Encaissement.ListerTableauPourEncaissement();
        tableau_encaissement.setItems(listeEncaissement);
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

}
