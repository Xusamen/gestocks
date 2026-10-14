package com.cybersek.gestocks.ModuleParametre;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.mindrot.jbcrypt.BCrypt;

import com.cybersek.gestocks.App;
import com.cybersek.gestocks.NomUser;

public class VerificationUser {

    public static boolean Check_MotDePasse(String password, String hashed) {
        return BCrypt.checkpw(password, hashed);
    }

    public static String[] Deconcatener(String suiteDEmots) {
        return suiteDEmots.split("//");
    }

    public static boolean Verification_Identifiant_Connexion(String userIdentifiant, String motDePasse) {
        String requete = "SELECT id_utilisateur, nom, prenom, fonction, mot_de_passe FROM utilisateurs WHERE identifiant = ?";

        try (Connection connexion = DriverManager.getConnection(App.url, App.utilisateur, App.motDePasse);
                PreparedStatement statement = connexion.prepareStatement(requete)) {

            statement.setString(1, userIdentifiant);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String hashStocke = resultSet.getString("mot_de_passe");

                    if (Check_MotDePasse(motDePasse, hashStocke)) {
                        NomUser.setUserInfo(
                                resultSet.getInt("id_utilisateur"),
                                resultSet.getString("nom"),
                                resultSet.getString("prenom"),
                                resultSet.getString("fonction")
                        );
                        return true;
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return false;
    }

    // une connexion réussie
    public static boolean EnregistrerConnexionSucces(Integer idUtilisateur, String identifiantTente, String adresseIp) {
        String requete = "INSERT INTO historique_connexions (id_utilisateur, identifiant_tente, succes, adresse_ip) "
                + "VALUES (?, ?, 1, ?)";

        try (Connection connexion = DriverManager.getConnection(App.url, App.utilisateur, App.motDePasse);
                PreparedStatement statement = connexion.prepareStatement(requete)) {

            if (idUtilisateur != null) {
                statement.setInt(1, idUtilisateur);
            } else {
                statement.setNull(1, Types.INTEGER);
            }
            statement.setString(2, identifiantTente);
            statement.setString(3, adresseIp);

            return statement.executeUpdate() > 0;

        } catch (SQLException erreur) {
            System.err.println(erreur.getMessage());
        }
        return false;
    }

    // Enregistrer une connexion échouée
    public static boolean EnregistrerConnexionEchec(String identifiantTente, String adresseIp) {
        String requete = "INSERT INTO historique_connexions (id_utilisateur, identifiant_tente, succes, adresse_ip) "
                + "VALUES (NULL, ?, 0, ?)";

        try (Connection connexion = DriverManager.getConnection(App.url, App.utilisateur, App.motDePasse);
                PreparedStatement statement = connexion.prepareStatement(requete)) {

            statement.setString(1, identifiantTente);
            statement.setString(2, adresseIp);

            return statement.executeUpdate() > 0;

        } catch (SQLException erreur) {
            System.err.println(erreur.getMessage());
        }
        return false;
    }

}
