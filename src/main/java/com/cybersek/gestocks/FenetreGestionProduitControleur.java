package com.cybersek.gestocks;

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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.time.LocalDateTime;

import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

import com.cybersek.gestocks.StructureDonnee.Produit;
import com.cybersek.gestocks.ModuleStocks.GestionProduit;

public class FenetreGestionProduitControleur {

    @FXML
    private Button bouton_ajouter_produit;

    @FXML
    private TextField champ_nom_produit; // Utilisé pour la recherche dynamique

    @FXML
    private TableColumn<Produit, Void> colonne_action_produit;

    @FXML
    private TableColumn<Produit, LocalDateTime> colonne_date_produit;

    @FXML
    private TableColumn<Produit, String> colonne_nom_produit;

    @FXML
    private TableView<Produit> tableau_produit;

    // 1. Liste complète d'origine
    private ObservableList<Produit> listeProduitsComprehension = FXCollections.observableArrayList();

    // 2. Liste filtrée liée au tableau
    private FilteredList<Produit> listeFiltree;

    @FXML
    public void initialize() {
        // Configuration des colonnes
        colonne_nom_produit.setCellValueFactory(new PropertyValueFactory<>("libelleProduit"));
        colonne_date_produit.setCellValueFactory(new PropertyValueFactory<>("dateCreationFormat"));
        action_bouton_gestion_produit();
        chargerProduits();
        configurerRechercheDynamique();
    }

    private void chargerProduits() {
        listeProduitsComprehension = GestionProduit.listerProduits();
        listeFiltree = new FilteredList<>(listeProduitsComprehension, p -> true);
        tableau_produit.setItems(listeFiltree);
    }

    private void configurerRechercheDynamique() {
        // Écoute les changements de texte dans le TextField en temps réel
        champ_nom_produit.textProperty().addListener((observable, oldValue, newValue) -> {
            listeFiltree.setPredicate(produit -> {
                // Si le champ est vide, on affiche tous les produits
                if (newValue == null || newValue.trim().isEmpty()) {
                    return true;
                }
                String saisieUtilisateur = newValue.toLowerCase();
                if (produit.getLibelleProduit() != null &&
                        produit.getLibelleProduit().toLowerCase().contains(saisieUtilisateur)) {
                    return true;
                }
                return false;
            });
        });
    }

    @FXML
    void initialize_liste_produit() {
        chargerProduits();
    }

    private static Stage BoiteDialogueAjouterProduit;

    public static void FermerBoiteDialogueAjouterProduit() {
        BoiteDialogueAjouterProduit.close();
    }

