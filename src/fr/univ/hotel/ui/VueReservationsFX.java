package fr.univ.hotel.ui;

import fr.univ.hotel.util.DialogUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.util.Optional;

public class VueReservationsFX extends BorderPane {

    private final ObservableList<ReservationInfo> reservations = FXCollections.observableArrayList();
    private final TableView<ReservationInfo> tableView = new TableView<>();

    public VueReservationsFX() {
        setPadding(new Insets(10));
        setTop(buildActions());
        setCenter(buildTable());
        chargerReservations();
    }

    private HBox buildActions() {
        Button btnReload = new Button("Recharger");
        Button btnAdd = new Button("Ajouter");
        Button btnDelete = new Button("Supprimer");

        btnReload.setOnAction(e -> chargerReservations());
        btnAdd.setOnAction(e -> ajouterReservation());
        btnDelete.setOnAction(e -> supprimerReservation());

        HBox actions = new HBox(10, btnReload, btnAdd, btnDelete);
        actions.setAlignment(Pos.CENTER_LEFT);
        actions.setPadding(new Insets(0, 0, 10, 0));
        return actions;
    }

    private TableView<ReservationInfo> buildTable() {
        tableView.setItems(reservations);
        tableView.setPlaceholder(new Label("Aucune réservation"));
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<ReservationInfo, String> colClient = new TableColumn<>("Client");
        colClient.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getClient()));

        TableColumn<ReservationInfo, String> colChambre = new TableColumn<>("Chambre");
        colChambre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getChambre()));

        TableColumn<ReservationInfo, String> colDates = new TableColumn<>("Dates");
        colDates.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDates()));

        tableView.getColumns().addAll(colClient, colChambre, colDates);
        return tableView;
    }

    private void chargerReservations() {
        try {
            reservations.setAll(
                    new ReservationInfo("Paul Martin", "101 (SIMPLE)", "12/06 -> 15/06"),
                    new ReservationInfo("Sophie Durand", "203 (DOUBLE)", "20/06 -> 23/06")
            );
        } catch (Exception e) {
            DialogUtils.showError("Erreur", "Impossible de charger les réservations : " + e.getMessage());
        }
    }

    private void ajouterReservation() {
        try {
            TextInputDialog dialogClient = new TextInputDialog();
            dialogClient.setTitle("Nouvelle réservation");
            dialogClient.setHeaderText("Ajouter une réservation");
            dialogClient.setContentText("Client :");
            Optional<String> resClient = dialogClient.showAndWait();
            if (resClient.isEmpty() || resClient.get().isBlank()) {
                DialogUtils.showError("Validation", "Le client est obligatoire.");
                return;
            }

            TextInputDialog dialogChambre = new TextInputDialog();
            dialogChambre.setTitle("Nouvelle réservation");
            dialogChambre.setHeaderText(null);
            dialogChambre.setContentText("Chambre :");
            Optional<String> resChambre = dialogChambre.showAndWait();
            if (resChambre.isEmpty() || resChambre.get().isBlank()) {
                DialogUtils.showError("Validation", "La chambre est obligatoire.");
                return;
            }

            TextInputDialog dialogDates = new TextInputDialog();
            dialogDates.setTitle("Nouvelle réservation");
            dialogDates.setHeaderText(null);
            dialogDates.setContentText("Dates (ex : 01/07 -> 05/07) :");
            Optional<String> resDates = dialogDates.showAndWait();
            if (resDates.isEmpty() || resDates.get().isBlank()) {
                DialogUtils.showError("Validation", "Les dates sont obligatoires.");
                return;
            }

            reservations.add(new ReservationInfo(
                    resClient.get().trim(),
                    resChambre.get().trim(),
                    resDates.get().trim()
            ));
        } catch (Exception e) {
            DialogUtils.showError("Erreur", "Erreur lors de l'ajout de la réservation : " + e.getMessage());
        }
    }

    private void supprimerReservation() {
        ReservationInfo selection = tableView.getSelectionModel().getSelectedItem();
        if (selection == null) {
            DialogUtils.showError("Suppression", "Veuillez sélectionner une réservation à supprimer.");
            return;
        }

        boolean confirme = DialogUtils.showConfirmation(
                "Confirmation",
                "Supprimer la réservation de " + selection.getClient() + " ?"
        );
        if (!confirme) {
            return;
        }

        try {
            reservations.remove(selection);
        } catch (Exception e) {
            DialogUtils.showError("Erreur", "Erreur lors de la suppression : " + e.getMessage());
        }
    }

    private static class ReservationInfo {
        private final String client;
        private final String chambre;
        private final String dates;

        ReservationInfo(String client, String chambre, String dates) {
            this.client = client;
            this.chambre = chambre;
            this.dates = dates;
        }

        String getClient() {
            return client;
        }

        String getChambre() {
            return chambre;
        }

        String getDates() {
            return dates;
        }
    }
}