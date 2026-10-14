package com.cybersek.gestocks;

import com.cybersek.gestocks.ModuleStocks.GestionProduit;
import com.cybersek.gestocks.StructureDonnee.Produit;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class BoiteDialogueApprovisionnementProduitControleur {

    @FXML
    private TextField champ_date_appro;

    @FXML
    private TextField champ_date_preremp;

    @FXML
    private TextField champ_prix_achat_unitaire;

    @FXML
    private TextField champ_prix_vente_unitaire;

    @FXML
    private TextField champ_quantite;

    @FXML
    private Text label_nom_produit;

    // Variable pour stocker l'ID du produit concerné par l'approvisionnement
    private Long idProduitActuel;

    @FXML
    void AjouterApprovisionnement(ActionEvent event) {
        try {
            // 1. Récupérer les valeurs des champs de texte
            String dateApproStr = champ_date_appro.getText().trim();
            String datePerempStr = champ_date_preremp.getText().trim();
            String prixAchatStr = champ_prix_achat_unitaire.getText().trim();
            String prixVenteStr = champ_prix_vente_unitaire.getText().trim();
            String quantiteStr = champ_quantite.getText().trim();

            if (dateApproStr.isEmpty() || datePerempStr.isEmpty() || prixAchatStr.isEmpty() ||
                    prixVenteStr.isEmpty() || quantiteStr.isEmpty()) {
                Functions.Alerte_erreur("Veuillez remplir tous les champs.");
                return;
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate dateAppro = LocalDate.parse(dateApproStr, formatter);
            LocalDate datePeremp = LocalDate.parse(datePerempStr, formatter);

            BigDecimal prixAchat = new BigDecimal(prixAchatStr);
            BigDecimal prixVente = new BigDecimal(prixVenteStr);
            int quantite = Integer.parseInt(quantiteStr);
            if (quantite <= 0) {
                Functions.Alerte_erreur("La quantité d'approvisionnement doit être supérieure à 0.");
                return;
            }

            LocalDate dateDuJour = LocalDate.now();
            if (datePeremp.isBefore(dateDuJour)) {
                Functions.Alerte_erreur("La date de péremption ne peut pas être antérieure à la date du jour ("
                        + dateDuJour.format(formatter) + ").");
                return;
            }

            boolean succes = GestionProduit.ajouterStock(
                    idProduitActuel, dateAppro, datePeremp, prixAchat, quantite, prixVente);

            if (succes) {
                Functions.Alerte_succes("Approvisionnement ajouté avec succès !");
                FenetreGestionProduitControleur.FermerBoiteDialogueApprovisionnementProduit();
            } else {
                Functions.Alerte_erreur("Échec de l'enregistrement de l'approvisionnement.");
            }

        } catch (DateTimeParseException e) {
            Functions.Alerte_erreur("Format de date invalide. Utilisez le format jj/mm/aaaa.");
        } catch (NumberFormatException e) {
            Functions.Alerte_erreur("Format de prix ou de quantité invalide.");
        }
    }

    @FXML
    void AnnulerApprovisionnement(ActionEvent event) {
        FenetreGestionProduitControleur.FermerBoiteDialogueApprovisionnementProduit();
    }

    public void chargerDonnees(Long idProduit) {
        this.idProduitActuel = idProduit; // Stockage de l'ID
        Produit produit = GestionProduit.rechercherProduitParId(idProduit);
        if (produit != null) {
            label_nom_produit.setText(produit.getLibelleProduit());
        }
    }
}