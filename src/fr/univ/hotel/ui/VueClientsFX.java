
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

public class VueClientsFX extends BorderPane {

    private final ObservableList<ClientInfo> clients = FXCollections.observableArrayList();
    private final TableView<ClientInfo> tableView = new TableView<>();

    public VueClientsFX() {
        setPadding(new Insets(10));
        setTop(buildActions());
        setCenter(buildTable());
        chargerClients();
    }

    private HBox buildActions() {
        Button btnReload = new Button("Recharger");
        Button btnAdd = new Button("Ajouter");
        Button btnDelete = new Button("Supprimer");

        btnReload.setOnAction(e -> chargerClients());
        btnAdd.setOnAction(e -> ajouterClient());
        btnDelete.setOnAction(e -> supprimerClient());

        HBox actions = new HBox(10, btnReload, btnAdd, btnDelete);
        actions.setAlignment(Pos.CENTER_LEFT);
        actions.setPadding(new Insets(0, 0, 10, 0));
        return actions;
    }

    private TableView<ClientInfo> buildTable() {
        tableView.setItems(clients);
        tableView.setPlaceholder(new Label("Aucun client"));
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<ClientInfo, String> colNom = new TableColumn<>("Nom");
        colNom.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNom()));

        TableColumn<ClientInfo, String> colPrenom = new TableColumn<>("Prénom");
        colPrenom.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPrenom()));

        TableColumn<ClientInfo, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));

        tableView.getColumns().addAll(colNom, colPrenom, colEmail);
        return tableView;
    }

    private void chargerClients() {
        try {
            // Simulation DAO - données statiques
            clients.setAll(
                    new ClientInfo("Martin", "Paul", "paul.martin@example.com"),
                    new ClientInfo("Durand", "Sophie", "sophie.durand@example.com"),
                    new ClientInfo("Leblanc", "Claire", "claire.leblanc@example.com")
            );
        } catch (Exception e) {
            DialogUtils.showError("Erreur", "Impossible de charger les clients : " + e.getMessage());
        }
    }

    private void ajouterClient() {
        try {
            TextInputDialog dialogNom = new TextInputDialog();
            dialogNom.setTitle("Nouveau client");
            dialogNom.setHeaderText("Ajouter un client");
            dialogNom.setContentText("Nom :");
            Optional<String> resNom = dialogNom.showAndWait();
            if (resNom.isEmpty() || resNom.get().isBlank()) {
                DialogUtils.showError("Validation", "Le nom est obligatoire.");
                return;
            }

            TextInputDialog dialogPrenom = new TextInputDialog();
            dialogPrenom.setTitle("Nouveau client");
            dialogPrenom.setHeaderText(null);
            dialogPrenom.setContentText("Prénom :");
            Optional<String> resPrenom = dialogPrenom.showAndWait();
            if (resPrenom.isEmpty() || resPrenom.get().isBlank()) {
                DialogUtils.showError("Validation", "Le prénom est obligatoire.");
                return;
            }

            TextInputDialog dialogEmail = new TextInputDialog();
            dialogEmail.setTitle("Nouveau client");
            dialogEmail.setHeaderText(null);
            dialogEmail.setContentText("Email :");
            Optional<String> resEmail = dialogEmail.showAndWait();
            if (resEmail.isEmpty() || resEmail.get().isBlank()) {
                DialogUtils.showError("Validation", "L'email est obligatoire.");
                return;
            }

            clients.add(new ClientInfo(
                    resNom.get().trim(),
                    resPrenom.get().trim(),
                    resEmail.get().trim()
            ));
        } catch (Exception e) {
            DialogUtils.showError("Erreur", "Erreur lors de l'ajout du client : " + e.getMessage());
        }
    }

    private void supprimerClient() {
        ClientInfo selection = tableView.getSelectionModel().getSelectedItem();
        if (selection == null) {
            DialogUtils.showError("Suppression", "Veuillez sélectionner un client à supprimer.");
            return;
        }

        boolean confirme = DialogUtils.showConfirmation(
                "Confirmation",
                "Supprimer le client " + selection.getNom() + " " + selection.getPrenom() + " ?"
        );
        if (!confirme) {
            return;
        }

        try {
            clients.remove(selection);
        } catch (Exception e) {
            DialogUtils.showError("Erreur", "Erreur lors de la suppression : " + e.getMessage());
        }
    }

    private static class ClientInfo {
        private final String nom;
        private final String prenom;
        private final String email;

        ClientInfo(String nom, String prenom, String email) {
            this.nom = nom;
            this.prenom = prenom;
            this.email = email;
        }

        String getNom() {
            return nom;
        }

        String getPrenom() {
            return prenom;
        }

        String getEmail() {
            return email;
        }
    }
}