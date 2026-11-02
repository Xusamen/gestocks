package com.cybersek.gestocks;

import com.cybersek.gestocks.ModuleParametre.GestionUtilisateur;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

/**
 * BoiteDialogueAjouterUtilisateurControleur
 */
public class BoiteDialogueAjouterUtilisateurControleur {

    @FXML
    private Button bouton_ajouter;

    @FXML
    private Button bouton_annuler;

    @FXML
    private TextField champ_fonction;

    @FXML
    private TextField champ_identifiant;

    @FXML
    private TextField champ_mdp;

    @FXML
    private TextField champ_nom;

    @FXML
    private TextField champ_prenoms;

    @FXML
    void AjouterUtilisateur(ActionEvent event) {
        String nom = champ_nom.getText().trim();
        String prenoms = champ_prenoms.getText().trim();
        String identifiant = champ_identifiant.getText().trim();
        String mdp = champ_mdp.getText().trim();
        String fonction = champ_fonction.getText().trim();

        if (identifiant.isEmpty() || mdp.isEmpty() || nom.isEmpty() || prenoms.isEmpty()) {
            Functions.Alerte_erreur("Veuillez remplir au moins le nom, le prénom, l'identifiant et le mot de passe.");
            return;
        }
        boolean succes = GestionUtilisateur.CreerUtilisateur(nom, prenoms, identifiant, mdp, fonction);

        if (succes) {
            FenetreParametreControleur.FermerBoiteDialogueAjouterUtilisateur();
        }
    }

    @FXML
    void AnnulerAjoutUtilisateur(ActionEvent event) {
        FenetreParametreControleur.FermerBoiteDialogueAjouterUtilisateur();
    }

}
