package com.cybersek.gestocks.ModuleVente;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import com.cybersek.gestocks.App;
import com.cybersek.gestocks.StructureDonnee.TableauBilanAnnuel;
import com.cybersek.gestocks.StructureDonnee.TableauBilanJournalier;
import com.cybersek.gestocks.StructureDonnee.TableauBilanMensuel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class BilanVente {
    public static ObservableList<Integer> listerAnneesDisponibles() {
        ObservableList<Integer> anneesList = FXCollections.observableArrayList();
        String query = "SELECT DISTINCT YEAR(date_vente) AS annee FROM vente WHERE statut = 'PAYEE' ORDER BY annee DESC";

        try (Connection conn = DriverManager.getConnection(App.url, App.utilisateur, App.motDePasse);
                PreparedStatement pstmt = conn.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                anneesList.add(rs.getInt("annee"));
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL (années) : " + e.getMessage());
        }
        return anneesList;
    }

    public static ObservableList<TableauBilanJournalier> listerBilanJournalier(String dateFiltre) {
        ObservableList<TableauBilanJournalier> list = FXCollections.observableArrayList();

        StringBuilder query = new StringBuilder(
                "SELECT DATE(date_vente) AS date_v, SUM(montant_total) AS ca " +
                        "FROM vente WHERE statut = 'PAYEE' ");

        if (dateFiltre != null && !dateFiltre.trim().isEmpty()) {
            // Utilisation de LIKE pour permettre le filtrage au fil de la frappe
            query.append("AND DATE_FORMAT(date_vente, '%d/%m/%Y') LIKE ? ");
        }
        query.append("GROUP BY DATE(date_vente) ORDER BY date_v DESC");

        try (Connection conn = DriverManager.getConnection(App.url, App.utilisateur, App.motDePasse);
                PreparedStatement pstmt = conn.prepareStatement(query.toString())) {

            if (dateFiltre != null && !dateFiltre.trim().isEmpty()) {
                pstmt.setString(1, "%" + dateFiltre.trim() + "%");
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    LocalDate date = rs.getDate("date_v").toLocalDate();
                    BigDecimal ca = rs.getBigDecimal("ca");
                    list.add(new TableauBilanJournalier(date, ca));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL (bilan journalier) : " + e.getMessage());
        }
        return list;
    }

    // 3. Bilan Mensuel pour une année donnée
    public static ObservableList<TableauBilanMensuel> listerBilanMensuel(int annee) {
        ObservableList<TableauBilanMensuel> list = FXCollections.observableArrayList();
        String query = "SELECT MONTH(date_vente) AS mois, SUM(montant_total) AS ca " +
                "FROM vente WHERE statut = 'PAYEE' AND YEAR(date_vente) = ? " +
                "GROUP BY MONTH(date_vente) ORDER BY mois ASC";

        try (Connection conn = DriverManager.getConnection(App.url, App.utilisateur, App.motDePasse);
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, annee);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int moisNum = rs.getInt("mois");
                    BigDecimal ca = rs.getBigDecimal("ca");
                    // Transformation du numéro de mois en libellé (ex: Janvier, Février...)
                    String moisNom = java.time.Month.of(moisNum).getDisplayName(java.time.format.TextStyle.FULL,
                            java.util.Locale.FRENCH);
                    list.add(new TableauBilanMensuel(moisNom, ca));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL (bilan mensuel) : " + e.getMessage());
        }
        return list;
    }

    // 4. Bilan Annuel global
    public static ObservableList<TableauBilanAnnuel> listerBilanAnnuel() {
        ObservableList<TableauBilanAnnuel> list = FXCollections.observableArrayList();
        String query = "SELECT YEAR(date_vente) AS annee, SUM(montant_total) AS ca " +
                "FROM vente WHERE statut = 'PAYEE' " +
                "GROUP BY YEAR(date_vente) ORDER BY annee DESC";

        try (Connection conn = DriverManager.getConnection(App.url, App.utilisateur, App.motDePasse);
                PreparedStatement pstmt = conn.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int annee = rs.getInt("annee");
                BigDecimal ca = rs.getBigDecimal("ca");
                list.add(new TableauBilanAnnuel(annee, ca));
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL (bilan annuel) : " + e.getMessage());
        }
        return list;
    }
}
