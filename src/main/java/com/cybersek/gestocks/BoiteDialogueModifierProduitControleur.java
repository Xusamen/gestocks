package com.cybersek.gestocks;

import com.cybersek.gestocks.StructureDonnee.Produit;
import com.cybersek.gestocks.ModuleStocks.GestionProduit;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

public class BoiteDialogueModifierProduitControleur {

    @FXML
    private TextField champ_nom_produit;
    @FXML
    private ChoiceBox<String> choicebox_unite;
    private Long idProduitActuel;

    @FXML
    void FermerFenetre(ActionEvent event) {
        FenetreGestionProduitControleur.FermerBoiteDialogueModifierProduit();
    }

    @FXML
    void ModifierProduit(ActionEvent event) {
        String nouveauLibelle = champ_nom_produit.getText();
        if (nouveauLibelle != null && !nouveauLibelle.trim().isEmpty() && idProduitActuel != null) {
            boolean succes = GestionProduit.modifierProduit(idProduitActuel, nouveauLibelle.trim(), choicebox_unite.getValue().trim());
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
            choicebox_unite.setValue(produit.getUnite());
            // je ne donne pas la possiblité de changer l'unite du produit
        }
    }
}