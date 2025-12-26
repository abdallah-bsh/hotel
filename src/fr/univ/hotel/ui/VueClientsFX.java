package fr.univ.hotel.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

public class VueClientsFX extends BorderPane {

    public VueClientsFX() {
        setPadding(new Insets(10));
        setCenter(new Label("Gestion des clients (à implémenter)"));
    }
}