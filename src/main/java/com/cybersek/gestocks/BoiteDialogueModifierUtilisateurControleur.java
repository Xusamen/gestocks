package com.cybersek.gestocks;

import com.cybersek.gestocks.StructureDonnee.Utilisateur;
import com.cybersek.gestocks.ModuleParametre.GestionUtilisateur;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

/**
 * BoiteDialogueModifierUtilisateurControleur
 */
public class BoiteDialogueModifierUtilisateurControleur {

    @FXML
    private Button bouton_annuler;

    @FXML
    private Button bouton_modifier;

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

    private int idUtilisateur;

    public void chargerDonnees(int id) {
        Utilisateur utilisateur = GestionUtilisateur.ObtenirUtilisateurParId(id);
        if (utilisateur != null) {
            this.idUtilisateur = utilisateur.getId();
            champ_nom.setText(utilisateur.getNom());
            champ_prenoms.setText(utilisateur.getPrenom());
            champ_identifiant.setText(utilisateur.getIdentifiant());
            champ_fonction.setText(utilisateur.getFonction());
            champ_mdp.clear();
        }
    }

    @FXML
    void ModifierInformationUtilisateur(ActionEvent event) {
        String nom = champ_nom.getText().trim();
        String prenoms = champ_prenoms.getText().trim();
        String identifiant = champ_identifiant.getText().trim();
        String fonction = champ_fonction.getText().trim();
        String mdp = champ_mdp.getText().trim(); // Vide ou nouveau mot de passe

        if (nom.isEmpty() || prenoms.isEmpty() || identifiant.isEmpty()) {
            Functions.Alerte_erreur("Veuillez remplir les champs obligatoires (nom, prénom, identifiant).");
            return;
        }

        boolean succes = GestionUtilisateur.ModifierUtilisateur(idUtilisateur, nom, prenoms, identifiant, fonction,
                mdp);

        if (succes) {
            FenetreParametreControleur.FermerBoiteDialogueModifierUtilisateur();
        }
    }

    @FXML
    void FermerBoiteDialogue(ActionEvent event) {
        FenetreParametreControleur.FermerBoiteDialogueModifierUtilisateur();
    }
}