package com.cybersek.gestocks.ModuleVente;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.cybersek.gestocks.App;
import com.cybersek.gestocks.StructureDonnee.CommandeDetails;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Commande {

    public static long creerVente() {
        String sql = "INSERT INTO vente (statut) VALUES ('NON_PAYEE')";

        try (Connection conn = DriverManager.getConnection(App.url, App.utilisateur, App.motDePasse);
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    long idVente = rs.getLong(1);
                    System.out.println("Vente créée avec succès, ID : " + idVente);
                    return idVente;
                } else {
                    System.err.println("Échec de la création de la vente : aucun ID généré.");
                    return -1;
                }
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la création de la vente : " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    public static boolean ajouterProduitCommande(long idVente, long idProduit, int quantite, BigDecimal prixUnitaire) {
        BigDecimal coutTotal = new BigDecimal(quantite).multiply(prixUnitaire);

        String sqlCommande = "INSERT INTO commande (id_vente, id_produit, quantite_produit, prix_unitaire, cout_total) VALUES (?, ?, ?, ?, ?)";
        String sqlUpdateVente = "UPDATE vente SET montant_total = (SELECT COALESCE(SUM(cout_total), 0) FROM commande WHERE id_vente = ?) WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(App.url, App.utilisateur, App.motDePasse)) {

            // Désactiver l'auto-commit pour gérer la transaction (insertion + mise à jour
            // du total)
            conn.setAutoCommit(false);

            try {
                // Étape A : Insérer la commande
                try (PreparedStatement pstmtCmd = conn.prepareStatement(sqlCommande)) {
                    pstmtCmd.setLong(1, idVente);
                    pstmtCmd.setLong(2, idProduit);
                    pstmtCmd.setInt(3, quantite);
                    pstmtCmd.setBigDecimal(4, prixUnitaire);
                    pstmtCmd.setBigDecimal(5, coutTotal);
                    pstmtCmd.executeUpdate();
                }

                // Étape B : Mettre à jour le montant total de la vente
                try (PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdateVente)) {
                    pstmtUpdate.setLong(1, idVente);
                    pstmtUpdate.setLong(2, idVente);
                    pstmtUpdate.executeUpdate();
                }

                // Valider la transaction
                conn.commit();
                System.out.println("Commande ajoutée avec succès pour le produit ID " + idProduit);
                return true;

            } catch (SQLException e) {
                // En cas d'erreur, annuler les opérations
                conn.rollback();
                throw e;
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de l'ajout de la commande : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static ObservableList<CommandeDetails> listerCommandesParVente(long idVente) {
        ObservableList<CommandeDetails> listeCommandes = FXCollections.observableArrayList();

        // Requête avec jointure pour récupérer le libellé du produit
        String query = "SELECT c.id, c.id_produit, p.libelle_produit, c.quantite_produit, c.prix_unitaire, c.cout_total "
                +
                "FROM commande c " +
                "JOIN produit p ON c.id_produit = p.id_produit " +
                "WHERE c.id_vente = ?";

        try (Connection conn = DriverManager.getConnection(App.url, App.utilisateur, App.motDePasse);
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setLong(1, idVente);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    long idCommande = rs.getLong("id");
                    long idProduit = rs.getLong("id_produit");
                    String libelleProduit = rs.getString("libelle_produit");
                    int quantite = rs.getInt("quantite_produit");
                    BigDecimal prixUnitaire = rs.getBigDecimal("prix_unitaire");
                    BigDecimal coutTotal = rs.getBigDecimal("cout_total");

                    // Création de l'objet et ajout à la liste
                    CommandeDetails ligne = new CommandeDetails(
                            idCommande, idProduit, libelleProduit, quantite, prixUnitaire, coutTotal);
                    listeCommandes.add(ligne);
                }
            }

        } catch (SQLException e) {
            System.err.println(
                    "Erreur SQL lors de la récupération des commandes de la vente " + idVente + " : " + e.getMessage());
            e.printStackTrace();
        }

        return listeCommandes;
    }

    public static boolean annulerVenteEntiere(long idVente) {
        String query = "UPDATE vente SET statut = 'ANNULEE', montant_total = 0.00 WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(App.url, App.utilisateur, App.motDePasse);
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setLong(1, idVente);
            int lignesAffectees = pstmt.executeUpdate();

            if (lignesAffectees > 0) {
                System.out.println("La vente #" + idVente + " a été marquée comme ANNULEE.");
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de l'annulation de la vente : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static boolean retirerProduitCommande(long idCommande) {
        String sqlGetVenteId = "SELECT id_vente FROM commande WHERE id = ?";
        String sqlDelete = "DELETE FROM commande WHERE id = ?";
        String sqlUpdateVente = "UPDATE vente SET montant_total = (SELECT COALESCE(SUM(cout_total), 0) FROM commande WHERE id_vente = ?) WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(App.url, App.utilisateur, App.motDePasse)) {

            // Désactiver l'auto-commit pour la transaction
            conn.setAutoCommit(false);
            long idVente = -1;

            try {
                // 1. Récupérer l'ID de la vente avant de supprimer la ligne
                try (PreparedStatement pstmtGet = conn.prepareStatement(sqlGetVenteId)) {
                    pstmtGet.setLong(1, idCommande);
                    try (ResultSet rs = pstmtGet.executeQuery()) {
                        if (rs.next()) {
                            idVente = rs.getLong("id_vente");
                        } else {
                            System.err.println("Ligne de commande introuvable (ID: " + idCommande + ")");
                            return false;
                        }
                    }
                }

                // 2. Supprimer la ligne de commande correspondante
                try (PreparedStatement pstmtDelete = conn.prepareStatement(sqlDelete)) {
                    pstmtDelete.setLong(1, idCommande);
                    pstmtDelete.executeUpdate();
                }

                // 3. Mettre à jour le montant total de la vente restante
                try (PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdateVente)) {
                    pstmtUpdate.setLong(1, idVente);
                    pstmtUpdate.setLong(2, idVente);
                    pstmtUpdate.executeUpdate();
                }

                // Valider la transaction
                conn.commit();
                System.out
                        .println("Produit retiré avec succès. Le total de la vente #" + idVente + " a été mis à jour.");
                return true;

            } catch (SQLException e) {
                // En cas d'erreur, on annule tout
                conn.rollback();
                throw e;
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQL lors du retrait du produit : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

}
