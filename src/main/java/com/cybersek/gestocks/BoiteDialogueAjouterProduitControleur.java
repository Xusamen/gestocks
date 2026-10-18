package com.cybersek.gestocks;

import com.cybersek.gestocks.ModuleStocks.GestionProduit;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

public class BoiteDialogueAjouterProduitControleur {

    @FXML
    public void initialize() {
        // Liste des unités spécialisées pour une pharmacie
        choicebox_unite.getItems().addAll(
                // 1. Formes galéniques orales (Solides)
                "Comprimé", "Gélule", "Capsule", "Sachet", "Pilule", "Pastille", "Dragée", "Granulé",

                // 2. Formes liquides, injectables et gouttes
                "Flacon", "Ampoule", "Sirop", "Goutte", "Fioule", "Soluté",

                // 3. Formes topiques et externes (Pommades, crémes, etc.)
                "Tube", "Pot", "Spray", "Inhalateur", "Suppositoire", "Ovule", "Patch",

                // 4. Dispositifs médicaux, pansements et matériel
                "Unité", "Boîte", "Flacon-doseur", "Seringue", "Poche", "Pansement", "Bande", "Rouleau", "Paire",

                // 5. Conditionnements regroupés / Conditionnement primaire & secondaire
                "Plaquette", "Blister", "Carton", "Lot", "Flacon de",

                // 6. Dosages et masses
                "mg", "g", "kg", "mcg", "UI", // UI = Unités Internationales

                // 7. Volumes
                "ml", "cl", "L");

        // Valeur par défaut recommandée pour une pharmacie
        choicebox_unite.setValue("Boîte");
    }

    @FXML
    private TextField champ_nom_produit;

    @FXML
    private ChoiceBox<String> choicebox_unite;

    @FXML
    void AjouterProduit(ActionEvent event) {
        String libelleProduit = champ_nom_produit.getText().trim();
        String unite = choicebox_unite.getValue().trim();

        // 1. Vérification que le champ n'est pas vide
        if (libelleProduit.isEmpty()) {
            Functions.Alerte_erreur("Le nom du produit ne peut pas être vide.");
            return;
        }

        if (unite.isEmpty()) {
            Functions.Alerte_erreur("Le nom du produit doit avoir une unité.");
            return;
        }
        boolean succes = GestionProduit.insererProduit(libelleProduit, unite);

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