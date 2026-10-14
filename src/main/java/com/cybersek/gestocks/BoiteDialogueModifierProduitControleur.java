package com.cybersek.gestocks;

import com.cybersek.gestocks.StructureDonnee.Produit;
import com.cybersek.gestocks.ModuleStocks.GestionProduit;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class BoiteDialogueModifierProduitControleur {

    @FXML
    private TextField champ_nom_produit;
    private Long idProduitActuel;

    @FXML
    void FermerFenetre(ActionEvent event) {
        FenetreGestionProduitControleur.FermerBoiteDialogueModifierProduit();
    }

    @FXML
    void ModifierProduit(ActionEvent event) {
        String nouveauLibelle = champ_nom_produit.getText();
        if (nouveauLibelle != null && !nouveauLibelle.trim().isEmpty() && idProduitActuel != null) {
            boolean succes = GestionProduit.modifierLibelleProduit(idProduitActuel, nouveauLibelle.trim());
            if (succes) {
                Functions.Alerte_succes("Produit modifié avec succès !");
                FenetreGestionProduitControleur.FermerBoiteDialogueModifierProduit();
            } else {
                Functions.Alerte_erreur("Échec de la modification du produit.");
            }
        } else {
            Functions.Alerte_erreur("Le nom du produit ne peut pas être vide.");
        }
    }

    public void chargerDonnees(Long idProduit) {
        this.idProduitActuel = idProduit;
        Produit produit = GestionProduit.rechercherProduitParId(idProduit);
        if (produit != null) {
            champ_nom_produit.setText(produit.getLibelleProduit());
        }
    }
}