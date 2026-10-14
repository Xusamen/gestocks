package com.cybersek.gestocks.ModuleParametre;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mindrot.jbcrypt.BCrypt;

import com.cybersek.gestocks.App;
import com.cybersek.gestocks.Functions;
import com.cybersek.gestocks.StructureDonnee;
import com.cybersek.gestocks.StructureDonnee.Utilisateur;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class GestionUtilisateur {

    public static String Hash_MotDePasse(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean CreerUtilisateur(String nom, String prenom, String identifiant, String motDePasse,
            String fonction) {
        String requete = "INSERT INTO utilisateurs (nom, prenom, identifiant, mot_de_passe, fonction) "
                + "VALUES (?, ?, ?, ?, ?)";

        // Hachage du mot de passe brut avant enregistrement
        String motDePasseHache = Hash_MotDePasse(motDePasse);

        try (Connection connexion = DriverManager.getConnection(App.url, App.utilisateur, App.motDePasse);
                PreparedStatement statement = connexion.prepareStatement(requete)) {

            statement.setString(1, nom);
            statement.setString(2, prenom);
            statement.setString(3, identifiant);
            statement.setString(4, motDePasseHache);
            statement.setString(5, fonction);
            Functions.Alerte_succes("Utilisateur "+identifiant+" creer");
            return statement.executeUpdate() > 0;

        } catch (SQLException erreur) {
            Functions.Alerte_erreur("Erreur rencontrer lors de  la creation de l'utilisateur "+identifiant);
            System.err.println(erreur.getMessage());
        }
        return false;
    }

    // Charge un utilisateur à partir de son ID
    public static Utilisateur ObtenirUtilisateurParId(int idUtilisateur) {
        String requete = "SELECT id_utilisateur, nom, prenom, identifiant, fonction, actif FROM utilisateurs WHERE id_utilisateur = ?";

        try (Connection connexion = DriverManager.getConnection(App.url, App.utilisateur, App.motDePasse);
                PreparedStatement statement = connexion.prepareStatement(requete)) {

            statement.setInt(1, idUtilisateur);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return new StructureDonnee.Utilisateur(
                            rs.getInt("id_utilisateur"),
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("identifiant"),
                            rs.getString("fonction"),
                            rs.getInt("actif"));
                }
            }
        } catch (SQLException erreur) {
            System.err.println(erreur.getMessage());
        }
        return null;
    }

    // Modifie l'utilisateur (avec ou sans changement de mot de passe)
    public static boolean ModifierUtilisateur(int idUtilisateur, String nom, String prenom, String identifiant,
            String fonction, String nouveauMotDePasse) {
        boolean modifierMdp = nouveauMotDePasse != null && !nouveauMotDePasse.trim().isEmpty();

        // Adapte la requête SQL selon si le mot de passe a été saisi ou non
        String requete = modifierMdp
                ? "UPDATE utilisateurs SET nom = ?, prenom = ?, identifiant = ?, fonction = ?, mot_de_passe = ? WHERE id_utilisateur = ?"
                : "UPDATE utilisateurs SET nom = ?, prenom = ?, identifiant = ?, fonction = ? WHERE id_utilisateur = ?";

        try (Connection connexion = DriverManager.getConnection(App.url, App.utilisateur, App.motDePasse);
                PreparedStatement statement = connexion.prepareStatement(requete)) {

            statement.setString(1, nom);
            statement.setString(2, prenom);
            statement.setString(3, identifiant);
            statement.setString(4, fonction);

            if (modifierMdp) {
                String motDePasseHache = Hash_MotDePasse(nouveauMotDePasse);
                statement.setString(5, motDePasseHache);
                statement.setInt(6, idUtilisateur);
            } else {
                statement.setInt(5, idUtilisateur);
            }

            return statement.executeUpdate() > 0;

        } catch (SQLException erreur) {
            System.err.println(erreur.getMessage());
        }
        return false;
    }

    // 3. Fonction pour supprimer un utilisateur en fournissant son nom et prénom
    public static boolean SupprimerUtilisateur(Utilisateur data) {
        String requete = "DELETE FROM utilisateurs WHERE id_utilisateur = ?";

        try (Connection connexion = DriverManager.getConnection(App.url, App.utilisateur, App.motDePasse);
                PreparedStatement statement = connexion.prepareStatement(requete)) {

            statement.setInt(1, data.getId());

            boolean supprime = statement.executeUpdate() > 0;

            if (supprime) {
                Functions.Alerte_succes(
                        "Utilisateur " + data.getNom() + " " + data.getPrenom() + " supprimé avec succès");
            }

            return supprime;

        } catch (SQLException erreur) {
            System.err.println(erreur.getMessage());
            Functions.Alerte_erreur("Erreur lors de la suppression");
            return false;
        }
    }

    public static ObservableList<Utilisateur> ListeUtilisateurs() {
        ObservableList<Utilisateur> liste = FXCollections.observableArrayList();
        String requete = "SELECT id_utilisateur, nom, prenom, identifiant, actif FROM utilisateurs";

        try (Connection connexion = DriverManager.getConnection(App.url, App.utilisateur, App.motDePasse);
                PreparedStatement statement = connexion.prepareStatement(requete);
                ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                Utilisateur u = new StructureDonnee.Utilisateur(
                        rs.getInt("id_utilisateur"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("identifiant"),
                        rs.getInt("actif"));
                liste.add(u);
            }

        } catch (SQLException erreur) {
            System.err.println(erreur.getMessage());
        }

        return liste;
    }

    public static boolean ChangerStatutUtilisateur(int idUtilisateur) {
        String requete = "UPDATE utilisateurs SET actif = IF(actif = 1, 0, 1) WHERE id_utilisateur = ?";

        try (Connection connexion = DriverManager.getConnection(App.url, App.utilisateur, App.motDePasse);
                PreparedStatement statement = connexion.prepareStatement(requete)) {

            statement.setInt(1, idUtilisateur);

            return statement.executeUpdate() > 0;

        } catch (SQLException erreur) {
            System.err.println(erreur.getMessage());
        }
        return false;
    }
}
