package com.cybersek.gestocks.ModuleVente;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.cybersek.gestocks.App;
import com.cybersek.gestocks.Functions;
import com.cybersek.gestocks.StructureDonnee.CommandeDetails;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Commande {

    public static long creerVente() {
        String sql = "INSERT INTO vente (statut) VALUES ('INTERROMPUE')";

        try (Connection conn = DriverManager.getConnection(App.url, App.utilisateur, App.motDePasse);
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    long idVente = rs.getLong(1);
                    System.out.println("Vente initialisée en état INTERROMPUE, ID : " + idVente);
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

    public static boolean ajouterProduitCommande(long idVente, long idProduit, int quantiteDemandee,
            BigDecimal prixUnitaire) {
        // Requête pour trouver le stock non périmé le plus ancien (FIFO)
        String sqlSelectStock = "SELECT id, quantite_restante FROM stocks " +
                "WHERE id_produit = ? AND quantite_restante > 0 AND date_peremp >= CURDATE() " +
                "ORDER BY date_appro ASC, date_peremp ASC LIMIT 1";
        String sqlUpdateStock = "UPDATE stocks SET quantite_restante = ? WHERE id = ?";

        // Insertion avec l'id_stock_prov source
        String sqlCommande = "INSERT INTO commande (id_vente, id_produit, id_stock_prov, quantite_produit, prix_unitaire, cout_total) VALUES (?, ?, ?, ?, ?, ?)";
        String sqlUpdateVente = "UPDATE vente SET montant_total = (SELECT COALESCE(SUM(cout_total), 0) FROM commande WHERE id_vente = ?) WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(App.url, App.utilisateur, App.motDePasse)) {
            conn.setAutoCommit(false);

            try {
                long idStockSource = -1;
                int qteRestanteLot = 0;

                // ÉTAPE 1 : Trouver le lot source valide (FIFO + non périmé)
                try (PreparedStatement pstmtSel = conn.prepareStatement(sqlSelectStock)) {
                    pstmtSel.setLong(1, idProduit);
                    try (ResultSet rs = pstmtSel.executeQuery()) {
                        if (rs.next()) {
                            idStockSource = rs.getLong("id");
                            qteRestanteLot = rs.getInt("quantite_restante");
                        }
                    }
                }

                if (idStockSource == -1 || qteRestanteLot < quantiteDemandee) {
                    conn.rollback();
                    System.err.println("Stock insuffisant ou produit périmé pour le produit ID " + idProduit);
                    Functions.Alerte_erreur("Stocks Insuffisant");
                    return false;
                }

                // ÉTAPE 2 : Décrémenter ce stock précis
                int nouvelleQteStock = qteRestanteLot - quantiteDemandee;
                try (PreparedStatement pstmtUp = conn.prepareStatement(sqlUpdateStock)) {
                    pstmtUp.setInt(1, nouvelleQteStock);
                    pstmtUp.setLong(2, idStockSource);
                    pstmtUp.executeUpdate();
                }

                // ÉTAPE 3 : Insérer la commande en enregistrant le lot source (`idStockSource`)
                BigDecimal coutTotal = new BigDecimal(quantiteDemandee).multiply(prixUnitaire);
                try (PreparedStatement pstmtCmd = conn.prepareStatement(sqlCommande)) {
                    pstmtCmd.setLong(1, idVente);
                    pstmtCmd.setLong(2, idProduit);
                    pstmtCmd.setLong(3, idStockSource); // <-- On mémorise le lot d'origine !
                    pstmtCmd.setInt(4, quantiteDemandee);
                    pstmtCmd.setBigDecimal(5, prixUnitaire);
                    pstmtCmd.setBigDecimal(6, coutTotal);
                    pstmtCmd.executeUpdate();
                }

                // ÉTAPE 4 : Mettre à jour le montant total de la vente
                try (PreparedStatement pstmtUpVente = conn.prepareStatement(sqlUpdateVente)) {
                    pstmtUpVente.setLong(1, idVente);
                    pstmtUpVente.setLong(2, idVente);
                    pstmtUpVente.executeUpdate();
                }

                conn.commit();
                System.out.println("Commande ajoutée avec succès depuis le lot stock ID: " + idStockSource);
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
        String sqlGetCommandes = "SELECT id_stock_prov, quantite_produit FROM commande WHERE id_vente = ?";
        String sqlUpdateVente = "UPDATE vente SET statut = 'ANNULEE', montant_total = 0.00 WHERE id = ?";
        String sqlRestaurerStock = "UPDATE stocks SET quantite_restante = LEAST(quantite_appro, quantite_restante + ?) WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(App.url, App.utilisateur, App.motDePasse)) {
            conn.setAutoCommit(false);

            try {
                // ÉTAPE 1 : Restituer chaque quantité dans son lot d'origine (id_stock_prov)
                try (PreparedStatement pstmtGetCmds = conn.prepareStatement(sqlGetCommandes);
                        PreparedStatement pstmtStock = conn.prepareStatement(sqlRestaurerStock)) {

                    pstmtGetCmds.setLong(1, idVente);
                    try (ResultSet rs = pstmtGetCmds.executeQuery()) {
                        while (rs.next()) {
                            long idStockProv = rs.getLong("id_stock_prov");
                            int quantite = rs.getInt("quantite_produit");

                            pstmtStock.setInt(1, quantite);
                            pstmtStock.setLong(2, idStockProv);
                            pstmtStock.executeUpdate();
                        }
                    }
                }

                // ÉTAPE 2 : Mettre à jour le statut de la vente à 'ANNULEE'
                int lignesAffectees = 0;
                try (PreparedStatement pstmtVente = conn.prepareStatement(sqlUpdateVente)) {
                    pstmtVente.setLong(1, idVente);
                    lignesAffectees = pstmtVente.executeUpdate();
                }

                if (lignesAffectees == 0) {
                    conn.rollback();
                    System.err.println("Vente introuvable (ID: " + idVente + ")");
                    return false;
                }

                conn.commit();
                System.out
                        .println("Vente #" + idVente + " annulée et tous les stocks d'origine restaurés avec succès.");
                return true;

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de l'annulation de la vente : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static boolean retirerProduitCommande(long idCommande) {
        // On récupère directement l'id_stock_prov et la quantité depuis la ligne de
        // commande
        String sqlGetInfos = "SELECT id_vente, id_stock_prov, quantite_produit FROM commande WHERE id = ?";
        String sqlDelete = "DELETE FROM commande WHERE id = ?";
        String sqlUpdateVente = "UPDATE vente SET montant_total = (SELECT COALESCE(SUM(cout_total), 0) FROM commande WHERE id_vente = ?) WHERE id = ?";

        // Restituer dans le lot d'origine en s'assurant qu'on ne dépasse pas la
        // quantité initiale (quantite_appro)
        String sqlRestaurerStock = "UPDATE stocks SET quantite_restante = LEAST(quantite_appro, quantite_restante + ?) WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(App.url, App.utilisateur, App.motDePasse)) {
            conn.setAutoCommit(false);
            long idVente = -1;
            long idStock = -1;
            int quantiteARestituer = 0;

            try {
                // ÉTAPE 1 : Récupérer les infos de la ligne de commande
                try (PreparedStatement pstmtGet = conn.prepareStatement(sqlGetInfos)) {
                    pstmtGet.setLong(1, idCommande);
                    try (ResultSet rs = pstmtGet.executeQuery()) {
                        if (rs.next()) {
                            idVente = rs.getLong("id_vente");
                            idStock = rs.getLong("id_stock_prov");
                            quantiteARestituer = rs.getInt("quantite_produit");
                        } else {
                            System.err.println("Ligne de commande introuvable (ID: " + idCommande + ")");
                            return false;
                        }
                    }
                }

                // ÉTAPE 2 : Remettre exactement la quantité dans le lot d'origine
                try (PreparedStatement pstmtStock = conn.prepareStatement(sqlRestaurerStock)) {
                    pstmtStock.setInt(1, quantiteARestituer);
                    pstmtStock.setLong(2, idStock);
                    pstmtStock.executeUpdate();
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
                System.out.println("Produit retiré et stock restitué avec succès dans le lot d'origine (Stock ID: "
                        + idStock + ").");
                return true;

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQL lors du retrait du produit : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static boolean ValiderCommandeVente(long idVente) {
        // On vérifie que la vente est bien 'INTERROMPUE' et qu'elle possède un montant
        // total > 0
        String sqlCheck = "SELECT statut, montant_total FROM vente WHERE id = ?";
        String sqlUpdate = "UPDATE vente SET statut = 'EN_ATTENTE' WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(App.url, App.utilisateur, App.motDePasse)) {
            conn.setAutoCommit(false);

            try {
                // ÉTAPE 1 : Vérification de l'état actuel de la vente
                try (PreparedStatement pstmtCheck = conn.prepareStatement(sqlCheck)) {
                    pstmtCheck.setLong(1, idVente);
                    try (ResultSet rs = pstmtCheck.executeQuery()) {
                        if (rs.next()) {
                            String statut = rs.getString("statut");
                            BigDecimal montantTotal = rs.getBigDecimal("montant_total");

                            if (!"INTERROMPUE".equals(statut)) {
                                System.err.println("Impossible de valider : la vente n'est pas à l'état INTERROMPUE.");
                                conn.rollback();
                                return false;
                            }

                            if (montantTotal.compareTo(BigDecimal.ZERO) <= 0) {
                                System.err.println(
                                        "Impossible de valider : le panier est vide (montant total égal à 0).");
                                conn.rollback();
                                return false;
                            }
                        } else {
                            System.err.println("Vente introuvable (ID: " + idVente + ")");
                            conn.rollback();
                            return false;
                        }
                    }
                }

                // ÉTAPE 2 : Passage du statut à 'EN_ATTENTE'
                try (PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdate)) {
                    pstmtUpdate.setLong(1, idVente);
                    pstmtUpdate.executeUpdate();
                }

                conn.commit();
                System.out.println("Vente #" + idVente + " passée à l'état EN_ATTENTE de paiement.");
                return true;

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQL lors du passage de la vente en EN_ATTENTE : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static BigDecimal calculerMontantTotalCommande(long idVente) {
        String sqlCalculEtUpdate = "UPDATE vente SET montant_total = (" +
                "    SELECT COALESCE(SUM(cout_total), 0.00) " +
                "    FROM commande " +
                "    WHERE id_vente = ? " +
                ") WHERE id = ?";

        String sqlGetMontant = "SELECT montant_total FROM vente WHERE id = ?";

        BigDecimal montantTotal = BigDecimal.ZERO;

        try (Connection conn = DriverManager.getConnection(App.url, App.utilisateur, App.motDePasse)) {

            // Désactiver l'auto-commit pour la transaction
            conn.setAutoCommit(false);

            try {
                // Étape 1 : Mettre à jour le montant total dans la table vente
                try (PreparedStatement pstmtUpdate = conn.prepareStatement(sqlCalculEtUpdate)) {
                    pstmtUpdate.setLong(1, idVente);
                    pstmtUpdate.setLong(2, idVente);
                    pstmtUpdate.executeUpdate();
                }

                // Étape 2 : Récupérer la valeur mise à jour pour la retourner
                try (PreparedStatement pstmtGet = conn.prepareStatement(sqlGetMontant)) {
                    pstmtGet.setLong(1, idVente);
                    try (ResultSet rs = pstmtGet.executeQuery()) {
                        if (rs.next()) {
                            montantTotal = rs.getBigDecimal("montant_total");
                            if (montantTotal == null) {
                                montantTotal = BigDecimal.ZERO;
                            }
                        }
                    }
                }

                // Valider la transaction
                conn.commit();
                System.out.println(
                        "Montant total de la vente #" + idVente + " recalculé avec succès : " + montantTotal + " CFA");
                return montantTotal;

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQL lors du calcul du montant total : " + e.getMessage());
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }
}
