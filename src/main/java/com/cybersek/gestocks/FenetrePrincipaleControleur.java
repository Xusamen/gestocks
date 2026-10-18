package com.cybersek.gestocks;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class FenetrePrincipaleControleur {

    @FXML
    private Button Bouton_acceuil;

    @FXML
    private Button Bouton_gestocks;

    @FXML
    private Button Bouton_historique;

    @FXML
    private Button Bouton_parametres;

    @FXML
    private Button Bouton_ventes;

    @FXML
    private StackPane conteneur;

    @FXML
    private HBox topBar;

    public void automatisation_ouverture_acceuil() {
        if (Bouton_acceuil != null) {
            Bouton_acceuil.fire();
        }
    }

    private void highlightButton(Button selectedButton) {
        for (Node node : topBar.getChildren()) {
            if (node instanceof Button) {
                node.getStyleClass().remove("bouton-selectionne");
            }
        }
        selectedButton.getStyleClass().add("bouton-selectionne");
    }

    @FXML
    void ouvrir_fenetre_gestocks() {
        highlightButton(Bouton_gestocks);
        if (!(conteneur.getChildren().isEmpty())) {
            conteneur.getChildren().clear();
        }
        Task<TabPane> task = new Task<TabPane>() {
            @SuppressWarnings("unused")
            @Override
            protected TabPane call() throws Exception {
                TabPane FenetreGestionProduit = null;
                try {

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("FenetreGestionProduit.fxml"));
                    FenetreGestionProduit = loader.load();
                    FenetreGestionProduitControleur controleur_historique = loader.getController();
                } catch (Exception erreur) {
                    // Gérer l'erreur si nécessaire
                    System.out.println(erreur.getMessage());
                }
                return FenetreGestionProduit;
            }
        };
        task.setOnSucceeded(evenement5 -> {
            TabPane FenetreGestionProduit = task.getValue();
            Platform.runLater(() -> {
                conteneur.getChildren().add(FenetreGestionProduit);
            });
        });
        Thread thread = new Thread(task);
        thread.start();

    }

    @FXML
    void ouvrir_historique_fenetre() {

    }

    @FXML
    void ouvrir_parametres_fenetre() {

    }

    @FXML
    void ouvrir_tableau_de_bord() {
        highlightButton(Bouton_acceuil);
        if (!(conteneur.getChildren().isEmpty())) {
            conteneur.getChildren().clear();
        }
        switch (NomUser.getUserFonction()) {
            case "Directeur":
                Task<StackPane> task = new Task<StackPane>() {
                    @SuppressWarnings("unused")
                    @Override
                    protected StackPane call() throws Exception {
                        StackPane FenetreTableauBord = null;
                        try {
                            // Charger l'interface
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("FenetreTableauBordAdmin.fxml"));
                            FenetreTableauBord = loader.load();
                            FenetreTableauBordControleur controleur_tableau = loader.getController();
                        } catch (Exception erreur) {
                            // Gérer l'erreur si nécessaire
                            System.out.println(erreur.getMessage());
                        }
                        return FenetreTableauBord;
                    }
                };
                task.setOnSucceeded(evenement1 -> {
                    StackPane FenetreTableauBord = task.getValue();
                    Platform.runLater(() -> {
                        conteneur.getChildren().add(FenetreTableauBord);
                    });
                });
                Thread thread = new Thread(task);
                thread.start();
                break;

            case "Administrateur":
                Task<StackPane> task1 = new Task<StackPane>() {
                    @SuppressWarnings("unused")
                    @Override
                    protected StackPane call() throws Exception {
                        StackPane FenetreTableauBord = null;
                        try {
                            // Charger l'interface
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("FenetreTableauBordAdmin.fxml"));
                            FenetreTableauBord = loader.load();
                            FenetreTableauBordControleur controleur_tableau = loader.getController();
                        } catch (Exception erreur) {
                            // Gérer l'erreur si nécessaire
                            System.out.println(erreur.getMessage());
                        }
                        return FenetreTableauBord;
                    }
                };
                task1.setOnSucceeded(evenement1 -> {
                    StackPane FenetreTableauBord = task1.getValue();
                    Platform.runLater(() -> {
                        conteneur.getChildren().add(FenetreTableauBord);
                    });
                });
                Thread thread1 = new Thread(task1);
                thread1.start();
                break;

            default:
                Task<StackPane> task2 = new Task<StackPane>() {
                    @SuppressWarnings("unused")
                    @Override
                    protected StackPane call() throws Exception {
                        StackPane FenetreTableauBord = null;
                        try {
                            // Charger l'interface
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("FenetreTableauBord.fxml"));
                            FenetreTableauBord = loader.load();
                            FenetreTableauBordControleur controleur_tableau = loader.getController();
                        } catch (Exception erreur) {
                            // Gérer l'erreur si nécessaire
                            System.out.println(erreur.getMessage());
                        }
                        return FenetreTableauBord;
                    }
                };
                task2.setOnSucceeded(evenement1 -> {
                    StackPane FenetreTableauBord = task2.getValue();
                    Platform.runLater(() -> {
                        conteneur.getChildren().add(FenetreTableauBord);
                    });
                });
                Thread thread2 = new Thread(task2);
                thread2.start();
                break;
        }

    }

    @FXML
    void ouvrir_ventes_fenetre() {
        highlightButton(Bouton_ventes);
        if (!(conteneur.getChildren().isEmpty())) {
            conteneur.getChildren().clear();
        }
        Task<TabPane> task = new Task<TabPane>() {
            @SuppressWarnings("unused")
            @Override
            protected TabPane call() throws Exception {
                TabPane FenetreGestionProduit = null;
                try {

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("FenetreVenteProduitAdmin.fxml"));
                    FenetreGestionProduit = loader.load();
                    FenetreVenteProduitAdminControleur controleur_vente = loader.getController();
                } catch (Exception erreur) {
                    // Gérer l'erreur si nécessaire
                    System.out.println(erreur.getMessage());
                }
                return FenetreGestionProduit;
            }
        };
        task.setOnSucceeded(evenement5 -> {
            TabPane FenetreGestionProduit = task.getValue();
            Platform.runLater(() -> {
                conteneur.getChildren().add(FenetreGestionProduit);
            });
        });
        Thread thread = new Thread(task);
        thread.start();

    }

}