    @FXML
    void OuvrirFenetreAjouterProduit(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("BoiteDialogueAjouterProduit.fxml"));
            Parent root = loader.load();
            root.setStyle("-fx-background-color: white; -fx-background-radius: 15;");
            root.setEffect(Functions.ArrierePlan());
            StackPane contenaire = new StackPane(root);
            contenaire.setStyle("-fx-background-color: white; -fx-background-radius: 20;");
            contenaire.setPadding(new Insets(20));
            BoiteDialogueAjouterProduit = new Stage();
            BoiteDialogueAjouterProduit.setResizable(false);
            BoiteDialogueAjouterProduit.initModality(Modality.APPLICATION_MODAL);
            BoiteDialogueAjouterProduit
                    .initOwner(((javafx.scene.Node) event.getSource()).getScene().getWindow());
            BoiteDialogueAjouterProduit.initStyle(StageStyle.TRANSPARENT);
            Scene scene = new Scene(contenaire);
            scene.setFill(Color.TRANSPARENT);
            BoiteDialogueAjouterProduit.setScene(scene);
            BoiteDialogueAjouterProduit.showAndWait();
        } catch (IOException erreur) {
            System.err.println(erreur.getMessage());
        }
        chargerProduits();
    }

    // BOUTONs Actions
    private static Stage BoiteDialogueApprovisionnementProduit;

    public static void FermerBoiteDialogueApprovisionnementProduit() {
        BoiteDialogueApprovisionnementProduit.close();
    }

    private static Stage BoiteDialogueModifierProduit;

    public static void FermerBoiteDialogueModifierProduit() {
        BoiteDialogueModifierProduit.close();
    }

    private static Stage BoiteDialogueInfoProduit;

    public static void FermerBoiteDialogueInfoProduit() {
        BoiteDialogueInfoProduit.close();
    }

    private void action_bouton_gestion_produit() {
        Callback<TableColumn<Produit, Void>, TableCell<Produit, Void>> cellFactory = new Callback<TableColumn<Produit, Void>, TableCell<Produit, Void>>() {
            @Override
            public TableCell<Produit, Void> call(final TableColumn<Produit, Void> param) {
                final TableCell<Produit, Void> cell = new TableCell<Produit, Void>() {
                    // Initialisation des boutons
                    private final Button boutonApproProduit = new Button("Approvisionner");
                    private final Button boutonModifierProduit = new Button("Modifier");
                    private final Button boutonInfosProduit = new Button("Informations");

                    // Styles CSS pour les boutons (Harmonisés avec votre charte)
                    private final String baseStyle = "-fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 6 12; -fx-cursor: hand; -fx-font-size: 12px;";
                    private final String styleVert = baseStyle + " -fx-background-color: #4CAF50;"; // Appro
                    private final String styleOrange = baseStyle + " -fx-background-color: #FF9800;"; // Modifier
                    private final String styleCyan = baseStyle + " -fx-background-color: #00BCD4;"; // Infos

                    {
                        // Application des styles de couleur initiaux
                        boutonApproProduit.setStyle(styleVert);
                        boutonModifierProduit.setStyle(styleOrange);
                        boutonInfosProduit.setStyle(styleCyan);

                        // --- Événements au survol (Effet Hover) ---
                        boutonApproProduit.setOnMouseEntered(e -> boutonApproProduit
                                .setStyle(baseStyle + " -fx-background-color: #43A047;"));
                        boutonApproProduit
                                .setOnMouseExited(e -> boutonApproProduit.setStyle(styleVert));

                        boutonModifierProduit.setOnMouseEntered(e -> boutonModifierProduit
                                .setStyle(baseStyle + " -fx-background-color: #FB8C00;"));
                        boutonModifierProduit
                                .setOnMouseExited(e -> boutonModifierProduit.setStyle(styleOrange));

                        boutonInfosProduit.setOnMouseEntered(e -> boutonInfosProduit
                                .setStyle(baseStyle + " -fx-background-color: #00ACC1;"));
                        boutonInfosProduit
                                .setOnMouseExited(e -> boutonInfosProduit.setStyle(styleCyan));

                        // --- Action Appro ---
                        boutonApproProduit.setOnAction(event -> {
                            Produit data = getTableView().getItems().get(getIndex());
                            System.out.println("Approvisionnement du produit : " + data.getLibelleProduit());
                            try {
                                FXMLLoader loader = new FXMLLoader(
                                        getClass().getResource("BoiteDialogueApprovisionnementProduit.fxml"));
                                Parent root = loader.load();
                                root.setStyle("-fx-background-color: white; -fx-background-radius: 15;");
                                root.setEffect(Functions.ArrierePlan());

                                StackPane contenaire = new StackPane(root);
                                contenaire.setStyle("-fx-background-color: white; -fx-background-radius: 20;");
                                contenaire.setPadding(new Insets(20));

                                BoiteDialogueApprovisionnementProduitControleur controleur = loader.getController();
                                controleur.chargerDonnees(data.getIdProduit());

                                BoiteDialogueApprovisionnementProduit = new Stage();
                                BoiteDialogueApprovisionnementProduit.setResizable(false);
                                BoiteDialogueApprovisionnementProduit.initModality(Modality.APPLICATION_MODAL);
                                BoiteDialogueApprovisionnementProduit
                                        .initOwner(((javafx.scene.Node) event.getSource()).getScene().getWindow());
                                BoiteDialogueApprovisionnementProduit.initStyle(StageStyle.TRANSPARENT);

                                Scene scene = new Scene(contenaire);
                                scene.setFill(Color.TRANSPARENT);
                                BoiteDialogueApprovisionnementProduit.setScene(scene);
                                BoiteDialogueApprovisionnementProduit.showAndWait();
                            } catch (IOException erreur) {
                                System.err.println(erreur.getMessage());
                            }
                            // tableau_produit.setItems(GestionProduit.listerProduits());
                        });

                        // --- Action Modifier ---
                        boutonModifierProduit.setOnAction(event -> {
                            Produit data = getTableView().getItems().get(getIndex());
                            System.out.println("Modification du produit ID : " + data.getIdProduit());
                            try {
                                FXMLLoader loader = new FXMLLoader(
                                        getClass().getResource("BoiteDialogueModifierProduit.fxml"));
                                Parent root = loader.load();
                                root.setStyle("-fx-background-color: white; -fx-background-radius: 15;");
                                root.setEffect(Functions.ArrierePlan());

                                StackPane contenaire = new StackPane(root);
                                contenaire.setStyle("-fx-background-color: white; -fx-background-radius: 20;");
                                contenaire.setPadding(new Insets(20));

                                BoiteDialogueModifierProduitControleur controleur = loader.getController();
                                controleur.chargerDonnees(data.getIdProduit());

                                BoiteDialogueModifierProduit = new Stage();
                                BoiteDialogueModifierProduit.setResizable(false);
                                BoiteDialogueModifierProduit.initModality(Modality.APPLICATION_MODAL);
                                BoiteDialogueModifierProduit
                                        .initOwner(((javafx.scene.Node) event.getSource()).getScene().getWindow());
                                BoiteDialogueModifierProduit.initStyle(StageStyle.TRANSPARENT);

                                Scene scene = new Scene(contenaire);
                                scene.setFill(Color.TRANSPARENT);
                                BoiteDialogueModifierProduit.setScene(scene);
                                BoiteDialogueModifierProduit.showAndWait();
                            } catch (IOException erreur) {
                                System.err.println(erreur.getMessage());
                            }
                            tableau_produit.setItems(GestionProduit.listerProduits());
                        });

                        // --- Action Infos ---
                        boutonInfosProduit.setOnAction(event -> {
                            Produit data = getTableView().getItems().get(getIndex());
                            System.out.println("Affichage des infos pour : " + data.getLibelleProduit());
                            try {
                                FXMLLoader loader = new FXMLLoader(
                                        getClass().getResource("BoiteDialogueInfoProduit.fxml"));
                                Parent root = loader.load();
                                root.setStyle("-fx-background-color: white; -fx-background-radius: 15;");
                                root.setEffect(Functions.ArrierePlan());

                                StackPane contenaire = new StackPane(root);
                                contenaire.setStyle("-fx-background-color: white; -fx-background-radius: 20;");
                                contenaire.setPadding(new Insets(20));

                                BoiteDialogueInfoProduitControleur controleur = loader.getController();
                                controleur.chargerDonnees(data.getIdProduit());

                                BoiteDialogueInfoProduit = new Stage();
                                BoiteDialogueInfoProduit.setResizable(false);
                                BoiteDialogueInfoProduit.initModality(Modality.APPLICATION_MODAL);
                                BoiteDialogueInfoProduit
                                        .initOwner(((javafx.scene.Node) event.getSource()).getScene().getWindow());
                                BoiteDialogueInfoProduit.initStyle(StageStyle.TRANSPARENT);

                                Scene scene = new Scene(contenaire);
                                scene.setFill(Color.TRANSPARENT);
                                BoiteDialogueInfoProduit.setScene(scene);
                                BoiteDialogueInfoProduit.showAndWait();
                            } catch (IOException erreur) {
                                System.err.println(erreur.getMessage());
                            }
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                            setGraphic(null);
                        } else {
                            HBox box = new HBox(6, boutonApproProduit, boutonModifierProduit, boutonInfosProduit);
                            box.setAlignment(Pos.CENTER);
                            setGraphic(box);
                        }
                    }
                };
                return cell;
            }
        };

        colonne_action_produit.setCellFactory(cellFactory);
    }
}