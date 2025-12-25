package fr.univ.hotel.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

public class VueReservationsFX extends BorderPane {

    public VueReservationsFX() {
        setPadding(new Insets(10));
        setCenter(new Label("Gestion des réservations (à implémenter)"));
    }
}
