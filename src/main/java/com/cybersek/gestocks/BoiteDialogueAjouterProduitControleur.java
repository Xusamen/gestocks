package com.cybersek.gestocks;

import com.cybersek.gestocks.ModuleStocks.GestionProduit;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class BoiteDialogueAjouterProduitControleur {

    @FXML
    private TextField champ_nom_produit;

    @FXML
    void AjouterProduit(ActionEvent event) {
        String libelleProduit = champ_nom_produit.getText().trim();

        // 1. Vérification que le champ n'est pas vide
        if (libelleProduit.isEmpty()) {
            Functions.Alerte_erreur("Le nom du produit ne peut pas être vide.");
            return;
        }
        boolean succes = GestionProduit.insererProduit(libelleProduit);

        if (succes) {
            Functions.Alerte_succes("Produit ajouté avec succès !");
            FenetreGestionProduitControleur.FermerBoiteDialogueAjouterProduit();
        } else {
            Functions.Alerte_erreur("Échec de l'ajout du produit (peut-être qu'il existe déjà ou erreur SQL).");
        }
    }

    @FXML
    void AnnulerAjouterProduit(ActionEvent event) {
        FenetreGestionProduitControleur.FermerBoiteDialogueAjouterProduit();
    }
}