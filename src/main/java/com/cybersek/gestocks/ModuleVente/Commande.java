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

    public static boolean ajouterProduitCommande(long idVente, long idProduit, int quantiteRestante,
            BigDecimal prixUnitaire) {
        BigDecimal coutTotal = new BigDecimal(quantiteRestante).multiply(prixUnitaire);

        String sqlCommande = "INSERT INTO commande (id_vente, id_produit, quantite_produit, prix_unitaire, cout_total) VALUES (?, ?, ?, ?, ?)";
        String sqlUpdateVente = "UPDATE vente SET montant_total = (SELECT COALESCE(SUM(cout_total), 0) FROM commande WHERE id_vente = ?) WHERE id = ?";

        // Requête pour trouver les lots de stock disponibles, non périmés, du plus
        // ancien au plus récent
        String sqlSelectStock = "SELECT id, quantite_restante FROM stocks " +
                "WHERE id_produit = ? AND quantite_restante > 0 AND date_peremp >= CURDATE() " +
                "ORDER BY date_appro ASC, date_peremp ASC";
        String sqlUpdateStock = "UPDATE stocks SET quantite_restante = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(App.url, App.utilisateur, App.motDePasse)) {
            conn.setAutoCommit(false);

            try {
                // ÉTAPE 1 : Gestion du stock (Décrémentation FIFO)
                int quantiteAjuster = quantiteRestante;

                try (PreparedStatement pstmtStockSelect = conn.prepareStatement(sqlSelectStock);
                        PreparedStatement pstmtStockUpdate = conn.prepareStatement(sqlUpdateStock)) {

                    pstmtStockSelect.setLong(1, idProduit);
                    try (ResultSet rsStock = pstmtStockSelect.executeQuery()) {
                        while (rsStock.next() && quantiteAjuster > 0) {
                            long idStockLot = rsStock.getLong("id");
                            int qteRestanteLot = rsStock.getInt("quantite_restante");

                            int qteAPrelever = Math.min(quantiteAjuster, qteRestanteLot);
                            int nouvelleQteStock = qteRestanteLot - qteAPrelever;

                            // Mettre à jour le lot de stock
                            pstmtStockUpdate.setInt(1, nouvelleQteStock);
                            pstmtStockUpdate.setLong(2, idStockLot);
                            pstmtStockUpdate.executeUpdate();

                            quantiteAjuster -= qteAPrelever;
                        }
                    }
                }

                // Si le stock disponible était insuffisant
                if (quantiteAjuster > 0) {
                    conn.rollback();
                    System.err.println("Stock insuffisant ou produits périmés pour le produit ID " + idProduit);
                    return false;
                }

                // ÉTAPE 2 : Insérer la commande
                try (PreparedStatement pstmtCmd = conn.prepareStatement(sqlCommande)) {
                    pstmtCmd.setLong(1, idVente);
                    pstmtCmd.setLong(2, idProduit);
                    pstmtCmd.setInt(3, quantiteRestante);
                    pstmtCmd.setBigDecimal(4, prixUnitaire);
                    pstmtCmd.setBigDecimal(5, coutTotal);
                    pstmtCmd.executeUpdate();
                }

                // ÉTAPE 3 : Mettre à jour le montant total de la vente
                try (PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdateVente)) {
                    pstmtUpdate.setLong(1, idVente);
                    pstmtUpdate.setLong(2, idVente);
                    pstmtUpdate.executeUpdate();
                }

                conn.commit();
                System.out.println("Commande ajoutée et stock mis à jour pour le produit ID " + idProduit);
                return true;

            } catch (SQLException e) {
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
        String sqlGetInfosCommande = "SELECT id_vente, id_produit, quantite_produit FROM commande WHERE id = ?";
        String sqlDelete = "DELETE FROM commande WHERE id = ?";
        String sqlUpdateVente = "UPDATE vente SET montant_total = (SELECT COALESCE(SUM(cout_total), 0) FROM commande WHERE id_vente = ?) WHERE id = ?";

        // Pour réinjecter le stock (on cherche un lot actif du produit pour le
        // réalimenter)
        String sqlSelectStockRestauration = "SELECT id, quantite_restante, quantite_appro FROM stocks " +
                "WHERE id_produit = ? AND date_peremp >= CURDATE() " +
                "ORDER BY date_appro DESC LIMIT 1";
        String sqlUpdateStockRestaurer = "UPDATE stocks SET quantite_restante = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(App.url, App.utilisateur, App.motDePasse)) {
            conn.setAutoCommit(false);
            long idVente = -1;
            long idProduit = -1;
            int quantiteARestituer = 0;

            try {
                // ÉTAPE 1 : Récupérer les informations de la ligne avant suppression
                try (PreparedStatement pstmtGet = conn.prepareStatement(sqlGetInfosCommande)) {
                    pstmtGet.setLong(1, idCommande);
                    try (ResultSet rs = pstmtGet.executeQuery()) {
                        if (rs.next()) {
                            idVente = rs.getLong("id_vente");
                            idProduit = rs.getLong("id_produit");
                            quantiteARestituer = rs.getInt("quantite_produit");
                        } else {
                            System.err.println("Ligne de commande introuvable (ID: " + idCommande + ")");
                            return false;
                        }
                    }
                }

                // ÉTAPE 2 : Remettre la quantité dans le stock
                try (PreparedStatement pstmtStockSel = conn.prepareStatement(sqlSelectStockRestauration);
                        PreparedStatement pstmtStockUp = conn.prepareStatement(sqlUpdateStockRestaurer)) {

                    pstmtStockSel.setLong(1, idProduit);
                    try (ResultSet rsStock = pstmtStockSel.executeQuery()) {
                        if (rsStock.next()) {
                            long idStockLot = rsStock.getLong("id");
                            int qteRestante = rsStock.getInt("quantite_restante");
                            int qteAppro = rsStock.getInt("quantite_appro");

                            // Ne pas dépasser la quantité initiale approvisionnée du lot
                            int nouvelleQte = Math.min(qteAppro, qteRestante + quantiteARestituer);

                            pstmtStockUp.setInt(1, nouvelleQte);
                            pstmtStockUp.setLong(2, idStockLot);
                            pstmtStockUp.executeUpdate();
                        } else {
                            // S'il n'y a plus de lot actif non périmé, on peut éventuellement rouvrir ou
                            // créer,
                            // mais généralement on réincrémente le dernier lot existant même s'il est
                            // périmé de justesse,
                            // ou on gère selon votre règle métier. Ici on sécurise en cherchant n'importe
                            // quel lot récent.
                            System.err.println("Attention : Aucun lot actif trouvé pour restituer le stock du produit "
                                    + idProduit);
                        }
                    }
                }

                // ÉTAPE 3 : Supprimer la ligne de commande
                try (PreparedStatement pstmtDelete = conn.prepareStatement(sqlDelete)) {
                    pstmtDelete.setLong(1, idCommande);
                    pstmtDelete.executeUpdate();
                }

                // ÉTAPE 4 : Mettre à jour le montant total de la vente
                try (PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdateVente)) {
                    pstmtUpdate.setLong(1, idVente);
                    pstmtUpdate.setLong(2, idVente);
                    pstmtUpdate.executeUpdate();
                }

                conn.commit();
                System.out.println("Produit retiré de la commande et stock restitué avec succès.");
                return true;

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }

        } catch (SQLException e) {
            System.err
                    .println("Erreur SQL lors du retrait du produit et de la mise à jour du stock : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

}
