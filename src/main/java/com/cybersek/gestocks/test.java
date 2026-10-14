package com.cybersek.gestocks;
import com.cybersek.gestocks.ModuleParametre.GestionUtilisateur;

public class test {

    public static void main(String[] args) {

        // 1. (Optionnel) Assurez-vous que vos variables App sont configurées si ce
        // n'est pas déjà fait dans App.java
        // App.url = "jdbc:mysql://localhost:3306/votre_bdd";
        // App.utilisateur = "root";
        // App.motDePasse = "votre_mot_de_passe";

        // 2. Informations du nouvel utilisateur à créer
        String nom = "OUEDRAOGO";
        String prenom = "Yann";
        String identifiant = "dev";
        String motDePasseClair = "souris"; // Le hachage BCrypt se fera automatiquement
        String fonction = "Administrateur";

        System.out.println("Tentative de création de l'utilisateur...");

        // 3. Appel de la méthode de création
        boolean resultat = GestionUtilisateur.CreerUtilisateur(
                nom,
                prenom,
                identifiant,
                motDePasseClair,
                fonction);

        // 4. Affichage du résultat
        if (resultat) {
            System.out
                    .println("Succès : L'utilisateur '" + identifiant + "' a été créé et son mot de passe haché !");
        } else {
            System.err.println(
                    "Échec : Impossible de créer l'utilisateur (vérifiez si l'identifiant existe déjà ou vos identifiants BDD).");
        }
    }

}
