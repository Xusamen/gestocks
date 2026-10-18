package com.cybersek.gestocks;

import java.util.List;

import com.cybersek.gestocks.StructureDonnee.InventaireStocks;
import com.cybersek.gestocks.ModuleStocks.Inventaire;

public class test {

    public static void main(String[] args) {
        List<InventaireStocks> liste = Inventaire.listerInventaire();
        GenerateurPDF.InventairePdf(liste);
    }

}
