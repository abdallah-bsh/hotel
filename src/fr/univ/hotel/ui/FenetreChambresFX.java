package fr.univ.hotel.ui;

import fr.univ.hotel.dao.ChambreDAO;//je hais nizar
import fr.univ.hotel.dao.jdbc.ChambreDAOJdbc;
import fr.univ.hotel.metier.Chambre;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;
public class FenetreChambresFX extends Application {

    private final ChambreDAO chambreDAO = new ChambreDAOJdbc();
    private TableView<Chambre> table;
    private ObservableList<Chambre> data;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Gestion des chambres (JavaFX)");
        BorderPane root = creerContenu();

        Scene scene = new Scene(root, 800, 500);
        Styles.appliquerCssGlobal(scene);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public BorderPane creerContenu() {
        // TABLEVIEW
        table = new TableView<>();
        data = FXCollections.observableArrayList();
        table.setItems(data);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Chambre, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Chambre, Integer> colNumero = new TableColumn<>("Numéro");
        colNumero.setCellValueFactory(new PropertyValueFactory<>("numero"));

        TableColumn<Chambre, String> colType = new TableColumn<>("Type");
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<Chambre, Double> colPrix = new TableColumn<>("Prix/nuit");
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prixParNuit"));

        TableColumn<Chambre, String> colStatut = new TableColumn<>("Statut");
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));

        table.getColumns().addAll(colId, colNumero, colType, colPrix, colStatut);

        // BOUTONS
        Button btnReload = new Button("Recharger");
        Button btnAjouter = new Button("Ajouter");
        Button btnModifier = new Button("Modifier");
        Button btnStatut = new Button("Changer statut");
        Button btnSupprimer = new Button("Supprimer");

        btnReload.setOnAction(e -> chargerChambres());
        btnAjouter.setOnAction(e -> ajouterChambre());
        btnModifier.setOnAction(e -> modifierChambre());
        btnStatut.setOnAction(e -> changerStatut());
        btnSupprimer.setOnAction(e -> supprimerChambre());

        HBox topBar = new HBox(
                10,
                btnReload,
                btnAjouter,
                btnModifier,
                btnStatut,
                btnSupprimer
        );
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(10));

        // LAYOUT PRINCIPAL
        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(table);

        // Chargement initial
        chargerChambres();

        return root;
    }

    private void chargerChambres() {
        try {
            List<Chambre> chambres = chambreDAO.findAll();
            data.setAll(chambres);
        } catch (Exception e) {
            showError("Impossible de charger les chambres : " + e.getMessage());
        }
    }

    private void ajouterChambre() {
        try {
            // Numéro
            TextInputDialog dialogNumero = new TextInputDialog();
            dialogNumero.setTitle("Nouvelle chambre");
            dialogNumero.setHeaderText("Ajouter une chambre");
            dialogNumero.setContentText("Numéro de la chambre :");
            Optional<String> resNumero = dialogNumero.showAndWait();
            if (resNumero.isEmpty()) return;
            int numero = Integer.parseInt(resNumero.get().trim());

            // Type
            TextInputDialog dialogType = new TextInputDialog("SIMPLE");
            dialogType.setTitle("Nouvelle chambre");
            dialogType.setHeaderText(null);
            dialogType.setContentText("Type (SIMPLE/DOUBLE/SUITE) :");
            Optional<String> resType = dialogType.showAndWait();
            if (resType.isEmpty() || resType.get().isBlank()) return;
            String type = resType.get().trim().toUpperCase();

            // Prix
            TextInputDialog dialogPrix = new TextInputDialog("50");
            dialogPrix.setTitle("Nouvelle chambre");
            dialogPrix.setHeaderText(null);
            dialogPrix.setContentText("Prix par nuit :");
            Optional<String> resPrix = dialogPrix.showAndWait();
            if (resPrix.isEmpty()) return;
            double prix = Double.parseDouble(resPrix.get().trim());

            // Statut (par défaut)
            String statut = "LIBRE";

            // Création + insertion
            Chambre c = new Chambre(numero, numero, statut, prix, statut);
            c.setNumero(numero);
            c.setType(type);
            c.setPrixParNuit(prix);
            c.setStatut(statut);

            boolean ok = chambreDAO.insert(c);
            if (ok) {
                chargerChambres();
            } else {
                showError("Erreur lors de l'insertion en base.");
            }

        } catch (NumberFormatException ex) {
            showError("Valeur numérique invalide.");
        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Erreur inattendue : " + ex.getMessage());
        }
    }

    private void supprimerChambre() {
        Chambre selection = table.getSelectionModel().getSelectedItem();
        if (selection == null) {
            showError("Veuillez sélectionner une chambre à supprimer.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText(null);
        confirm.setContentText("Supprimer la chambre " + selection.getNumero() + " ?");
        Optional<ButtonType> res = confirm.showAndWait();

        if (res.isPresent() && res.get() == ButtonType.OK) {
            try {
                boolean ok = chambreDAO.delete(selection);
                if (ok) {
                    chargerChambres();
                } else {
                    showError("Erreur lors de la suppression.");
                }
            } catch (Exception e) {
                showError("Erreur lors de la suppression : " + e.getMessage());
            }
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void changerStatut() {

        Chambre selection = table.getSelectionModel().getSelectedItem();

        if (selection == null) {
            showError("Veuillez sélectionner une chambre.");
            return;
        }

        // ComboBox avec les statuts possibles
        ComboBox<String> comboStatut = new ComboBox<>();
        comboStatut.getItems().addAll("LIBRE", "OCCUPEE", "RESERVEE");
        comboStatut.setValue(selection.getStatut());

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Changer le statut");
        dialog.setHeaderText("Chambre " + selection.getNumero());

        ButtonType btnOk = new ButtonType("Valider", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnOk, ButtonType.CANCEL);

        dialog.getDialogPane().setContent(comboStatut);

        dialog.showAndWait().ifPresent(result -> {
            if (result == btnOk) {
                String nouveauStatut = comboStatut.getValue();

                selection.setStatut(nouveauStatut);

                try {
                    boolean ok = chambreDAO.update(selection);
                    if (ok) {
                        chargerChambres();
                    } else {
                        showError("Erreur lors de la mise à jour du statut.");
                    }
                } catch (Exception e) {
                    showError("Erreur lors de la mise à jour du statut : " + e.getMessage());
                }
            }
        });
    }
    private void modifierChambre() {

        Chambre selection = table.getSelectionModel().getSelectedItem();

        if (selection == null) {
            showError("Veuillez sélectionner une chambre à modifier.");
            return;
        }

        try {
            // Numéro
            TextInputDialog dNum = new TextInputDialog(
                    String.valueOf(selection.getNumero())
            );
            dNum.setTitle("Modifier une chambre");
            dNum.setHeaderText("Chambre " + selection.getNumero());
            dNum.setContentText("Numéro :");
            Optional<String> resNum = dNum.showAndWait();
            if (resNum.isEmpty()) return;
            int nouveauNumero = Integer.parseInt(resNum.get().trim());


            TextInputDialog dType = new TextInputDialog(selection.getType());
            dType.setTitle("Modifier une chambre");
            dType.setHeaderText(null);
            dType.setContentText("Type (SIMPLE / DOUBLE / SUITE) :");
            Optional<String> resType = dType.showAndWait();
            if (resType.isEmpty()) return;
            String nouveauType = resType.get().trim().toUpperCase();

            // Prix
            TextInputDialog dPrix = new TextInputDialog(
                    String.valueOf(selection.getPrixParNuit())
            );
            dPrix.setTitle("Modifier une chambre");
            dPrix.setHeaderText(null);
            dPrix.setContentText("Prix par nuit :");
            Optional<String> resPrix = dPrix.showAndWait();
            if (resPrix.isEmpty()) return;
            double nouveauPrix = Double.parseDouble(resPrix.get().trim());

            // Mise à jour de l'objet
            selection.setNumero(nouveauNumero);
            selection.setType(nouveauType);
            selection.setPrixParNuit(nouveauPrix);

            // Mise à jour BD
            try {
                boolean ok = chambreDAO.update(selection);

                if (ok) {
                    chargerChambres();
                } else {
                    showError("Erreur lors de la mise à jour.");
                }
            } catch (Exception ex) {
                showError("Erreur lors de la mise à jour : " + ex.getMessage());
            }

        } catch (NumberFormatException e) {
            showError("Valeur numérique invalide.");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur inattendue : " + e.getMessage());
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}