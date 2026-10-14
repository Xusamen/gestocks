package com.cybersek.gestocks;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

/**
 * Functions
 */
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

    public static  DropShadow ArrierePlan() {
        DropShadow ombre = new DropShadow();
        ombre.setHeight(45.07);
        ombre.setWidth(53.25);
        ombre.setRadius(24.08);
        ombre.setColor(Color.BLACK);
        return ombre;
    }

}
