package com.cybersek.gestocks.ModuleStocks;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import com.cybersek.gestocks.App;
import com.cybersek.gestocks.StructureDonnee.CommandeDetails;
import com.cybersek.gestocks.StructureDonnee.InventaireStocks;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Inventaire {

    public static ObservableList<InventaireStocks> listerInventaire() {
        ObservableList<InventaireStocks> liste = FXCollections.observableArrayList();
        String query = "SELECT p.id_produit, p.libelle_produit, p.unite, " +
                "COALESCE(SUM(s.quantite_restante), 0) AS stock_total, " +
                "ROUND(COALESCE(AVG(s.prix_vente_unitaire), 0) * COALESCE(SUM(s.quantite_restante), 0), 2) AS valeur_stock, "
                +
                "MAX(s.date_appro) AS dernier_maj " +
                "FROM produit p " +
                "LEFT JOIN stocks s ON p.id_produit = s.id_produit AND s.date_peremp >= CURDATE() " +
                "GROUP BY p.id_produit, p.libelle_produit, p.unite";

        try (Connection conn = DriverManager.getConnection(App.url, App.utilisateur, App.motDePasse);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Long id = rs.getLong("id_produit");
                String nom = rs.getString("libelle_produit");
                String unite = rs.getString("unite");
                int stockTotal = rs.getInt("stock_total");

                // Récupération de la valeur et sécurisation à 2 décimales avec BigDecimal
                BigDecimal valeurStock = rs.getBigDecimal("valeur_stock");
                if (valeurStock != null) {
                    valeurStock = valeurStock.setScale(2, java.math.RoundingMode.HALF_UP);
                } else {
                    valeurStock = BigDecimal.ZERO.setScale(2);
                }

                Date dateSql = rs.getDate("dernier_maj");
                LocalDate dernierMaj = dateSql != null ? dateSql.toLocalDate() : null;

                liste.add(new InventaireStocks(id, nom, stockTotal, unite, valeurStock, dernierMaj));
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQL Inventaire : " + e.getMessage());
            e.printStackTrace();
        }

        return liste;
    }

    

}
