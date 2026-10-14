package com.cybersek.gestocks.ModuleStocks;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.cybersek.gestocks.App;
import com.cybersek.gestocks.StructureDonnee.InfoProduit;
import com.cybersek.gestocks.StructureDonnee.Produit;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class GestionProduit {
    public static ObservableList<Produit> listerProduits() {
        ObservableList<Produit> produits = FXCollections.observableArrayList();
        String query = "SELECT id_produit, libelle_produit, date_creation FROM produit";

        try (Connection conn = DriverManager.getConnection(App.url, App.utilisateur, App.motDePasse);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Long id = rs.getLong("id_produit");
                String libelle = rs.getString("libelle_produit");
                Timestamp timestamp = rs.getTimestamp("date_creation");
                LocalDateTime date = timestamp != null ? timestamp.toLocalDateTime() : null;

                produits.add(new Produit(id, libelle, date));
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQL lors du chargement des produits : " + e.getMessage());
            e.printStackTrace();
        }

        return produits;
    }

    public static Produit rechercherProduitParId(Long idRecherche) {
        String query = "SELECT id_produit, libelle_produit, date_creation FROM produit WHERE id_produit = ?";

        try (Connection conn = DriverManager.getConnection(App.url, App.utilisateur, App.motDePasse);
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setLong(1, idRecherche);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Long id = rs.getLong("id_produit");
                    String libelle = rs.getString("libelle_produit");
                    Timestamp timestamp = rs.getTimestamp("date_creation");
                    LocalDateTime date = timestamp != null ? timestamp.toLocalDateTime() : null;
                    return new Produit(id, libelle, date);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la recherche du produit par ID : " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static boolean modifierLibelleProduit(Long idProduit, String nouveauLibelle) {
        String query = "UPDATE produit SET libelle_produit = ? WHERE id_produit = ?";

        try (Connection conn = DriverManager.getConnection(App.url, App.utilisateur, App.motDePasse);
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, nouveauLibelle);
            pstmt.setLong(2, idProduit);

            int lignesAffectees = pstmt.executeUpdate();
            return lignesAffectees > 0;

        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la modification du produit : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static ObservableList<InfoProduit> listerInfosStocksParProduit(Long idProduit) {
        ObservableList<InfoProduit> listeInfos = FXCollections.observableArrayList();
        String query = "SELECT id, date_appro, date_peremp, prix_achat_unitaire, prix_vente_unitaire, quantite_appro, quantite_restante "
                +
                "FROM stocks WHERE id_produit = ? ORDER BY date_appro DESC";

        try (Connection conn = DriverManager.getConnection(App.url, App.utilisateur, App.motDePasse);
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setLong(1, idProduit);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Long idStock = rs.getLong("id");
                    LocalDate dateAppro = rs.getDate("date_appro").toLocalDate();
                    LocalDate datePeremp = rs.getDate("date_peremp").toLocalDate();
                    BigDecimal prixAchat = rs.getBigDecimal("prix_achat_unitaire");
                    BigDecimal prixVente = rs.getBigDecimal("prix_vente_unitaire");
                    int qteAppro = rs.getInt("quantite_appro");
                    int qteRestante = rs.getInt("quantite_restante");

                    listeInfos.add(new InfoProduit(idStock, dateAppro, datePeremp, prixAchat, prixVente, qteAppro,
                            qteRestante));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors du chargement des infos de stocks : " + e.getMessage());
            e.printStackTrace();
        }

        return listeInfos;
    }

    public static boolean ajouterStock(Long idProduit, LocalDate dateAppro, LocalDate datePeremp,
            BigDecimal prixAchat, int quantiteAppro, BigDecimal prixVente) {
        String query = "INSERT INTO stocks (id_produit, date_appro, date_peremp, prix_achat_unitaire, quantite_appro, prix_vente_unitaire, quantite_restante) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(App.url, App.utilisateur, App.motDePasse);
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setLong(1, idProduit);
            pstmt.setDate(2, java.sql.Date.valueOf(dateAppro));
            pstmt.setDate(3, java.sql.Date.valueOf(datePeremp));
            pstmt.setBigDecimal(4, prixAchat);
            pstmt.setInt(5, quantiteAppro);
            pstmt.setBigDecimal(6, prixVente);
            pstmt.setInt(7, quantiteAppro); // À l'approvisionnement, la quantité restante est égale à la quantité appro

            int lignesAffectees = pstmt.executeUpdate();
            return lignesAffectees > 0;

        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de l'ajout du stock : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static boolean insererProduit(String libelle) {
        String query = "INSERT INTO produit (libelle_produit) VALUES (?)";

        try (Connection conn = DriverManager.getConnection(App.url, App.utilisateur, App.motDePasse);
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            // Injection du libellé dans le paramètre de la requête
            pstmt.setString(1, libelle);

            int lignesAffectees = pstmt.executeUpdate();
            return lignesAffectees > 0;

        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de l'insertion du produit : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
