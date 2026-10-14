package com.cybersek.gestocks;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StructureDonnee {

    public static class Utilisateur {
        private final int id;
        private final String nom;
        private final String prenom;
        private final String identifiant;
        private String motDePasse;
        private String fonction;
        private int statut;

        public Utilisateur(int id, String nom, String prenom, String identifiant, String motDePasse, String fonction,
                int statut) {
            this.id = id;
            this.nom = nom;
            this.prenom = prenom;
            this.identifiant = identifiant;
            this.motDePasse = motDePasse;
            this.fonction = fonction;
            this.statut = statut;
        }

        public Utilisateur(int id, String nom, String prenom, String identifiant, String fonction,
                int statut) {
            this.id = id;
            this.nom = nom;
            this.prenom = prenom;
            this.identifiant = identifiant;
            this.fonction = fonction;
            this.statut = statut;
        }

        public Utilisateur(int id, String nom, String prenom, String identifiant, int statut) {
            this.id = id;
            this.nom = nom;
            this.prenom = prenom;
            this.identifiant = identifiant;
            this.statut = statut;
        }

        // Getters
        public int getId() {
            return id;
        }

        public String getNom() {
            return nom;
        }

        public String getPrenom() {
            return prenom;
        }

        public String getIdentifiant() {
            return identifiant;
        }

        public String getMotDePasse() {
            return motDePasse;
        }

        public String getFonction() {
            return fonction;
        }

        public String getStatut() {
            return (statut == 1) ? "Actif" : "Non-Actif";
        }
    }

    public static class Produit {
        private Long idProduit;
        private String libelleProduit;
        private LocalDateTime dateCreation;

        public Produit(Long idProduit, String libelleProduit, LocalDateTime dateCreation) {
            this.idProduit = idProduit;
            this.libelleProduit = libelleProduit;
            this.dateCreation = dateCreation;
        }

        // Getters et Setters
        public Long getIdProduit() {
            return idProduit;
        }

        public String getLibelleProduit() {
            return libelleProduit;
        }

        public LocalDateTime getDateCreation() {
            return dateCreation;
        }

        public String getDateCreationFormat() {
            if (dateCreation == null)
                return "";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return dateCreation.format(formatter);
        }
    }

    public static class InfoProduit {
        private Long idStock;
        private LocalDate dateAppro;
        private LocalDate datePeremption;
        private BigDecimal prixUnitaireAchat;
        private BigDecimal prixUnitaireVente;
        private int quantiteAppro;
        private int quantiteRestante;

        public InfoProduit(Long idStock, LocalDate dateAppro, LocalDate datePeremption,
                BigDecimal prixUnitaireAchat, BigDecimal prixUnitaireVente,
                int quantiteAppro, int quantiteRestante) {
            this.idStock = idStock;
            this.dateAppro = dateAppro;
            this.datePeremption = datePeremption;
            this.prixUnitaireAchat = prixUnitaireAchat;
            this.prixUnitaireVente = prixUnitaireVente;
            this.quantiteAppro = quantiteAppro;
            this.quantiteRestante = quantiteRestante;
        }

        // Getters bruts (si besoin)
        public Long getIdStock() {
            return idStock;
        }

        public LocalDate getDateAppro() {
            return dateAppro;
        }

        public LocalDate getDatePeremption() {
            return datePeremption;
        }

        public BigDecimal getPrixUnitaireAchat() {
            return prixUnitaireAchat;
        }

        public BigDecimal getPrixUnitaireVente() {
            return prixUnitaireVente;
        }

        public int getQuantiteAppro() {
            return quantiteAppro;
        }

        public int getQuantiteRestante() {
            return quantiteRestante;
        }

        // --- Getters formatés en dd/MM/yyyy ---

        public String getDateApproFormat() {
            if (dateAppro == null)
                return "";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return dateAppro.format(formatter);
        }

        public String getDatePeremptionFormat() {
            if (datePeremption == null)
                return "";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return datePeremption.format(formatter);
        }
    }
}
