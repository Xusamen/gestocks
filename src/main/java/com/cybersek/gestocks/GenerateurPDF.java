package com.cybersek.gestocks;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import com.cybersek.gestocks.StructureDonnee.CommandeDetails;
import com.cybersek.gestocks.StructureDonnee.InventaireStocks;

public class GenerateurPDF {

    private static final float MARGE = 50;
    private static final float ROW_HEIGHT = 20;
    private static final float FONT_SIZE = 11;
    private static final float PAGE_WIDTH = PDRectangle.A4.getWidth();
    private static final float PAGE_HEIGHT = PDRectangle.A4.getHeight();
    private static final float Y_MIN = MARGE + 100;
    public static final float Y_MIN_NEW = 60;
    public static final float PAGE_LARGEUR = 595;
    public static final float PAGE_HAUTEUR = 420;

    public static DateTimeFormatter formatterFichier = DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm");
    public static DateTimeFormatter formatterAffichage = DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm");

    public static LocalDateTime maintenant = LocalDateTime.now();
    public static String dateHeureAffichage = maintenant.format(formatterAffichage);

    public static void InventairePdf(List<InventaireStocks> liste) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            PDType1Font fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            float fontSize = 9f;
            float y = PAGE_HEIGHT - 50;

            // Ouvrir le premier flux de contenu
            PDPageContentStream content = new PDPageContentStream(document, page);
            try {
                // Titre centré
                content.beginText();
                content.setFont(fontBold, 12);
                float titreWidth = fontBold.getStringWidth("INVENTAIRE DU STOCK") / 1000 * 12;
                float titreX = (PDRectangle.A4.getWidth() - titreWidth) / 2;
                content.newLineAtOffset(titreX, y);
                content.showText("INVENTAIRE DU STOCK");
                content.endText();

                float underlineY = y - 2;
                content.moveTo(titreX, underlineY);
                content.lineTo(titreX + titreWidth, underlineY);
                content.stroke();
                y -= 30;

                content.setFont(fontBold, 10);
                float subtitleX = MARGE;
                content.beginText();
                content.newLineAtOffset(subtitleX, y);
                content.showText("Géneré le " + dateHeureAffichage);
                content.endText();

                content.beginText();
                content.newLineAtOffset(subtitleX + 4 * 100, y);
                content.showText("PAR GESTOCKS");
                content.endText();
                y -= 45;

                // --- Colonnes
                String[] enTetes = { "N°","Nom Produit", "Unité", "Quantité Dispo", "Quantité Compté" };
                float[] colWidths = { 30, 200, 85, 85, 90 };
                y = drawTableHeader(content, enTetes, colWidths, y, fontBold);

                int num = 1;
                for (InventaireStocks s : liste) {
                    String[] data = { String.valueOf(num++), s.getNomProduit(),
                            s.getUnite(), String.valueOf(s.getStockTotal()), " " };

                    float ligneHauteur = calculerHauteur(data, colWidths, font, fontSize);

                    // Saut de page si besoin
                    if (y - ligneHauteur < Y_MIN_NEW) {
                        content.close(); // Fermeture propre du flux de la page actuelle avant de changer
                        page = new PDPage(PDRectangle.A4);
                        document.addPage(page);
                        content = new PDPageContentStream(document, page); // Nouveau flux pour la nouvelle page
                        y = PAGE_HEIGHT - MARGE;

                        y = drawTableHeader(content, enTetes, colWidths, y, fontBold);
                    }

                    y = drawTableRow(content, data, colWidths, y, font, fontSize);
                }
            } finally {
                // GARANTIE : On ferme impérativement le dernier flux actif, qu'il y ait une
                // erreur ou non
                if (content != null) {
                    content.close();
                }
            }

            // Sauvegarde du document une fois que tous les flux sont fermés
            String cheminRacine = System.getProperty("user.home") + "\\Documents\\GESTOCKS\\Inventaire\\";
            String nomduFichier = "stock_inventaire_" + maintenant.format(formatterFichier) + ".pdf";
            String cheminFichier = cheminRacine + nomduFichier;

