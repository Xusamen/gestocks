package com.cybersek.gestocks;

import com.cybersek.gestocks.StructureDonnee.InfoProduit;
import com.cybersek.gestocks.StructureDonnee.Produit;
import com.cybersek.gestocks.ModuleStocks.GestionProduit; // ou votre classe de gestion

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

import java.math.BigDecimal;

public class BoiteDialogueInfoProduitControleur {

    @FXML
    private TableColumn<InfoProduit, String> coonne_date_appro;

    @FXML
    private TableColumn<InfoProduit, String> colonne_date_peremption;

    @FXML
    private TableColumn<InfoProduit, BigDecimal> colonne_prix_unitaire_achat;

    @FXML
    private TableColumn<InfoProduit, BigDecimal> colonne_prix_unitaire_vente;

    @FXML
    private TableColumn<InfoProduit, Integer> colonne_quantite_appro;

    @FXML
    private TableColumn<InfoProduit, Integer> colonne_quantite_restante;

    @FXML
    private TableColumn<InfoProduit, String> colonne_unite_appro;

    @FXML
    private Text label_nom_produit;

    @FXML
    private TableView<InfoProduit> tableau_info_produit;

    @FXML
    void FermerFenetre(ActionEvent event) {
        FenetreGestionProduitControleur.FermerBoiteDialogueInfoProduit();
    }

    /**
     * Méthode à appeler lors de l'ouverture de la boîte de dialogue pour charger
     * les données du produit.
     * 
     * @param idProduit L'ID du produit dont on veut voir les infos
     */
    public void chargerDonnees(Long idProduit) {
        // 1. Récupérer et afficher le nom du produit
        Produit nomProduit = GestionProduit.rechercherProduitParId(idProduit);
        if (nomProduit != null) {
            label_nom_produit.setText(nomProduit.getLibelleProduit());
        }

        // 2. Configurer les colonnes
        coonne_date_appro.setCellValueFactory(new PropertyValueFactory<>("dateApproFormat"));
        colonne_date_peremption.setCellValueFactory(new PropertyValueFactory<>("datePeremptionFormat"));
        colonne_prix_unitaire_achat.setCellValueFactory(new PropertyValueFactory<>("prixUnitaireAchat"));
        colonne_prix_unitaire_vente.setCellValueFactory(new PropertyValueFactory<>("prixUnitaireVente"));
        colonne_quantite_appro.setCellValueFactory(new PropertyValueFactory<>("quantiteAppro"));
        colonne_quantite_restante.setCellValueFactory(new PropertyValueFactory<>("quantiteRestante"));
        colonne_unite_appro.setCellValueFactory(new PropertyValueFactory<>("Unite"));


        // 4. Charger et lier les données de la table stocks
        ObservableList<InfoProduit> listeInfos = GestionProduit.listerInfosStocksParProduit(idProduit);
        tableau_info_produit.setItems(listeInfos);
    }

}