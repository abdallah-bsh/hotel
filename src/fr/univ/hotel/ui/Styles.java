package fr.univ.hotel.ui;

import javafx.scene.Scene;

import java.net.URL;

public final class Styles {

    private Styles() {
        // Classe utilitaire
    }

    public static void appliquerCssGlobal(Scene scene) {
        if (scene == null) {
            return;
        }

        URL cssUrl = Styles.class.getResource("style.css");
        if (cssUrl != null) {
            String stylesheet = cssUrl.toExternalForm();
            if (!scene.getStylesheets().contains(stylesheet)) {
                scene.getStylesheets().add(stylesheet);
            }
        }
    }
}