            // Création du répertoire si besoin
            java.io.File dossier = new java.io.File(cheminRacine);
            if (!dossier.exists()) {
                dossier.mkdirs();
            }

            document.save(cheminFichier);
            System.out.println("PDF généré avec succès : " + cheminFichier);
            Functions.printPdfLePlusRecent(cheminRacine);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static float drawTableHeader(PDPageContentStream content, String[] entetes, float[] widths, float y,
            PDFont font) throws IOException {
        float x = MARGE;
        float rowHeight = 20;

        // 1. Définir la couleur de fond (Vert RGB : 7, 157, 64) via java.awt.Color
        content.setNonStrokingColor(new java.awt.Color(7, 157, 64));

        for (int i = 0; i < entetes.length; i++) {
            content.addRect(x, y - rowHeight, widths[i], rowHeight);

            // 2. Remplir le fond ET tracer la bordure en même temps
            content.fillAndStroke();

            // 3. Mettre la couleur en BLANC (ou NOIR) pour le texte de l'en-tête (le vert
            // foncé est un fond sombre, le blanc ressortira mieux)
            content.setNonStrokingColor(java.awt.Color.WHITE);

            // Écrire le texte
            drawText(content, entetes[i], font, 9, x + 3, y - 14);

            // 4. Remettre la couleur de fond verte pour la cellule suivante
            content.setNonStrokingColor(new java.awt.Color(7, 157, 64));

            x += widths[i];
        }

        // Remettre proprement la couleur en noir à la fin pour la suite du document
        content.setNonStrokingColor(java.awt.Color.BLACK);
        return y - rowHeight;
    }

    public static float drawTableRow(PDPageContentStream content, String[] data, float[] widths, float y, PDFont font,
            float fontSize) throws IOException {
        float height = calculerHauteur(data, widths, font, fontSize);
        float x = MARGE;

        for (int i = 0; i < data.length; i++) {
            content.addRect(x, y - height, widths[i], height);
            content.stroke();
            drawMultilineCellule(content, data[i], font, fontSize, x, y - height, widths[i], height);
            x += widths[i];
        }
        return y - height;
    }

    public static void drawText(PDPageContentStream content, String text, PDFont font, float size, float x, float y)
            throws IOException {
        content.beginText();
        content.setFont(font, size);
        content.newLineAtOffset(x, y);
        content.showText(text);
        content.endText();
    }

    public static void drawMultilineCellule(PDPageContentStream content, String text, PDFont font, float fontSize,
            float x, float y, float width, float height) throws IOException {
        List<String> lines = new ArrayList<>();
        StringBuilder line = new StringBuilder();

        for (String word : text.split(" ")) {
            String tentative = line + (line.length() == 0 ? "" : " ") + word;
            float w = font.getStringWidth(tentative) / 1000 * fontSize;
            if (w > width - 6) {
                lines.add(line.toString());
                line = new StringBuilder(word);
            } else {
                line = new StringBuilder(tentative);
            }
        }
        if (line.length() > 0)
            lines.add(line.toString());

        float textY = y + height - fontSize - 4;
        for (String l : lines) {
            content.beginText();
            content.setFont(font, fontSize);
            content.newLineAtOffset(x + 3, textY);
            content.showText(l);
            content.endText();
            textY -= fontSize;
        }
    }

    public static float calculerHauteur(String[] data, float[] widths, PDFont font, float fontSize) throws IOException {
        float max = 0;
        for (int i = 0; i < data.length; i++) {
            int lignes = 1;
            StringBuilder currentLine = new StringBuilder();
            for (String word : data[i].split(" ")) {
                String tentative = currentLine + (currentLine.length() == 0 ? "" : " ") + word;
                float w = font.getStringWidth(tentative) / 1000 * fontSize;
                if (w > widths[i] - 6) {
                    lignes++;
                    currentLine = new StringBuilder(word);
                } else {
                    currentLine = new StringBuilder(tentative);
                }
            }
            float h = lignes * fontSize + 6;
            if (h > max)
                max = h;
        }
        return max;
    }



}
