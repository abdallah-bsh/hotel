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
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class FenetreReservationsFX extends BorderPane {

    private final ReservationDAO reservationDAO = new ReservationDAOJdbc();
    private final ClientDAO clientDAO = new ClientDAOJdbc();
    private final ChambreDAO chambreDAO = new ChambreDAOJdbc();

    private final TableView<Reservation> table = new TableView<>();
    private final ObservableList<Reservation> reservations = FXCollections.observableArrayList();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public FenetreReservationsFX() {
        setPadding(new Insets(10));
        setCenter(creerTable());
        setTop(creerBarreBoutons());
        rafraichir();
    }

    private TableView<Reservation> creerTable() {
        table.setItems(reservations);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Reservation, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Reservation, String> colClient = new TableColumn<>("Client");
        colClient.setCellValueFactory(cell -> {
            Client client = cell.getValue().getClient();
            if (client == null) {
                return new SimpleStringProperty("");
            }
            return new SimpleStringProperty(client.getPrenom() + " " + client.getNom());
        });

        TableColumn<Reservation, String> colChambre = new TableColumn<>("Chambre");
        colChambre.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getChambre() != null ? String.valueOf(cell.getValue().getChambre().getNumero()) : ""
        ));

        TableColumn<Reservation, String> colDates = new TableColumn<>("Dates");
        colDates.setCellValueFactory(cell -> {
            Reservation r = cell.getValue();
            if (r.getDateEntree() == null || r.getDateSortie() == null) {
                return new SimpleStringProperty("");
            }
            String txt = formatter.format(r.getDateEntree()) + " → " + formatter.format(r.getDateSortie());
            return new SimpleStringProperty(txt);
        });

        TableColumn<Reservation, Double> colPrix = new TableColumn<>("Prix");
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prixTotal"));

        TableColumn<Reservation, String> colStatut = new TableColumn<>("Statut");
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));

        table.getColumns().addAll(colId, colClient, colChambre, colDates, colPrix, colStatut);
        return table;
    }

    private HBox creerBarreBoutons() {
        Button btnAjouter = new Button("Ajouter");
        Button btnSupprimer = new Button("Supprimer");
        Button btnRafraichir = new Button("Rafraîchir");

        btnAjouter.setOnAction(e -> ouvrirDialogCreation());
        btnSupprimer.setOnAction(e -> supprimerReservation());
        btnRafraichir.setOnAction(e -> rafraichir());

        HBox hBox = new HBox(10, btnAjouter, btnSupprimer, btnRafraichir);
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(10));
        return hBox;
    }

    private void rafraichir() {
        reservations.setAll(reservationDAO.findAll());
        table.refresh();
    }

    private void ouvrirDialogCreation() {
        List<Client> clients = clientDAO.findAll();
        List<Chambre> chambres = chambreDAO.findLibres();

        if (clients.isEmpty()) {
            afficherErreur("Aucun client disponible. Ajoutez un client avant de réserver.");
            return;
        }
        if (chambres.isEmpty()) {
            afficherErreur("Aucune chambre libre disponible pour le moment.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Nouvelle réservation");
        dialog.setHeaderText("Créer une nouvelle réservation");

        ComboBox<Client> comboClient = new ComboBox<>();
        comboClient.getItems().addAll(clients);
        comboClient.setPrefWidth(250);
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

        ComboBox<Chambre> comboChambre = new ComboBox<>();
        comboChambre.getItems().addAll(chambres);
        comboChambre.setPrefWidth(250);
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

        DatePicker dateEntree = new DatePicker(LocalDate.now());
        DatePicker dateSortie = new DatePicker(LocalDate.now().plusDays(1));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        grid.add(new Label("Client :"), 0, 0);
        grid.add(comboClient, 1, 0);
        grid.add(new Label("Chambre :"), 0, 1);
        grid.add(comboChambre, 1, 1);
        grid.add(new Label("Date entrée :"), 0, 2);
        grid.add(dateEntree, 1, 2);
        grid.add(new Label("Date sortie :"), 0, 3);
        grid.add(dateSortie, 1, 3);

        ButtonType btnValider = new ButtonType("Valider", ButtonType.OK.getButtonData());
        dialog.getDialogPane().getButtonTypes().addAll(btnValider, ButtonType.CANCEL);
        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(result -> {
            if (result == btnValider) {
                validerCreation(comboClient.getValue(), comboChambre.getValue(), dateEntree.getValue(), dateSortie.getValue());
            }
        });
    }

    private void validerCreation(Client client, Chambre chambre, LocalDate entree, LocalDate sortie) {
        if (client == null || chambre == null) {
            afficherErreur("Veuillez sélectionner un client et une chambre libre.");
            return;
        }
        if (entree == null || sortie == null) {
            afficherErreur("Veuillez renseigner des dates d'entrée et de sortie.");
            return;
        }
        if (!sortie.isAfter(entree)) {
            afficherErreur("La date de sortie doit être postérieure à la date d'entrée.");
            return;
        }

        Reservation reservation = new Reservation();
        reservation.setClient(client);
        reservation.setChambre(chambre);
        reservation.setDateEntree(entree);
        reservation.setDateSortie(sortie);
        reservation.setPrixTotal(calculerPrix(chambre, entree, sortie));
        reservation.setStatut("CONFIRMEE");

        boolean ok = reservationDAO.insert(reservation);
        if (ok) {
            rafraichir();
        } else {
            afficherErreur("Impossible d'enregistrer la réservation.");
        }
    }

    private void supprimerReservation() {
        Reservation selection = table.getSelectionModel().getSelectedItem();
        if (selection == null) {
            afficherErreur("Veuillez sélectionner une réservation à supprimer.");
            return;
        }

        boolean ok = reservationDAO.delete(selection);
        if (ok) {
            rafraichir();
        } else {
            afficherErreur("Impossible de supprimer la réservation.");
        }
    }

    private void afficherErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private double calculerPrix(Chambre chambre, LocalDate entree, LocalDate sortie) {
        if (chambre == null || entree == null || sortie == null) {
            return 0.0;
        }
        long nuits = Math.max(1, ChronoUnit.DAYS.between(entree, sortie));
        return nuits * chambre.getPrixParNuit();
    }
}