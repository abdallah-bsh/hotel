package fr.univ.hotel.ui;

import fr.univ.hotel.dao.ClientDAO;
import fr.univ.hotel.dao.jdbc.ClientDAOJdbc;
import fr.univ.hotel.metier.Client;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class FenetreClientsFX extends BorderPane {

    private final ClientDAO clientDAO = new ClientDAOJdbc();
    private final TableView<Client> table = new TableView<>();
    private final ObservableList<Client> data = FXCollections.observableArrayList();

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9 +\\-]{6,}$");

    public FenetreClientsFX() {
        setPadding(new Insets(10));
        configureTable();
        setCenter(table);
        setTop(creerBarreActions());
        chargerClients();
    }

    private void configureTable() {
        table.setItems(data);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Client, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Client, String> colNom = new TableColumn<>("Nom");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));

        TableColumn<Client, String> colPrenom = new TableColumn<>("Prénom");
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));

        TableColumn<Client, String> colTelephone = new TableColumn<>("Téléphone");
        colTelephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));

        TableColumn<Client, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        table.getColumns().addAll(colId, colNom, colPrenom, colTelephone, colEmail);
    }

    private HBox creerBarreActions() {
        Button btnAjouter = new Button("Ajouter");
        Button btnModifier = new Button("Modifier");
        Button btnSupprimer = new Button("Supprimer");
        Button btnRafraichir = new Button("Rafraîchir");

        btnAjouter.setOnAction(e -> ajouterClient());
        btnModifier.setOnAction(e -> modifierClient());
        btnSupprimer.setOnAction(e -> supprimerClient());
        btnRafraichir.setOnAction(e -> chargerClients());

        HBox barre = new HBox(10, btnAjouter, btnModifier, btnSupprimer, btnRafraichir);
        barre.setAlignment(Pos.CENTER_LEFT);
        barre.setPadding(new Insets(0, 0, 10, 0));
        return barre;
    }

    private void chargerClients() {
        try {
            List<Client> clients = clientDAO.findAll();
            data.setAll(clients);
        } catch (Exception e) {
            showError("Impossible de charger les clients : " + e.getMessage());
        }
    }

    private void ajouterClient() {
        Optional<Client> res = ouvrirDialogClient(null);
        res.ifPresent(client -> {
            boolean ok = clientDAO.insert(client);
            if (ok) {
                chargerClients();
            } else {
                showError("Impossible d'ajouter le client.");
            }
        });
    }

    private void modifierClient() {
        Client selection = table.getSelectionModel().getSelectedItem();
        if (selection == null) {
            showError("Veuillez sélectionner un client à modifier.");
            return;
        }

        Optional<Client> res = ouvrirDialogClient(selection);
        res.ifPresent(clientModifie -> {
            boolean ok = clientDAO.update(clientModifie);
            if (ok) {
                chargerClients();
            } else {
                showError("Impossible de mettre à jour le client.");
            }
        });
    }

    private void supprimerClient() {
        Client selection = table.getSelectionModel().getSelectedItem();
        if (selection == null) {
            showError("Veuillez sélectionner un client à supprimer.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer le client");
        confirm.setContentText("Supprimer " + selection.getPrenom() + " " + selection.getNom() + " ?");

        Optional<ButtonType> res = confirm.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.OK) {
            boolean ok = clientDAO.delete(selection);
            if (ok) {
                chargerClients();
            } else {
                showError("Impossible de supprimer le client.");
            }
        }
    }

    private Optional<Client> ouvrirDialogClient(Client existant) {
        Dialog<Client> dialog = new Dialog<>();
        dialog.setTitle(existant == null ? "Nouveau client" : "Modifier un client");
        dialog.setHeaderText(null);

        ButtonType btnValider = new ButtonType("Valider", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnValider, ButtonType.CANCEL);

        GridPane grille = new GridPane();
        grille.setHgap(10);
        grille.setVgap(10);
        grille.setPadding(new Insets(10, 0, 0, 0));

        TextField champNom = new TextField(existant != null ? existant.getNom() : "");
        TextField champPrenom = new TextField(existant != null ? existant.getPrenom() : "");
        TextField champTelephone = new TextField(existant != null ? existant.getTelephone() : "");
        TextField champEmail = new TextField(existant != null ? existant.getEmail() : "");

        grille.add(new Label("Nom *"), 0, 0);
        grille.add(champNom, 1, 0);

        grille.add(new Label("Prénom *"), 0, 1);
        grille.add(champPrenom, 1, 1);

        grille.add(new Label("Téléphone *"), 0, 2);
        grille.add(champTelephone, 1, 2);

        grille.add(new Label("Email *"), 0, 3);
        grille.add(champEmail, 1, 3);

        dialog.getDialogPane().setContent(grille);

        Button boutonValider = (Button) dialog.getDialogPane().lookupButton(btnValider);
        boutonValider.addEventFilter(javafx.event.ActionEvent.ACTION, evt -> {
            String erreur = validerSaisie(
                    champNom.getText(),
                    champPrenom.getText(),
                    champTelephone.getText(),
                    champEmail.getText()
            );
            if (erreur != null) {
                evt.consume();
                showError(erreur);
            }
        });

        dialog.setResultConverter(buttonType -> {
            if (buttonType == btnValider) {
                Client c = existant != null ? existant : new Client();
                c.setNom(champNom.getText().trim());
                c.setPrenom(champPrenom.getText().trim());
                c.setTelephone(champTelephone.getText().trim());
                c.setEmail(champEmail.getText().trim());
                return c;
            }
            return null;
        });

        return dialog.showAndWait();
    }

    private String validerSaisie(String nom, String prenom, String telephone, String email) {
        if (nom == null || nom.isBlank() ||
                prenom == null || prenom.isBlank() ||
                telephone == null || telephone.isBlank() ||
                email == null || email.isBlank()) {
            return "Tous les champs marqués d'un astérisque sont obligatoires.";
        }

        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            return "Format d'email invalide.";
        }

        if (!PHONE_PATTERN.matcher(telephone.trim()).matches()) {
            return "Format de téléphone invalide (au moins 6 caractères, chiffres/espaces/+/-).";
        }

        return null;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}