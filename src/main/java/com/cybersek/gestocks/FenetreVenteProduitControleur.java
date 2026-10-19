package com.cybersek.gestocks;

import java.io.IOException;
import java.math.BigDecimal;

import com.cybersek.gestocks.StructureDonnee.CommandeDetails;
import com.cybersek.gestocks.StructureDonnee.InventaireStocks;
import com.cybersek.gestocks.ModuleStocks.Inventaire;
import com.cybersek.gestocks.ModuleVente.Commande;

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

    private ObservableList<InventaireStocks> listeStockComplet = FXCollections.observableArrayList();
    private FilteredList<InventaireStocks> listeFiltreeStock;

    private ObservableList<CommandeDetails> listeCommande = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        action_bouton_vente_produit();
        action_bouton_commande_produit();
        InitTableauStock();
        InitTableauCommande();
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

    @FXML
    void Annuler_Commande_CLient(ActionEvent event) {

    }

    @FXML
    void Valider_Commande_Client(ActionEvent event) {

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
                    private final Button boutonAjouterProduit = new Button("Retirer");

                    // Styles CSS pour les boutons (Harmonisés avec votre charte)
                    private final String baseStyle = "-fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 6 12; -fx-cursor: hand; -fx-font-size: 12px;";
                    private final String styleVert = baseStyle + " -fx-background-color: #f44336 ;";
                    {
                        // Application des styles de couleur initiaux
                        boutonAjouterProduit.setStyle(styleVert);

                        // --- Événements au survol (Effet Hover) ---
                        boutonAjouterProduit.setOnMouseEntered(e -> boutonAjouterProduit
                                .setStyle(baseStyle + " -fx-background-color: #F40B0B;"));
                        boutonAjouterProduit
                                .setOnMouseExited(e -> boutonAjouterProduit.setStyle(styleVert));

                        // --- Action Ajouter Produit ---
                        boutonAjouterProduit.setOnAction(event -> {
                            CommandeDetails data = getTableView().getItems().get(getIndex());
                            Commande.retirerProduitCommande(data.getIdCommande());
                            tableau_commande.setItems(Commande.listerCommandesParVente(ID_VENTE));
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

        colonne_action_commande.setCellFactory(cellFactory);
    }

}
