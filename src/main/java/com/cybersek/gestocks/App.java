package com.cybersek.gestocks;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class App extends Application {

    public static final String utilisateur = "cybersek";
    public static final String motDePasse = "souris";
    public static final String url = "jdbc:mysql://localhost:3306/gestocks";

    //Client Distant info pour connexion DB
    /*public static final String utilisateur = "cybersek";
    public static final String motDePasse = "souris";
    public static final String url = "jdbc:mysql://192.168.11.10:3306/gestocks";*/

    private static Scene scene;

    @Override
    public void start(Stage PremiereFenetre) {
        try {
            scene = new Scene(loadFXML("ConteneurPrincipal.fxml"));
            Image icone = new Image(getClass().getResourceAsStream("/com/cybersek/gestocks/icone/iconeFP.png"));
            PremiereFenetre.getIcons().add(icone);
            PremiereFenetre.setTitle("GESTOCKS");
            PremiereFenetre.setScene(scene);
            PremiereFenetre.show();
        } catch (IOException erreur) {
            System.err.println(erreur.getMessage());
        }

    }
    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }
    private static Parent loadFXML(String nomTemplate) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(nomTemplate));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

