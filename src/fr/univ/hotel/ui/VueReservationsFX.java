package fr.univ.hotel.ui;

import fr.univ.hotel.dao.ChambreDAO;
import fr.univ.hotel.dao.ClientDAO;
import fr.univ.hotel.dao.ReservationDAO;
import fr.univ.hotel.dao.jdbc.ChambreDAOJdbc;
import fr.univ.hotel.dao.jdbc.ClientDAOJdbc;
import fr.univ.hotel.dao.jdbc.ReservationDAOJdbc;
import fr.univ.hotel.metier.Chambre;
import fr.univ.hotel.metier.Client;
import fr.univ.hotel.metier.Reservation;
import fr.univ.hotel.util.DialogUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

public class VueReservationsFX extends BorderPane {

    private final ReservationDAO reservationDAO = new ReservationDAOJdbc();
    private final ClientDAO clientDAO = new ClientDAOJdbc();
    private final ChambreDAO chambreDAO = new ChambreDAOJdbc();

    private final ObservableList<Reservation> reservations = FXCollections.observableArrayList();
    private final TableView<Reservation> tableView = new TableView<>();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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

    private TableView<Reservation> buildTable() {
        tableView.setItems(reservations);
        tableView.setPlaceholder(new Label("Aucune réservation"));
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Reservation, String> colClient = new TableColumn<>("Client");
        colClient.setCellValueFactory(data -> {
            Client client = data.getValue().getClient();
            String nomPrenom = client == null ? "" : client.getPrenom() + " " + client.getNom();
            return new SimpleStringProperty(nomPrenom.trim());
        });

        TableColumn<Reservation, String> colChambre = new TableColumn<>("Chambre");
        colChambre.setCellValueFactory(data -> {
            Chambre chambre = data.getValue().getChambre();
            return new SimpleStringProperty(chambre == null ? "" : String.valueOf(chambre.getNumero()));
        });

        TableColumn<Reservation, String> colDates = new TableColumn<>("Dates");
        colDates.setCellValueFactory(data -> {
            Reservation res = data.getValue();
            LocalDate entree = res.getDateEntree();
            LocalDate sortie = res.getDateSortie();
            if (entree == null || sortie == null) {
                return new SimpleStringProperty("");
            }
            String label = formatter.format(entree) + " -> " + formatter.format(sortie);
            return new SimpleStringProperty(label);
        });

        tableView.getColumns().addAll(colClient, colChambre, colDates);
        return tableView;
    }

    private void chargerReservations() {
        try {
            reservations.setAll(reservationDAO.findAll());
        } catch (Exception e) {
            DialogUtils.showError("Erreur", "Impossible de charger les réservations : " + e.getMessage());
        }
    }

