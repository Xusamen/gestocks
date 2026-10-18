package com.cybersek.gestocks;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Optional;


public class Functions {
    // notification en cas d'erreur
    public static void Alerte_erreur(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // notification en cas de reussite
    public static void Alerte_succes(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static DropShadow ArrierePlan() {
        DropShadow ombre = new DropShadow();
        ombre.setHeight(45.07);
        ombre.setWidth(53.25);
        ombre.setRadius(24.08);
        ombre.setColor(Color.BLACK);
        return ombre;
    }

    public static void printPdfLePlusRecent(String cheminRepertoire) {
        try {
            Path dossier = Paths.get(cheminRepertoire);

            // Vérifier si le dossier existe
            if (!Files.exists(dossier) || !Files.isDirectory(dossier)) {
                System.err.println("Le répertoire spécifié n'existe pas : " + cheminRepertoire);
                return;
            }

            // Trouver le fichier .pdf le plus récent dans le dossier
            Optional<Path> fichierRecent = Files.list(dossier)
                    .filter(p -> p.toString().toLowerCase().endsWith(".pdf"))
                    .max(Comparator.comparingLong(p -> p.toFile().lastModified()));

            if (fichierRecent.isPresent()) {
                File fichier = fichierRecent.get().toFile();
                ouvrirPdfDansNavigateur(fichier.getAbsolutePath());
                System.out.println("Ouverture du fichier le plus récent : " + fichier.getName());
            } else {
                System.out.println("Aucun fichier PDF trouvé dans le répertoire.");
            }

        } catch (IOException e) {
            System.err.println("Erreur lors de la recherche du fichier PDF : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void ouvrirPdfDansNavigateur(String cheminFichier) {
        try {
            File fichier = new File(cheminFichier);

            if (!fichier.exists()) {
                System.err.println("Le fichier PDF introuvable : " + cheminFichier);
                return;
            }

            // Vérifier si Desktop est supporté par le système
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();

                // Ouvrir le fichier avec l'application associée par défaut (le navigateur web
                // ou lecteur PDF par défaut)
                desktop.open(fichier);
            } else {
                System.err.println("La fonctionnalité Desktop n'est pas supportée sur ce système.");
            }

        } catch (IOException e) {
            System.err.println("Impossible d'ouvrir le fichier PDF : " + e.getMessage());
            e.printStackTrace();
        }
    }

}
