package com.cybersek.gestocks;

import java.math.BigDecimal;

import com.cybersek.gestocks.StructureDonnee.InfoProduit;
import com.cybersek.gestocks.StructureDonnee.Produit;
import com.cybersek.gestocks.ModuleStocks.GestionProduit;
import com.cybersek.gestocks.ModuleVente.Commande;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class BoiteDialogueAjouterProduitCommandeControleur {

    private Long id_produit;
    private BigDecimal prix_unitaire_produit;

    int quantite = 0;
    @FXML
    private Text champ_nom_produit;

    @FXML
    private TextField champ_quantite;

    public void initialize() {
        champ_quantite.setText("0");
    }

    @FXML
    void Ajouter_Produit(ActionEvent event) {
        long id_vente = FenetreVenteProduitControleur.getID_VENTE();
        if (id_vente == -1){
            FenetreVenteProduitControleur.setID_VENTE(Commande.creerVente());
        }
        id_vente = FenetreVenteProduitControleur.getID_VENTE();
        int quantite_produit = Integer.parseInt(champ_quantite.getText().trim());
        boolean statut = Commande.ajouterProduitCommande(id_vente, id_produit, quantite_produit, prix_unitaire_produit);
        if (statut){
            FenetreVenteProduitControleur.FermerBoiteDialogueAjouterProduitCommande();
        }

    }

    @FXML
    void Fermer_Fenetre(ActionEvent event) {
        FenetreVenteProduitControleur.FermerBoiteDialogueAjouterProduitCommande();

    }

    @FXML
    void ajouter_quantité(ActionEvent event) {
        quantite++;
        champ_quantite.setText(String.valueOf(quantite));
    }

    @FXML
    void diminuer_quantite(ActionEvent event) {
        quantite--;
        champ_quantite.setText(String.valueOf(quantite));
    }

    public void chargerDonnees(Long idProduit) {
        this.id_produit = idProduit; // Stockage de l'ID
        
        Produit produit = GestionProduit.rechercherProduitParId(idProduit);
        InfoProduit produit2 = GestionProduit.listerInfosProduit(idProduit);
        if (produit != null) {
            this.prix_unitaire_produit = produit2.getPrixUnitaireVente();
            champ_nom_produit.setText(produit.getLibelleProduit());
        }
    }

}
