package com.cybersek.gestocks;

import java.math.BigDecimal;
import java.util.List;

public class TicketCaisse {

    // --- Classe interne pour une ligne de produit ---
    public static class LigneTicket {
        private String libelleProduit;
        private BigDecimal prixUnitaire;
        private int quantite;
        private BigDecimal prixTotalProduit;

        public LigneTicket(String libelleProduit, BigDecimal prixUnitaire, int quantite, BigDecimal prixTotalProduit) {
            this.libelleProduit = libelleProduit;
            this.prixUnitaire = prixUnitaire;
            this.quantite = quantite;
            this.prixTotalProduit = prixTotalProduit;
        }

        // Getters
        public String getLibelleProduit() { return libelleProduit; }
        public BigDecimal getPrixUnitaire() { return prixUnitaire; }
        public int getQuantite() { return quantite; }
        public BigDecimal getPrixTotalProduit() { return prixTotalProduit; }
    }

    // --- Attributs globaux du Ticket de Caisse ---
    private long idVente;
    private String numeroRecu;
    private String dateVente;
    private String typeEncaissement;
    private List<LigneTicket> lignesProduits;
    private BigDecimal montantTotal;
    private BigDecimal montantVerser;
    private BigDecimal monnaieRendu;
    private BigDecimal reductionAppliquer;

    public TicketCaisse(long idVente, String numeroRecu, String dateVente, String typeEncaissement,
                           List<LigneTicket> lignesProduits, BigDecimal montantTotal, 
                           BigDecimal montantVerser, BigDecimal monnaieRendu, BigDecimal reductionAppliquer) {
        this.idVente = idVente;
        this.numeroRecu = numeroRecu;
        this.dateVente = dateVente;
        this.typeEncaissement = typeEncaissement;
        this.lignesProduits = lignesProduits;
        this.montantTotal = montantTotal;
        this.montantVerser = montantVerser;
        this.monnaieRendu = monnaieRendu;
        this.reductionAppliquer = reductionAppliquer;
    }

    // Getters globaux
    public long getIdVente() { return idVente; }
    public String getNumeroRecu() { return numeroRecu; }
    public String getDateVente() { return dateVente; }
    public String getTypeEncaissement() { return typeEncaissement; }
    public List<LigneTicket> getLignesProduits() { return lignesProduits; }
    public BigDecimal getMontantTotal() { return montantTotal; }
    public BigDecimal getMontantVerser() { return montantVerser; }
    public BigDecimal getMonnaieRendu() { return monnaieRendu; }
    public BigDecimal getReductionAppliquer() { return reductionAppliquer; }
}