    private void ajouterReservation() {
        try {
            List<Client> clients = clientDAO.findAll();
            List<Chambre> chambresLibres = chambreDAO.findLibres();

            if (clients.isEmpty()) {
                DialogUtils.showError("Données manquantes", "Aucun client dans la base.");
                return;
            }
            if (chambresLibres.isEmpty()) {
                DialogUtils.showError("Données manquantes", "Aucune chambre libre disponible.");
                return;
            }

            Dialog<Reservation> dialog = new Dialog<>();
            dialog.setTitle("Nouvelle réservation");
            dialog.setHeaderText("Créer une réservation");

            ButtonType btnValider = new ButtonType("Valider", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(btnValider, ButtonType.CANCEL);

            ComboBox<Client> comboClient = new ComboBox<>(FXCollections.observableArrayList(clients));
            comboClient.setConverter(new StringConverter<>() {
                @Override
                public String toString(Client client) {
                    return client == null ? "" : client.getPrenom() + " " + client.getNom();
                }

                @Override
                public Client fromString(String string) {
                    return null;
                }
            });
            comboClient.setCellFactory(list -> new ListCell<>() {
                @Override
                protected void updateItem(Client item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "" : item.getPrenom() + " " + item.getNom());
                }
            });
            comboClient.setButtonCell(comboClient.getCellFactory().call(null));
            comboClient.getSelectionModel().selectFirst();

            ComboBox<Chambre> comboChambre = new ComboBox<>(FXCollections.observableArrayList(chambresLibres));
            comboChambre.setConverter(new StringConverter<>() {
                @Override
                public String toString(Chambre chambre) {
                    if (chambre == null) return "";
                    return "N°" + chambre.getNumero() + " (" + chambre.getType() + ")";
                }

                @Override
                public Chambre fromString(String string) {
                    return null;
                }
            });
            comboChambre.setCellFactory(list -> new ListCell<>() {
                @Override
                protected void updateItem(Chambre item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText("");
                    } else {
                        setText("N°" + item.getNumero() + " (" + item.getType() + ")");
                    }
                }
            });
            comboChambre.setButtonCell(comboChambre.getCellFactory().call(null));
            comboChambre.getSelectionModel().selectFirst();

            DatePicker entree = new DatePicker(LocalDate.now());
            DatePicker sortie = new DatePicker(LocalDate.now().plusDays(1));

            GridPane grille = new GridPane();
            grille.setHgap(10);
            grille.setVgap(10);
            grille.setPadding(new Insets(10, 0, 0, 0));
            grille.add(new Label("Client"), 0, 0);
            grille.add(comboClient, 1, 0);
            grille.add(new Label("Chambre"), 0, 1);
            grille.add(comboChambre, 1, 1);
            grille.add(new Label("Entrée"), 0, 2);
            grille.add(entree, 1, 2);
            grille.add(new Label("Sortie"), 0, 3);
            grille.add(sortie, 1, 3);

            dialog.getDialogPane().setContent(grille);

            Button boutonValider = (Button) dialog.getDialogPane().lookupButton(btnValider);
            boutonValider.addEventFilter(javafx.event.ActionEvent.ACTION, evt -> {
                if (!validerDates(entree.getValue(), sortie.getValue())) {
                    evt.consume();
                    DialogUtils.showError("Validation", "La date de sortie doit être postérieure à la date d'entrée.");
                }
            });

            dialog.setResultConverter(type -> {
                if (type == btnValider) {
                    Reservation reservation = new Reservation();
                    reservation.setClient(comboClient.getValue());
                    reservation.setChambre(comboChambre.getValue());
                    reservation.setDateEntree(entree.getValue());
                    reservation.setDateSortie(sortie.getValue());
                    reservation.setPrixTotal(calculerPrix(comboChambre.getValue(), entree.getValue(), sortie.getValue()));
                    reservation.setStatut("CONFIRMEE");
                    return reservation;
                }
                return null;
            });

            Optional<Reservation> resultat = dialog.showAndWait();
            if (resultat.isPresent()) {
                boolean ok = reservationDAO.insert(resultat.get());
                if (ok) {
                    chargerReservations();
                } else {
                    DialogUtils.showError("Erreur", "Impossible d'enregistrer la réservation.");
                }
            }
        } catch (Exception e) {
            DialogUtils.showError("Erreur", "Erreur lors de l'ajout de la réservation : " + e.getMessage());
        }
    }

    private void supprimerReservation() {
        Reservation selection = tableView.getSelectionModel().getSelectedItem();
        if (selection == null) {
            DialogUtils.showError("Suppression", "Veuillez sélectionner une réservation à supprimer.");
            return;
        }

        boolean confirme = DialogUtils.showConfirmation(
                "Confirmation",
                "Supprimer la réservation de " + selection.getClient().getPrenom() + " " + selection.getClient().getNom() + " ?"
        );
        if (!confirme) {
            return;
        }

        try {
            boolean ok = reservationDAO.delete(selection);
            if (ok) {
                chargerReservations();
            } else {
                DialogUtils.showError("Erreur", "La réservation n'a pas pu être supprimée.");
            }
        } catch (Exception e) {
            DialogUtils.showError("Erreur", "Erreur lors de la suppression : " + e.getMessage());
        }
    }

    private boolean validerDates(LocalDate entree, LocalDate sortie) {
        return entree != null && sortie != null && sortie.isAfter(entree);
    }

    private double calculerPrix(Chambre chambre, LocalDate entree, LocalDate sortie) {
        if (chambre == null || entree == null || sortie == null) {
            return 0.0;
        }
        long nuits = Math.max(1, ChronoUnit.DAYS.between(entree, sortie));
        return nuits * chambre.getPrixParNuit();
    }
}