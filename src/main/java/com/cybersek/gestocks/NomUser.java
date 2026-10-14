package com.cybersek.gestocks;

public class NomUser {

    private static String nom_prenom = "";
    private static String user_identifiant = "";
    private static String nom_user;
    private static String prenom_user;
    private static int id_user;
    private static String user_fonction;

    public static String getNom_Prenom() {
        return nom_prenom;
    }

    public static String getUserFonction() {
        return user_fonction;
    }

    public static String getUserIdentifiant() {
        return user_identifiant;
    }

    public static void setUser(int idUtilisateur, String value1, String value2) {
        nom_prenom = value1 + "  " + value2;
        nom_user = value1;
        prenom_user = value2;
        id_user = idUtilisateur;
    }

    public static void setUserInfo(int idUtilisateur, String value1, String value2, String value3) {
        nom_prenom = value1 + "  " + value2;
        nom_user = value1;
        prenom_user = value2;
        id_user = idUtilisateur;
        user_fonction = value3;
    }

    public static void setUserIdentifiant(String value) {
        user_identifiant = value;
    }

    public static void setUserFonction(String value) {
        user_fonction = value;
    }

    public static String getNomUser() {
        return nom_user;
    }

    public static String getPrenomUser() {
        return prenom_user;
    }

    public static int getIdUtilisateur() {
        return id_user;
    }
}
