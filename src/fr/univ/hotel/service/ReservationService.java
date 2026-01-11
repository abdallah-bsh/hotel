package fr.univ.hotel.service;

import fr.univ.hotel.dao.ChambreDAO;
import fr.univ.hotel.dao.ClientDAO;
import fr.univ.hotel.dao.ReservationDAO;
import fr.univ.hotel.dao.jdbc.ChambreDAOJdbc;
import fr.univ.hotel.dao.jdbc.ClientDAOJdbc;
import fr.univ.hotel.dao.jdbc.ReservationDAOJdbc;
import fr.univ.hotel.metier.Chambre;
import fr.univ.hotel.metier.Client;
import fr.univ.hotel.metier.Reservation;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class ReservationService {

    private final ReservationDAO reservationDAO = new ReservationDAOJdbc();
    private final ClientDAO clientDAO = new ClientDAOJdbc();
    private final ChambreDAO chambreDAO = new ChambreDAOJdbc();

    public List<Reservation> findAll() {
        return reservationDAO.findAll();
    }

    public List<Client> findClients() {
        return clientDAO.findAll();
    }

    public List<Chambre> findChambresLibres() {
        return chambreDAO.findLibres();
    }

    public Reservation creerReservation(Client client, Chambre chambre, LocalDate entree, LocalDate sortie) {
        if (client == null || chambre == null || entree == null || sortie == null) {
            throw new IllegalArgumentException("Client, chambre et dates sont obligatoires");
        }
        if (!sortie.isAfter(entree)) {
            throw new IllegalArgumentException("La date de sortie doit être postérieure à la date d'entrée");
        }

        Reservation reservation = new Reservation();
        reservation.setClient(client);
        reservation.setChambre(chambre);
        reservation.setDateEntree(entree);
        reservation.setDateSortie(sortie);
        reservation.setPrixTotal(calculerPrix(chambre, entree, sortie));
        reservation.setStatut("CONFIRMEE");

        boolean inserted = reservationDAO.insert(reservation);
        if (!inserted) {
            throw new RuntimeException("Échec de la création de la réservation");
        }

        chambre.setStatut("RESERVEE");
        chambreDAO.update(chambre);
        return reservation;
    }

    public void supprimerReservation(Reservation reservation) {
        if (reservation == null) {
            return;
        }
        reservationDAO.delete(reservation);
        Chambre chambre = reservation.getChambre();
        if (chambre != null) {
            chambre.setStatut("LIBRE");
            chambreDAO.update(chambre);
        }
    }

    private double calculerPrix(Chambre chambre, LocalDate entree, LocalDate sortie) {
        long nuits = Math.max(1, ChronoUnit.DAYS.between(entree, sortie));
        return nuits * chambre.getPrixParNuit();
    }
}