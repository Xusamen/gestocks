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
        private String unite;
        private LocalDateTime dateCreation;

        public Produit(Long idProduit, String libelleProduit, String unite, LocalDateTime dateCreation) {
            this.idProduit = idProduit;
            this.libelleProduit = libelleProduit;
            this.unite = unite;
            this.dateCreation = dateCreation;
        }

        // Getters et Setters
        public Long getIdProduit() {
            return idProduit;
        }

        public String getLibelleProduit() {
            return libelleProduit;
        }

        public String getUnite() {
            return unite;
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
        private String unite;

        public InfoProduit(Long idStock, LocalDate dateAppro, LocalDate datePeremption,
                BigDecimal prixUnitaireAchat, BigDecimal prixUnitaireVente,
                int quantiteAppro, int quantiteRestante, String unite) {
            this.idStock = idStock;
            this.dateAppro = dateAppro;
            this.datePeremption = datePeremption;
            this.prixUnitaireAchat = prixUnitaireAchat;
            this.prixUnitaireVente = prixUnitaireVente;
            this.quantiteAppro = quantiteAppro;
            this.quantiteRestante = quantiteRestante;
            this.unite = unite;
        }

        // Getters bruts
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

        public String getUnite() {
            return unite;
        }

        // Getters formatés pour les dates
        public String getDateApproFormat() {
            if (dateAppro == null)
                return "";
            return dateAppro.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }

        public String getDatePeremptionFormat() {
            if (datePeremption == null)
                return "";
            return datePeremption.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
    }

    public static class InventaireStocks {
        private Long idProduit;
        private String nomProduit;
        private int stockTotal;
        private String unite;
        private BigDecimal valeurStock;
        private LocalDate dateDernierAppro;

        public InventaireStocks(Long idProduit, String nomProduit, int stockTotal, String unite, BigDecimal valeurStock,
                LocalDate dateDernierAppro) {
            this.idProduit = idProduit;
            this.nomProduit = nomProduit;
            this.stockTotal = stockTotal;
            this.unite = unite;
            this.valeurStock = valeurStock;
            this.dateDernierAppro = dateDernierAppro;
        }

        // Getters bruts
        public Long getIdProduit() {
            return idProduit;
        }

        public String getNomProduit() {
            return nomProduit;
        }

        public int getStockTotal() {
            return stockTotal;
        }

        public String getUnite() {
            return unite;
        }

        public BigDecimal getValeurStock() {
            return valeurStock;
        }

        public LocalDate getDateDernierAppro() {
            return dateDernierAppro;
        }

        // Getter formaté pour la date du dernier appro
        public String getDateDernierApproFormat() {
            if (dateDernierAppro == null)
                return "-";
            return dateDernierAppro.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
    }

    public static class CommandeDetails {
        private long idCommande;
        private long idProduit;
        private String libelleProduit;
        private int quantite;
        private BigDecimal prixUnitaire;
        private BigDecimal coutTotal;

        // Constructeur, Getters et Setters
        public CommandeDetails(long idCommande, long idProduit, String libelleProduit, int quantite,
                BigDecimal prixUnitaire, BigDecimal coutTotal) {
            this.idCommande = idCommande;
            this.idProduit = idProduit;
            this.libelleProduit = libelleProduit;
            this.quantite = quantite;
            this.prixUnitaire = prixUnitaire;
            this.coutTotal = coutTotal;
        }

        public String getLibelleProduit() {
            return libelleProduit;
        }

        public long getIdCommande() {
            return idCommande;
        }

        public long getIdProduit() {
            return idProduit;
        }

        public int getQuantite() {
            return quantite;
        }

        public BigDecimal getPrixUnitaire() {
            return prixUnitaire;
        }

        public BigDecimal getCoutTotal() {
            return coutTotal;
        }

        @Override
        public String toString() {
            return "Produit: " + libelleProduit + " | Quantité: " + quantite + " | P.U: " + prixUnitaire + " | Total: "
                    + coutTotal;
        }
    }

}
