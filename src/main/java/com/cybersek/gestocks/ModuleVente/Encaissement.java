package com.cybersek.gestocks.ModuleVente;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;

import com.cybersek.gestocks.App;
import com.cybersek.gestocks.StructureDonnee.InfoEncaissement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Encaissement {

    public static ObservableList<InfoEncaissement> ListerTableauPourEncaissement() {
        ObservableList<InfoEncaissement> ventesList = FXCollections.observableArrayList();

        // Requête mise à jour avec le format : "produit xN || produit xN"
        // On effectue une jointure sur la table 'produit' pour récupérer le libellé via
        // id_produit
        String query = "SELECT v.id AS id_vente, " +
                "DATE_FORMAT(v.date_vente, '%d/%m/%Y') AS date_f, " +
                "TIME_FORMAT(v.date_vente, '%H:%i:%s') AS heure_f, " +
                "GROUP_CONCAT(CONCAT(p.libelle_produit, ' x', c.quantite_produit) SEPARATOR ' || ') AS commande_client, "
                +
                "v.montant_total, v.statut " +
                "FROM vente v " +
                "LEFT JOIN commande c ON v.id = c.id_vente " +
                "LEFT JOIN produit p ON c.id_produit = p.id_produit " +
                "WHERE v.statut = 'EN_ATTENTE' " +
                "GROUP BY v.id, v.date_vente, v.montant_total, v.statut " +
                "ORDER BY v.date_vente ASC";

        try (Connection conn = DriverManager.getConnection(App.url, App.utilisateur, App.motDePasse);
                PreparedStatement pstmt = conn.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                long idVente = rs.getLong("id_vente");
                String dateVente = rs.getString("date_f");
                String heureVente = rs.getString("heure_f");
                String commandeClient = rs.getString("commande_client");
                BigDecimal montantTotal = rs.getBigDecimal("montant_total");
                String statutTransaction = rs.getString("statut");

                InfoEncaissement vente = new InfoEncaissement(
                        idVente, dateVente, heureVente,
                        commandeClient != null ? commandeClient : "Panier vide",
                        montantTotal,
                        statutTransaction);
                ventesList.add(vente);
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQL lors du chargement des ventes en attente : " + e.getMessage());
            e.printStackTrace();
        }

        return ventesList;
    }

    public static boolean AnnulerEncaissement(long idVente) {
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

    public static boolean encaisserVente(long idVente, String typeEncaissement,
            BigDecimal montantVerser, BigDecimal reduction,
            BigDecimal montantFinal, BigDecimal monnaieRendu) {

        int anneeCourante = LocalDate.now().getYear();

        // Requête pour compter combien de reçus ont déjà été émis cette année
        String sqlCountRecu = "SELECT COUNT(*) FROM vente WHERE YEAR(date_vente) = ? AND numero_recu IS NOT NULL";

        String sqlUpdate = "UPDATE vente SET " +
                "type_encaissement = ?, " +
                "montant_total = ?, " +
                "montant_verser = ?, " +
                "monnaie_rendu = ?, " +
                "reduction_appliquer = ?, " +
                "numero_recu = ?, " +
                "statut = 'PAYEE' " +
                "WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(App.url, App.utilisateur, App.motDePasse)) {
            conn.setAutoCommit(false);

            try {
                int sequenceNumber = 1;

                // Étape 1 : Calculer le prochain numéro séquentiel pour l'année en cours
                try (PreparedStatement pstmtCount = conn.prepareStatement(sqlCountRecu)) {
                    pstmtCount.setInt(1, anneeCourante);
                    try (ResultSet rs = pstmtCount.executeQuery()) {
                        if (rs.next()) {
                            sequenceNumber = rs.getInt(1) + 1; // S'il y en a 5, le prochain est 6
                        }
                    }
                }

                // Étape 2 : Formater le numéro de reçu (Ex: G2026REC00001, puis G2027REC00001
                // en 2027)
                String numeroRecu = String.format("G%dREC%05d", anneeCourante, sequenceNumber);

                // Étape 3 : Mettre à jour la vente
                try (PreparedStatement pstmt = conn.prepareStatement(sqlUpdate)) {
                    pstmt.setString(1, typeEncaissement);
                    pstmt.setBigDecimal(2, montantFinal);
                    pstmt.setBigDecimal(3, montantVerser);
                    pstmt.setBigDecimal(4, monnaieRendu);
                    pstmt.setBigDecimal(5, reduction != null ? reduction : BigDecimal.ZERO);
                    pstmt.setString(6, numeroRecu);
                    pstmt.setLong(7, idVente);

                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        conn.commit();
                        System.out.println("Encaissement réussi pour la vente #" + idVente + " - Reçu : " + numeroRecu);
                        return true;
                    } else {
                        conn.rollback();
                        System.err.println("Vente introuvable pour l'encaissement (ID: " + idVente + ")");
                        return false;
                    }
                }

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de l'encaissement : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

}
