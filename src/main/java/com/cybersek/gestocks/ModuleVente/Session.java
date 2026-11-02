package com.cybersek.gestocks.ModuleVente;

import java.sql.*;
import java.time.LocalDateTime;

import com.cybersek.gestocks.App;
import com.cybersek.gestocks.StructureDonnee.SessionVente;

public class Session {

    // 1. Récupérer la session en cours (IN_PROGRESS) ou la prochaine disponible (UNUSED)
    public static SessionVente recupererSessionActuelleOuSuivante() {
        SessionVente session = null;
        
        // On cherche d'abord s'il y a une session en cours
        String query = "SELECT * FROM session_vente WHERE etat = 'IN_PROGRESS' LIMIT 1";

        try (Connection conn = DriverManager.getConnection(App.url, App.utilisateur, App.motDePasse);
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                session = mapResultSetToSession(rs);
            } else {
                // S'il n'y a pas de session en cours, on récupère la prochaine session UNUSED (la plus proche)
                String queryUnused = "SELECT * FROM session_vente WHERE etat = 'UNUSED' ORDER BY date_vente ASC LIMIT 1";
                try (PreparedStatement pstmtUnused = conn.prepareStatement(queryUnused);
                     ResultSet rsUnused = pstmtUnused.executeQuery()) {
                    if (rsUnused.next()) {
                        session = mapResultSetToSession(rsUnused);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL (récupération session) : " + e.getMessage());
            e.printStackTrace();
        }

        return session;
    }

    // 2. Changer l'état d'une session (Lancer = IN_PROGRESS / Arrêter = CLOSED)
    public static boolean changerEtatSession(Long sessionId, String nouvelEtat, int userId) {
        StringBuilder query = new StringBuilder("UPDATE session_vente SET etat = ? ");
        
        // Si on lance la session, on enregistre user_start. Si on l'arrête, on enregistre user_stop.
        if ("IN_PROGRESS".equals(nouvelEtat)) {
            query.append(", user_start = ? ");
        } else if ("CLOSED".equals(nouvelEtat)) {
            query.append(", user_stop = ? ");
        }
        query.append("WHERE id = ?");

        try (Connection conn = DriverManager.getConnection(App.url, App.utilisateur, App.motDePasse);
             PreparedStatement pstmt = conn.prepareStatement(query.toString())) {

            pstmt.setString(1, nouvelEtat);
            pstmt.setInt(2, userId);
            pstmt.setLong(3, sessionId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Erreur SQL (changement état session) : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Méthode utilitaire pour mapper le ResultSet vers l'objet SessionVente
    private static SessionVente mapResultSetToSession(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        LocalDateTime dateVente = rs.getTimestamp("date_vente").toLocalDateTime();
        String description = rs.getString("description_session");
        String etat = rs.getString("etat");
        
        Integer userStart = rs.getObject("user_start") != null ? rs.getInt("user_start") : null;
        Integer userStop = rs.getObject("user_stop") != null ? rs.getInt("user_stop") : null;

        return new SessionVente(id, dateVente, description, etat, userStart, userStop);
    }
}