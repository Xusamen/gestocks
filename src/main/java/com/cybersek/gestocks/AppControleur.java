package com.cybersek.gestocks;

import java.io.IOException;

import com.cybersek.gestocks.ModuleParametre.VerificationUser;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

public class AppControleur {

    @FXML
    private Button bouton_connexion;

    @FXML
    private StackPane conteneur_principal = new StackPane();

    @FXML
    private PasswordField password_texfield;

    @FXML
    private TextField poste_textfield;

    private String ObtenirAdresseIP() {
        try {
            return java.net.InetAddress.getLocalHost().getHostAddress();
        } catch (java.net.UnknownHostException e) {
            return "127.0.0.1";
        }
    }

    public void FenetrePrincipaleContenu(String fxmlFile) {
        if (conteneur_principal != null) {
            conteneur_principal.getChildren().clear();
            try {
                FXMLLoader ouvreur = new FXMLLoader(getClass().getResource(fxmlFile));
                AnchorPane nouveauContenu = ouvreur.load();
                nouveauContenu.getStylesheets()
                        .add(getClass().getResource("/com/cybersek/gestocks/css/style.css").toExternalForm());
                FenetrePrincipaleControleur nouveauControleur = ouvreur.getController();
                nouveauControleur.automatisation_ouverture_acceuil();
                conteneur_principal.getChildren().add(nouveauContenu);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Erreur lors du chargement de " + fxmlFile);
            }
        } else {
            System.out.println("conteneur_principal est null !");
        }
    }

    @FXML
    void Ouvrir_FenetrePrincipale(ActionEvent event) {
        String ipAdresse = ObtenirAdresseIP();
        String username = poste_textfield.getText();
        String password = password_texfield.getText();
        poste_textfield.clear();
        password_texfield.clear();
        NomUser.setUserIdentifiant(username);
        boolean statut = VerificationUser.Verification_Identifiant_Connexion(username, password);
        if (statut) {
            VerificationUser.EnregistrerConnexionSucces(NomUser.getIdUtilisateur(), username, ipAdresse);
            FenetrePrincipaleContenu("FenetrePrincipale.fxml");
        } else {
            VerificationUser.EnregistrerConnexionEchec(username, ipAdresse);
            Functions.Alerte_erreur("Identifiant Erroné");
        }

    }

}
