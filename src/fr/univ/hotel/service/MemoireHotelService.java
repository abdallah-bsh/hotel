package fr.univ.hotel.service;

import fr.univ.hotel.metier.Chambre;
import fr.univ.hotel.metier.Client;
import fr.univ.hotel.metier.Reservation;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class MemoireHotelService {

    private static final List<Client> CLIENTS = new ArrayList<>();
    private static final List<Chambre> CHAMBRES = new ArrayList<>();
    private static final List<Reservation> RESERVATIONS = new ArrayList<>();
    private static final AtomicInteger SEQUENCE_RESERVATION = new AtomicInteger(1);

    static {
        CLIENTS.add(new Client(1, "Alice", "Martin", "06054840", "proutiprouta@gmail.com"));
        CLIENTS.add(new Client(2, "Dupont","Martin", "06054840", "proutiprouta@gmail.com"));
        CLIENTS.add(new Client(3, "Leroy","Martin", "06054840", "proutiprouta@gmail.com"));

        CHAMBRES.add(new Chambre(1, 101, "SIMPLE", 60.0, "LIBRE"));
        CHAMBRES.add(new Chambre(2, 102, "DOUBLE", 80.0, "LIBRE"));
        CHAMBRES.add(new Chambre(3, 201, "SUITE", 140.0, "OCCUPEE"));
        CHAMBRES.add(new Chambre(4, 202, "DOUBLE", 90.0, "LIBRE"));
    }

    private MemoireHotelService() {
    }

    public static List<Client> getClients() {
        return Collections.unmodifiableList(CLIENTS);
    }

    public static List<Chambre> getChambres() {
        return Collections.unmodifiableList(CHAMBRES);
    }

    public static List<Chambre> getChambresLibres() {
        return CHAMBRES.stream()
                .filter(c -> "LIBRE".equalsIgnoreCase(c.getStatut()))
                .collect(Collectors.toUnmodifiableList());
    }

    public static List<Reservation> getReservations() {
        return Collections.unmodifiableList(RESERVATIONS);
    }

    public static Reservation ajouterReservation(Client client,
                                                 Chambre chambre,
                                                 LocalDate entree,
                                                 LocalDate sortie) {
        int id = SEQUENCE_RESERVATION.getAndIncrement();
        double prix = calculerPrix(chambre, entree, sortie);
        Reservation reservation = new Reservation(
                id,
                client,
                chambre,
                entree,
                sortie,
                prix,
                "CONFIRMEE"
        );

        RESERVATIONS.add(reservation);
        chambre.setStatut("RESERVEE");
        return reservation;
    }

    public static void supprimerReservation(Reservation reservation) {
        if (reservation == null) {
            return;
        }
        RESERVATIONS.remove(reservation);
        Chambre chambre = reservation.getChambre();
        if (chambre != null && "RESERVEE".equalsIgnoreCase(chambre.getStatut())) {
            chambre.setStatut("LIBRE");
        }
    }

    public static double calculerPrix(Chambre chambre, LocalDate entree, LocalDate sortie) {
        if (chambre == null || entree == null || sortie == null) {
            return 0.0;
        }
        long nuits = Math.max(1, ChronoUnit.DAYS.between(entree, sortie));
        return nuits * chambre.getPrixParNuit();
    }
}