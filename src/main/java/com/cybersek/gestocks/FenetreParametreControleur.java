package com.cybersek.gestocks;

import java.io.IOException;

import com.cybersek.gestocks.StructureDonnee.CommandeDetails;
import com.cybersek.gestocks.StructureDonnee.Utilisateur;
import com.cybersek.gestocks.ModuleParametre.GestionUtilisateur;
import com.cybersek.gestocks.ModuleVente.Commande;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

public class FenetreParametreControleur {

    @FXML
    private Button bouton_ajouter_utilisateur;

    @FXML
    private TableColumn<Utilisateur, Void> colonne_actions_gestion_user;

    @FXML
    private TableColumn<Utilisateur, String> colonne_identifiant_gestion_user;

    @FXML
    private TableColumn<Utilisateur, String> colonne_nom_gestion_user;

    @FXML
    private TableColumn<Utilisateur, String> colonne_prenom_gestion_user;

    @FXML
    private TableColumn<Utilisateur, String> colonne_statut_gestion_user;

    @FXML
    private TableView<Utilisateur> tableau_gestion_user;

    private ObservableList<Utilisateur> listeUser = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        InitTableauUser();
        action_bouton_gestion_user();
    }

    private void InitTableauUser() {
        colonne_nom_gestion_user.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colonne_prenom_gestion_user.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colonne_identifiant_gestion_user.setCellValueFactory(new PropertyValueFactory<>("identifiant"));
        colonne_statut_gestion_user.setCellValueFactory(new PropertyValueFactory<>("statut"));
        listeUser = GestionUtilisateur.ListeUtilisateurs();
        tableau_gestion_user.setItems(listeUser);
    }

    private static Stage BoiteDialogueAjouterUtilisateur;

    public static void FermerBoiteDialogueAjouterUtilisateur() {
        BoiteDialogueAjouterUtilisateur.close();
    }

    @FXML
    void OuvrirFenetreAjoutUtilisateur(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("BoiteDialogueAjouterUtilisateur.fxml"));
            Parent root = loader.load();
            root.setStyle("-fx-background-color: white; -fx-background-radius: 15;");
            root.setEffect(Functions.ArrierePlan());
            StackPane contenaire = new StackPane(root);
            contenaire.setStyle("-fx-background-color: white; -fx-background-radius: 20;");
            contenaire.setPadding(new Insets(20));
            @SuppressWarnings("unused")
            BoiteDialogueAjouterUtilisateurControleur controleur = loader.getController();
            BoiteDialogueAjouterUtilisateur = new Stage();
            BoiteDialogueAjouterUtilisateur.setResizable(false);
            BoiteDialogueAjouterUtilisateur.initModality(Modality.APPLICATION_MODAL); // Modality.WINDOW_MODAL
            BoiteDialogueAjouterUtilisateur
                    .initOwner(((javafx.scene.Node) event.getSource()).getScene().getWindow());
            BoiteDialogueAjouterUtilisateur.initStyle(StageStyle.TRANSPARENT);
            Scene scene = new Scene(contenaire);
            scene.setFill(Color.TRANSPARENT);
            BoiteDialogueAjouterUtilisateur.setScene(scene);
            BoiteDialogueAjouterUtilisateur.showAndWait();
        } catch (IOException erreur) {
            System.err.println(erreur.getMessage());
        }
        tableau_gestion_user.setItems(GestionUtilisateur.ListeUtilisateurs());

    }

    private static Stage BoiteDialogueModifierUtilisateur;

    public static void FermerBoiteDialogueModifierUtilisateur() {
        BoiteDialogueModifierUtilisateur.close();
    }

    private void action_bouton_gestion_user() {
        Callback<TableColumn<Utilisateur, Void>, TableCell<Utilisateur, Void>> cellFactory = new Callback<TableColumn<Utilisateur, Void>, TableCell<Utilisateur, Void>>() {
            @Override
            public TableCell<Utilisateur, Void> call(final TableColumn<Utilisateur, Void> param) {
                final TableCell<Utilisateur, Void> cell = new TableCell<Utilisateur, Void>() {
                    // Initialisation des boutons
                    private final Button boutonDesactiverUtilisateur = new Button("Désactiver");
                    private final Button boutonModifierInfoUtilisateur = new Button("Modifier");
                    private final Button boutonSupprimerUtilisateur = new Button("Supprimer");

                    // Styles CSS pour les boutons
                    private final String baseStyle = "-fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 6 12; -fx-cursor: hand; -fx-font-size: 12px;";
                    private final String styleBleu = baseStyle + " -fx-background-color: #2196F3;";
                    private final String styleOrange = baseStyle + " -fx-background-color: #FF9800;";
                    private final String styleRouge = baseStyle + " -fx-background-color: #F44336;";

                    {
                        // Application des styles de couleur
                        boutonDesactiverUtilisateur.setStyle(styleBleu);
                        boutonModifierInfoUtilisateur.setStyle(styleOrange);
                        boutonSupprimerUtilisateur.setStyle(styleRouge);

                        // --- Événements au survol (Effet Hover) ---
                        boutonDesactiverUtilisateur.setOnMouseEntered(e -> boutonDesactiverUtilisateur
                                .setStyle(baseStyle + " -fx-background-color: #1E88E5;"));
                        boutonDesactiverUtilisateur
                                .setOnMouseExited(e -> boutonDesactiverUtilisateur.setStyle(styleBleu));

                        boutonModifierInfoUtilisateur.setOnMouseEntered(e -> boutonModifierInfoUtilisateur
                                .setStyle(baseStyle + " -fx-background-color: #FB8C00;"));
                        boutonModifierInfoUtilisateur
                                .setOnMouseExited(e -> boutonModifierInfoUtilisateur.setStyle(styleOrange));

                        boutonSupprimerUtilisateur.setOnMouseEntered(e -> boutonSupprimerUtilisateur
                                .setStyle(baseStyle + " -fx-background-color: #E53935;"));
                        boutonSupprimerUtilisateur
                                .setOnMouseExited(e -> boutonSupprimerUtilisateur.setStyle(styleRouge));

                        // --- Action Désactiver / Activer ---
                        boutonDesactiverUtilisateur.setOnAction(event -> {
                            Utilisateur data = getTableView().getItems().get(getIndex());
                            GestionUtilisateur.ChangerStatutUtilisateur(data.getId());
                            tableau_gestion_user.setItems(GestionUtilisateur.ListeUtilisateurs());
                        });

                        // --- Action Modifier ---
                        boutonModifierInfoUtilisateur.setOnAction(event -> {
                            Utilisateur data = getTableView().getItems().get(getIndex());
                            try {
                                FXMLLoader loader = new FXMLLoader(
                                        getClass().getResource("BoiteDialogueModifierUtilisateur.fxml"));
                                Parent root = loader.load();
                                root.setStyle("-fx-background-color: white; -fx-background-radius: 15;");
                                root.setEffect(Functions.ArrierePlan());

                                StackPane contenaire = new StackPane(root);
                                contenaire.setStyle("-fx-background-color: white; -fx-background-radius: 20;");
                                contenaire.setPadding(new Insets(20));

                                BoiteDialogueModifierUtilisateurControleur controleur = loader.getController();
                                controleur.chargerDonnees(data.getId());

                                BoiteDialogueModifierUtilisateur = new Stage();
                                BoiteDialogueModifierUtilisateur.setResizable(false);
                                BoiteDialogueModifierUtilisateur.initModality(Modality.APPLICATION_MODAL);
                                BoiteDialogueModifierUtilisateur
                                        .initOwner(((javafx.scene.Node) event.getSource()).getScene().getWindow());
                                BoiteDialogueModifierUtilisateur.initStyle(StageStyle.TRANSPARENT);

                                Scene scene = new Scene(contenaire);
                                scene.setFill(Color.TRANSPARENT);
                                BoiteDialogueModifierUtilisateur.setScene(scene);
                                BoiteDialogueModifierUtilisateur.showAndWait();
                            } catch (IOException erreur) {
                                System.err.println(erreur.getMessage());
                            }

                            tableau_gestion_user.setItems(GestionUtilisateur.ListeUtilisateurs());
                        });

                        // --- Action Supprimer ---
                        boutonSupprimerUtilisateur.setOnAction(event -> {
                            Utilisateur data = getTableView().getItems().get(getIndex());
                            GestionUtilisateur.SupprimerUtilisateur(data);
                            tableau_gestion_user.setItems(GestionUtilisateur.ListeUtilisateurs());
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                            setGraphic(null);
                        } else {
                            Utilisateur user = getTableView().getItems().get(getIndex());

                            if (user != null) {
                                if ("Actif".equalsIgnoreCase(user.getStatut())) {
                                    boutonDesactiverUtilisateur.setText("Désactiver");
                                } else {
                                    boutonDesactiverUtilisateur.setText("Activer");
                                }
                            }

                            HBox box = new HBox(6, boutonDesactiverUtilisateur, boutonModifierInfoUtilisateur,
                                    boutonSupprimerUtilisateur);
                            box.setAlignment(Pos.CENTER);
                            setGraphic(box);
                        }
                    }
                };
                return cell;
            }
        };

        colonne_actions_gestion_user.setCellFactory(cellFactory);
    }

}
