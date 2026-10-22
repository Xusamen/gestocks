package com.cybersek.gestocks;

import java.math.BigDecimal;

import com.cybersek.gestocks.ModuleVente.Commande;
import com.cybersek.gestocks.ModuleVente.Encaissement;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

/**
 * BoiteDialogueEncaissementVenteControleur
 */
public class BoiteDialogueEncaissementVenteControleur {

    @FXML
    private TextField champ_monnaie_rendu;

    @FXML
    private TextField champ_reduction_encaissement;

    @FXML
    private TextField champ_somme_verser;

    @FXML
    private ChoiceBox<String> choicebox_type_encaissement;

    @FXML
    private Text label_cout_total;

    private BigDecimal montantTotalOriginal = BigDecimal.ZERO;
    private long idVenteCourante = -1; // <--- Stockage de l'ID de la vente en cours

    @FXML
    void initialize() {
        champ_monnaie_rendu.setEditable(false);

        champ_somme_verser.textProperty().addListener((observable, oldValue, newValue) -> {
            recalculerMonnaie();
        });

        champ_reduction_encaissement.textProperty().addListener((observable, oldValue, newValue) -> {
            recalculerMonnaie();
        });
    }

    public void chargerDonnees(long idVente) {
        this.idVenteCourante = idVente; // <--- On mémorise l'ID
        BigDecimal montant = Commande.calculerMontantTotalCommande(idVente);
        if (montant != null) {
            montantTotalOriginal = montant;
            label_cout_total.setText(montantTotalOriginal.toPlainString());
            choicebox_type_encaissement.getItems().clear();
            choicebox_type_encaissement.getItems().addAll("Espece", "Mobile Money");
            choicebox_type_encaissement.setValue("Espece"); // Valeur par défaut
        }
    }

    @FXML
    void EncaisserClient() {
        if (idVenteCourante == -1) {
            System.err.println("Aucune vente active à encaisser.");
            return;
        }

        try {
            // 1. Récupérer le type d'encaissement choisi
            String typeEncaissement = choicebox_type_encaissement.getValue();
            if (typeEncaissement == null) {
                typeEncaissement = "Espece";
            }

            // 2. Récupérer la somme versée
            String texteSomme = champ_somme_verser.getText();
            if (texteSomme == null || texteSomme.trim().isEmpty()) {
                System.err.println("Veuillez saisir la somme versée.");
                return;
            }
            BigDecimal sommeVersee = new BigDecimal(texteSomme.trim());

            // 3. Récupérer la réduction (si renseignée)
            BigDecimal reduction = BigDecimal.ZERO;
            String texteReduction = champ_reduction_encaissement.getText();
            if (texteReduction != null && !texteReduction.trim().isEmpty()) {
                reduction = new BigDecimal(texteReduction.trim());
            }

            // 4. Calculer le montant final après réduction
            BigDecimal montantFinal = montantTotalOriginal.subtract(reduction);
            if (montantFinal.compareTo(BigDecimal.ZERO) < 0) {
                montantFinal = BigDecimal.ZERO;
            }

            // 5. Calculer la monnaie rendue
            BigDecimal monnaieRendu = sommeVersee.subtract(montantFinal);
            if (monnaieRendu.compareTo(BigDecimal.ZERO) < 0) {
                System.err.println("Somme versée insuffisante !");
                return;
            }

            // 6. Exécuter l'encaissement en base de données
            boolean succes = Encaissement.encaisserVente(
                idVenteCourante, typeEncaissement, sommeVersee, reduction, montantFinal, monnaieRendu
            );

            if (succes) {
                System.out.println("Paiement validé avec succès !");
                // Fermer la fenêtre de dialogue après succès
                FenetreVenteProduitControleur.FermerBoiteDialogueEncaissementVente();
            } else {
                System.err.println("Échec de l'encaissement.");
            }

        } catch (NumberFormatException e) {
            System.err.println("Erreur de format dans les montants saisis : " + e.getMessage());
        }
    }

    @FXML
    void FermerFenetre() {
        FenetreVenteProduitControleur.FermerBoiteDialogueEncaissementVente();
    }

    private void recalculerMonnaie() {
        try {
            String texteSomme = champ_somme_verser.getText();
            if (texteSomme == null || texteSomme.trim().isEmpty()) {
                champ_monnaie_rendu.clear();
                return;
            }
            BigDecimal sommeVersee = new BigDecimal(texteSomme.trim());

            BigDecimal montantFinal = montantTotalOriginal;
            String texteReduction = champ_reduction_encaissement.getText();
            if (texteReduction != null && !texteReduction.trim().isEmpty()) {
                BigDecimal reduction = new BigDecimal(texteReduction.trim());
                montantFinal = montantTotalOriginal.subtract(reduction);
                if (montantFinal.compareTo(BigDecimal.ZERO) < 0) {
                    montantFinal = BigDecimal.ZERO;
                }
            }

            BigDecimal monnaieRendue = sommeVersee.subtract(montantFinal);

            if (monnaieRendue.compareTo(BigDecimal.ZERO) >= 0) {
                champ_monnaie_rendu.setText(monnaieRendue.toPlainString());
            } else {
                champ_monnaie_rendu.setText("Montant insuffisant");
            }

        } catch (NumberFormatException e) {
            champ_monnaie_rendu.setText("");
        }
    }
}